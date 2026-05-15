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
            select user_id, preferred_username, name, password, role, active
            from employees
            where user_id = {userId} and active = true
            """)
    Optional<EmployeeEntity> findActiveByUserId(@Param("userId") String userId);

    @Select("""
            select user_id, preferred_username, name, password, role, active
            from employees
            where (user_id = {value} or preferred_username = {value}) and active = true
            """)
    Optional<EmployeeEntity> findActiveByUserIdOrPreferredUsername(@Param("value") String value);

    @Select("""
            select user_id, preferred_username, name, password, role, active
            from employees
            order by role, name
            """)
    List<EmployeeEntity> findEmployees();

    @Select("""
            select user_id, preferred_username, name, password, role, active
            from employees
            where user_id = {userId}
            """)
    EmployeeEntity findEmployee(@Param("userId") String userId);

    @Insert("""
            insert into employees (user_id, preferred_username, name, password, role, active)
            values ({userId}, {userId}, {name}, {password}, {role}, true)
            """)
    int createEmployee(@Param("userId") String userId,
                       @Param("name") String name,
                       @Param("password") String password,
                       @Param("role") String role);
}
