package xis.crm.customer;

import lombok.Data;
import one.xis.validation.EMail;
import one.xis.validation.LabelKey;
import one.xis.validation.Mandatory;
import one.xis.validation.MinLength;
import one.xis.validation.RegExpr;

@Data
public class CustomerFormObject {
    private long id;
    @Mandatory
    @MinLength(2)
    @LabelKey("customer.name")
    private String name;
    @Mandatory
    @LabelKey("customer.segment")
    private String segment;
    @Mandatory
    @LabelKey("customer.city")
    private String city;
    @Mandatory
    @EMail
    @LabelKey("customer.email")
    private String email;
    @RegExpr("(|[+0-9][0-9 ()/-]{5,24})")
    @LabelKey("customer.phone")
    private String phone;
    @Mandatory
    @LabelKey("customer.stage")
    private CustomerStage stage;
    private int revenue;
    @Mandatory
    @LabelKey("customer.owner")
    private String ownerId;
    private String notes;
}
