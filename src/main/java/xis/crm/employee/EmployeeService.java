package xis.crm.employee;

import one.xis.auth.UserInfoService;
import one.xis.sql.Transactional;

import java.util.List;

public interface EmployeeService extends UserInfoService<EmployeeEntity> {
    List<Employee> employees();

    Employee employee(String userId);

    Employee newEmployee();

    @Transactional
    void createEmployee(Employee employee);
}
