package xis.crm.employee;

import lombok.RequiredArgsConstructor;
import one.xis.context.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    @Override
    public boolean validateCredentials(String userId, String password) {
        return repository.findActiveByUsername(userId)
                .filter(employee -> employee.getPassword().equals(password))
                .isPresent();
    }

    @Override
    public Optional<Employee> getUserInfo(String userId) {
        return repository.findActiveByUsername(userId);
    }

    @Override
    public void saveUserInfo(Employee employee) {
        throw new UnsupportedOperationException("External identity providers are not used by this demo.");
    }

    @Override
    public List<Employee> employees() {
        return repository.findEmployees();
    }

    @Override
    public Employee employee(long id) {
        return repository.findEmployee(id);
    }

    @Override
    public Employee newEmployee() {
        var employee = new Employee();
        employee.setRole("SALES");
        employee.setActive(true);
        return employee;
    }

    @Override
    public void createEmployee(Employee employee) {
        employee.setActive(true);
        repository.createEmployee(employee.getName(), employee.getUsername(), employee.getPassword(), employee.getRole());
    }
}
