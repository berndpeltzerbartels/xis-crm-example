package xis.crm.employee;

import one.xis.auth.UserInfoService;
import one.xis.sql.Transactional;

import java.util.List;

public interface EmployeeService extends UserInfoService<Employee> {
    List<Employee> employees();

    Employee employee(long id);

    Employee newEmployee();

    @Transactional
    void createEmployee(Employee employee);
}
