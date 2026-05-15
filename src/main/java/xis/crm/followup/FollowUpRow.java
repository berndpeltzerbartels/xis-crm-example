package xis.crm.followup;

import lombok.Getter;
import lombok.Setter;
import one.xis.sql.Entity;

import java.time.Instant;

@Entity(value = "follow_ups", allowUnmappedFields = true)
@Getter
@Setter
class FollowUpRow {
    private long id;
    private long customerId;
    private String employeeId;
    private String employeeName;
    private Instant dueDate;
    private String task;
    private boolean done;
}
