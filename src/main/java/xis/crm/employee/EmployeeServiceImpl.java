package xis.crm.employee;

import lombok.RequiredArgsConstructor;
import one.xis.context.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    @Override
    public boolean validateCredentials(String userId, String password) {
        return repository.findActiveByUserIdOrPreferredUsername(userId)
                .filter(employee -> employee.getPassword().equals(password))
                .isPresent();
    }

    @Override
    public Optional<EmployeeEntity> getUserInfo(String userId) {
        return repository.findActiveByUserIdOrPreferredUsername(userId);
    }

    @Override
    public void saveUserInfo(EmployeeEntity employee) {
        throw new UnsupportedOperationException("External identity providers are not used by this demo.");
    }

    @Override
    public List<Employee> employees() {
        return repository.findEmployees().stream().map(this::toEmployee).toList();
    }

    @Override
    public Employee employee(String userId) {
        return toEmployee(repository.findEmployee(userId));
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
        if (employee.getUserId() == null || employee.getUserId().isBlank()) {
            employee.setUserId(UUID.randomUUID().toString());
        }
        repository.createEmployee(employee.getUserId(), employee.getName(),
                employee.getPassword(), employee.getRole());
    }

    private Employee toEmployee(EmployeeEntity entity) {
        var employee = new Employee();
        employee.setUserId(entity.getUserId());
        employee.setName(entity.getName());
        employee.setPassword(entity.getPassword());
        employee.setRole(entity.getRole());
        employee.setActive(entity.isActive());
        return employee;
    }
}
