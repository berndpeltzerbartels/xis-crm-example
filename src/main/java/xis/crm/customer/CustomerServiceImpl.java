package xis.crm.customer;

import lombok.RequiredArgsConstructor;
import one.xis.context.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customers;

    @Override
    public List<Customer> customers() {
        return customers.findAll().stream().map(this::toCustomer).toList();
    }

    @Override
    public Customer customer(long id) {
        return toCustomer(customerEntity(id));
    }

    @Override
    public CustomerFormObject customerFormObject(long id) {
        return toCustomerFormObject(customerEntity(id));
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

    Customer toCustomer(CustomerEntity entity) {
        var customer = new Customer();
        customer.setId(entity.getId());
        customer.setName(entity.getName());
        customer.setSegment(entity.getSegment());
        customer.setCity(entity.getCity());
        customer.setEmail(entity.getEmail());
        customer.setPhone(entity.getPhone());
        customer.setStage(entity.getStage());
        customer.setRevenue(entity.getRevenue());
        customer.setOwnerId(entity.getOwnerId());
        customer.setNotes(entity.getNotes());
        return customer;
    }

    private CustomerFormObject toCustomerFormObject(CustomerEntity entity) {
        var customer = new CustomerFormObject();
        customer.setId(entity.getId());
        customer.setName(entity.getName());
        customer.setSegment(entity.getSegment());
        customer.setCity(entity.getCity());
        customer.setEmail(entity.getEmail());
        customer.setPhone(entity.getPhone());
        customer.setStage(entity.getStage());
        customer.setRevenue(entity.getRevenue());
        customer.setOwnerId(entity.getOwnerId());
        customer.setNotes(entity.getNotes());
        return customer;
    }
}
