package xis.crm.employee;

import one.xis.sql.Entity;
import one.xis.auth.UserInfoImpl;

import java.util.Set;

@Entity(value = "employees", allowUnmappedFields = true)
public class Employee extends UserInfoImpl {
    private long id;
    private String password = "";
    private String role = "SALES";
    private boolean active = true;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return getUserId();
    }

    public void setUsername(String username) {
        setUserId(username);
        setPreferredUsername(username);
    }

    @Override
    public String getUserId() {
        return super.getUserId();
    }

    @Override
    public void setUserId(String userId) {
        super.setUserId(userId);
        super.setPreferredUsername(userId);
    }

    @Override
    public Set<String> getRoles() {
        return Set.of(role);
    }

    @Override
    public void setRoles(Set<String> roles) {
        this.role = roles == null || roles.isEmpty() ? "SALES" : roles.iterator().next();
        super.setRoles(roles);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        super.setRoles(Set.of(role));
    }

    public String role() {
        return role;
    }

    public String username() {
        return getUsername();
    }

    public String name() {
        return getName();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
