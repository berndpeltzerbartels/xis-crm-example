package xis.crm.customer;

import lombok.RequiredArgsConstructor;
import one.xis.Action;
import one.xis.Authenticated;
import one.xis.FormData;
import one.xis.ModelData;
import one.xis.Modal;
import one.xis.ModalResponse;
import one.xis.Parameter;
import xis.crm.employee.Employee;
import xis.crm.employee.EmployeeService;

import java.util.List;

@Modal("/customers/edit")
@Authenticated
@RequiredArgsConstructor
class EditCustomerModal {
    private final CustomerService customers;
    private final EmployeeService employees;

    @FormData("customer")
    CustomerFormObject customer(@Parameter("customerId") long customerId) {
        return CustomerFormObject.from(customers.customerEntity(customerId));
    }

    @ModelData
    List<Employee> owners() {
        return employees.employees();
    }

    @Action
    ModalResponse save(@FormData("customer") CustomerFormObject customer) {
        customers.updateCustomer(customer);
        return ModalResponse.close().reloadParent();
    }

    @Action
    ModalResponse cancel() {
        return ModalResponse.close();
    }
}
