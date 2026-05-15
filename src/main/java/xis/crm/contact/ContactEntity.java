package xis.crm.contact;

import lombok.Getter;
import lombok.Setter;
import one.xis.sql.Entity;

@Entity("contacts")
@Getter
@Setter
public class ContactEntity {
    private long id;
    private long customerId;
    private String employeeId;
    private String contactDate = "";
    private String channel = "Call";
    private String description = "";
}
