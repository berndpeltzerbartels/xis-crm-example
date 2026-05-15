package xis.crm.followup;

import lombok.Getter;
import lombok.Setter;
import one.xis.sql.Entity;

import java.time.Instant;

@Getter
@Setter
@Entity("follow_ups")
class FollowUpEntity {
    private long id;
    private long customerId;
    private String employeeId;
    private Instant dueDate;
    private String task;
    private boolean done;
}
