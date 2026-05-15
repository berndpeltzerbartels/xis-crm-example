package xis.crm.followup;

import lombok.Data;
import one.xis.validation.LabelKey;
import one.xis.validation.Mandatory;
import one.xis.validation.MinLength;

@Data
public class FollowUpFormObject {
    private long customerId;
    @Mandatory
    @LabelKey("followUp.dueDate")
    private String dueDate;
    @Mandatory
    @MinLength(6)
    @LabelKey("followUp.task")
    private String task = "";
}
