package xis.crm.employee;

import lombok.RequiredArgsConstructor;
import one.xis.Action;
import one.xis.FormData;
import one.xis.Modal;
import one.xis.ModalResponse;
import one.xis.Roles;

@Modal("/employees/new")
@Roles("ADMIN")
@RequiredArgsConstructor
public class EmployeeModal {
    private final EmployeeService employees;

    @FormData("employee")
    Employee employee() {
        return employees.newEmployee();
    }

    @Action
    @Roles("ADMIN")
    ModalResponse save(@FormData("employee") Employee employee) {
        employees.createEmployee(employee);
        return ModalResponse.close().reloadParent();
    }

    @Action
    ModalResponse cancel() {
        return ModalResponse.close();
    }
}
