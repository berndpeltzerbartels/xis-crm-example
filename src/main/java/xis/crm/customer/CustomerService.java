package xis.crm.customer;

import one.xis.sql.Transactional;

import java.util.List;

public interface CustomerService {
    List<Customer> customers();
    
    Customer customer(long id);

    CustomerFormObject customerFormObject(long id);

    CustomerEntity customerEntity(long id);

    @Transactional
    void updateCustomer(CustomerFormObject customer);
}
