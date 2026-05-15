package xis.crm.employee;

import lombok.Getter;
import lombok.Setter;
import one.xis.validation.LabelKey;
import one.xis.validation.Mandatory;
import one.xis.validation.MinLength;
import one.xis.validation.RegExpr;

@Getter
@Setter
public class Employee {
    @Mandatory
    @RegExpr("[a-zA-Z0-9._-]{3,40}")
    @LabelKey("employee.userId")
    private String userId;
    @Mandatory
    @MinLength(2)
    @LabelKey("employee.name")
    private String name;
    @Mandatory
    @MinLength(6)
    @LabelKey("employee.password")
    private String password = "";
    @Mandatory
    @LabelKey("employee.role")
    private String role = "SALES";
    private boolean active = true;

    public String name() {
        return name;
    }

    public String role() {
        return role;
    }
}
