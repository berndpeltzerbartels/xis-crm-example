package xis.crm;

import one.xis.RefreshEvent;
import one.xis.RefreshEventPublisher;
import one.xis.context.AppContext;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xis.crm.customer.CustomerRepository;
import xis.crm.contact.ContactService;
import xis.crm.employee.EmployeeService;
import xis.crm.employee.EmployeeServiceImpl;
import xis.crm.followup.FollowUpFormObject;
import xis.crm.followup.FollowUpService;
import xis.crm.reminder.ReminderService;

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
        EmployeeService employees = context.getSingleton(EmployeeService.class);

        var employee = employees.employee("mara");

        assertEquals("mara", employee.getUserId());
        assertEquals("Mara Stein", employee.getName());
        assertEquals("demo", employee.getPassword());
        assertEquals("SALES", employee.getRole());
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
        ContactService contacts = context.getSingleton(ContactService.class);
        FollowUpService followUps = context.getSingleton(FollowUpService.class);

        var contact = contacts.contacts(1).get(0);
        var followUp = followUps.followUps(1).get(0);

        assertEquals(1L, contact.getCustomerId());
        assertFalse(contact.getContactDate().isBlank());
        assertFalse(contact.getEmployeeName().isBlank());
        assertEquals(1L, followUp.getCustomerId());
        assertNotNull(followUp.getDueDate());
        assertFalse(followUp.getTask().isBlank());
    }

    @Test
    void createsFollowUpWithReminder() {
        FollowUpService followUps = context.getSingleton(FollowUpService.class);
        ReminderService reminders = context.getSingleton(ReminderService.class);

        var followUp = new FollowUpFormObject();
        followUp.setCustomerId(1);
        followUp.setDueDate("2026-05-20T10:30");
        followUp.setTask("Send architecture note");

        followUps.addFollowUp(followUp, "mara");

        assertTrue(followUps.followUps(1).stream().anyMatch(item -> item.getTask().equals("Send architecture note")));
        assertTrue(reminders.reminders("mara").stream()
                .anyMatch(item -> item.getTask().equals("Send architecture note")));
    }

    static class NoopRefreshEventPublisher implements RefreshEventPublisher {
        @Override
        public void publish(RefreshEvent refreshEvent) {
        }
    }
}
