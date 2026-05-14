package xis.crm.customer;

import one.xis.sql.Transactional;

import java.util.List;

public interface CustomerService {
    List<Customer> customers();

    CustomerDetail customerDetail(long id);

    Customer customer(long id);

    CustomerEntity customerEntity(long id);

    @Transactional
    void updateCustomer(CustomerFormObject customer);
}
