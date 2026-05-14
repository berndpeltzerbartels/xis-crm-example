package xis.crm.employee;

import one.xis.sql.Insert;
import one.xis.sql.Param;
import one.xis.sql.Repository;
import one.xis.sql.Select;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository {

    @Select("""
            select id, username as user_id, name, password, role, active
            from employees
            where username = {username} and active = true
            """)
    Optional<Employee> findActiveByUsername(@Param("username") String username);

    @Select("""
            select id, username as user_id, name, password, role, active
            from employees
            order by role, name
            """)
    List<Employee> findEmployees();

    @Select("""
            select id, username as user_id, name, password, role, active
            from employees
            where id = {id}
            """)
    Employee findEmployee(@Param("id") long id);

    @Insert("""
            insert into employees (name, username, password, role, active)
            values ({name}, {username}, {password}, {role}, true)
            """)
    int createEmployee(@Param("name") String name,
                       @Param("username") String username,
                       @Param("password") String password,
                       @Param("role") String role);
}
