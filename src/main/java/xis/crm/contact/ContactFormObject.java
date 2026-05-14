package xis.crm.contact;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactFormObject {
    private long customerId;
    private String contactDate = "";
    private String channel = "Call";
    private String description = "";
}
