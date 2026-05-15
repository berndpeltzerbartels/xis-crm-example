package xis.crm.contact;

import lombok.Getter;
import lombok.Setter;
import one.xis.validation.LabelKey;
import one.xis.validation.Mandatory;
import one.xis.validation.MinLength;

@Getter
@Setter
public class ContactFormObject {
    private long customerId;
    @Mandatory
    @LabelKey("contact.date")
    private String contactDate = "";
    @Mandatory
    @LabelKey("contact.channel")
    private String channel = "Call";
    @Mandatory
    @MinLength(8)
    @LabelKey("contact.description")
    private String description = "";
}
