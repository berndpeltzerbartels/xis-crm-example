package xis.crm.customer;

import lombok.RequiredArgsConstructor;
import one.xis.context.Component;
import xis.crm.contact.ContactRepository;
import xis.crm.employee.EmployeeService;
import xis.crm.followup.FollowUpRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customers;
    private final ContactRepository contacts;
    private final EmployeeService employees;
    private final FollowUpRepository followUps;

    @Override
    public List<Customer> customers() {
        return customers.findCustomerSummaries();
    }

    @Override
    public CustomerDetail customerDetail(long id) {
        Customer customer = customer(id);
        return new CustomerDetail(customer, customer.getEmail(), customer.getPhone(), customer.getNotes(),
                contacts.findByCustomer(id));
    }

    @Override
    public Customer customer(long id) {
        return toCustomer(customerEntity(id));
    }

    @Override
    public CustomerEntity customerEntity(long id) {
        return customers.findById(id).orElseThrow();
    }

    @Override
    public void updateCustomer(CustomerFormObject customer) {
        CustomerEntity entity = customers.findById(customer.getId()).orElseThrow();
        entity.setName(customer.getName());
        entity.setSegment(customer.getSegment());
        entity.setCity(customer.getCity());
        entity.setEmail(customer.getEmail());
        entity.setPhone(customer.getPhone());
        entity.setStage(customer.getStage());
        entity.setRevenue(customer.getRevenue());
        entity.setOwnerId(customer.getOwnerId());
        entity.setNotes(customer.getNotes());
        customers.save(entity);
    }

    private Customer toCustomer(CustomerEntity entity) {
        Customer customer = entity.toCustomer();
        customer.setOwnerName(employees.employee(entity.getOwnerId()).getName());
        var followUpsForCustomer = followUps.findByCustomer(entity.getId());
        customer.setOpenTasks((int) followUpsForCustomer.stream()
                .filter(followUp -> !followUp.isDone())
                .count());
        customer.setNextReminder(followUpsForCustomer.stream()
                .filter(followUp -> !followUp.isDone())
                .map(followUp -> followUp.getDueDate())
                .findFirst()
                .orElse("none"));
        return customer;
    }
}
