package xis.crm;

import one.xis.context.AppContext;
import one.xis.RefreshEvent;
import one.xis.RefreshEventPublisher;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xis.crm.contact.ContactEntry;
import xis.crm.contact.ContactRepository;
import xis.crm.customer.CustomerRepository;
import xis.crm.employee.Employee;
import xis.crm.employee.EmployeeRepository;
import xis.crm.employee.EmployeeServiceImpl;
import xis.crm.followup.FollowUp;
import xis.crm.followup.FollowUpRepository;
import xis.crm.reminder.ReminderRepository;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryMappingTest {
    private AppContext context;

    @BeforeEach
    void setUp() throws SQLException {
        var dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:crm-repository-mapping;DB_CLOSE_DELAY=-1");
        new DatabaseInitializer(dataSource).initialize();
        context = AppContext.builder()
                .withSingleton(dataSource)
                .withSingleton((RefreshEventPublisher) new NoopRefreshEventPublisher())
                .withSingletonClass(EmployeeServiceImpl.class)
                .withPackages("one.xis.sql",
                        "xis.crm.customer",
                        "xis.crm.contact",
                        "xis.crm.employee",
                        "xis.crm.followup",
                        "xis.crm.reminder")
                .build();
    }

    @Test
    void mapsEmployeesToUserInfo() {
        EmployeeRepository employees = context.getSingleton(EmployeeRepository.class);

        Employee employee = employees.findActiveByUsername("mara").orElseThrow();

        assertEquals("mara", employee.getUserId());
        assertEquals("mara", employee.getUsername());
        assertEquals("Mara Stein", employee.getName());
        assertEquals("demo", employee.getPassword());
        assertEquals("SALES", employee.getRole());
        assertTrue(employee.getRoles().contains("SALES"));
        assertTrue(employee.isActive());
    }

    @Test
    void mapsCustomerOverviewAndDetails() {
        CustomerRepository customers = context.getSingleton(CustomerRepository.class);

        var customer = customers.findById(1L).orElseThrow();

        assertEquals("Nordlicht Logistics", customer.getName());
        assertEquals("Enterprise", customer.getSegment());
        assertEquals("Hamburg", customer.getCity());
        assertEquals(180000, customer.getRevenue());
    }

    @Test
    void mapsContactsAndFollowUps() {
        ContactRepository contacts = context.getSingleton(ContactRepository.class);
        FollowUpRepository followUps = context.getSingleton(FollowUpRepository.class);

        ContactEntry contact = contacts.findByCustomer(1).get(0);
        FollowUp followUp = followUps.findByCustomer(1).get(0);

        assertEquals(1L, contact.getCustomerId());
        assertFalse(contact.getContactDate().isBlank());
        assertFalse(contact.getEmployeeName().isBlank());
        assertEquals(1L, followUp.getCustomerId());
        assertFalse(followUp.getDueDate().isBlank());
        assertFalse(followUp.getTask().isBlank());
    }

    @Test
    void createsFollowUpWithReminder() {
        FollowUpRepository followUps = context.getSingleton(FollowUpRepository.class);
        ReminderRepository reminders = context.getSingleton(ReminderRepository.class);

        var followUp = new FollowUp();
        followUp.setCustomerId(1);
        followUp.setEmployeeId(2);
        followUp.setDueDate("2020-01-01");
        followUp.setTask("Send architecture note");
        followUp.setReminder("08:30");

        long id = followUps.createFollowUp(followUp);

        assertTrue(followUps.findByCustomer(1).stream().anyMatch(item -> item.getId() == id));
        assertTrue(reminders.findForUser("mara").stream()
                .anyMatch(item -> item.followUpId() == id && item.reminder().equals("08:30")));
    }

    static class NoopRefreshEventPublisher implements RefreshEventPublisher {
        @Override
        public void publish(RefreshEvent refreshEvent) {
        }
    }
}
