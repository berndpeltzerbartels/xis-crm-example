package xis.crm.employee;

import lombok.Getter;
import lombok.Setter;
import one.xis.auth.UserInfoImpl;
import one.xis.sql.Entity;

import java.util.Set;

@Getter
@Setter
@Entity(value = "employees", allowUnmappedFields = true)
public class EmployeeEntity extends UserInfoImpl {
    private String password = "";
    private String role = "SALES";
    private boolean active = true;

    @Override
    public Set<String> getRoles() {
        return Set.of(role);
    }

    @Override
    public void setRoles(Set<String> roles) {
        this.role = roles == null || roles.isEmpty() ? "SALES" : roles.iterator().next();
        super.setRoles(roles);
    }

    public void setRole(String role) {
        this.role = role;
        super.setRoles(Set.of(role));
    }
}
