package xis.crm.contact;

import lombok.Data;

@Data
public class Contact {
    private long id;
    private long customerId;
    private String employeeId;
    private String contactDate;
    private String employeeName;
    private String channel;
    private String description;
}
