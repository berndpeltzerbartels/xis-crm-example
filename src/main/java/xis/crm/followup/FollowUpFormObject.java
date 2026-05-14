package xis.crm.followup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowUpFormObject {
    private long customerId;
    private String dueDate = "";
    private String task = "";
    private String reminder = "09:00";
}
