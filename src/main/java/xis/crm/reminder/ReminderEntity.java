package xis.crm.reminder;

import lombok.Getter;
import lombok.Setter;
import one.xis.sql.Entity;

import java.time.Instant;

@Getter
@Setter
@Entity("follow_up_reminders")
class ReminderEntity {
    private long id;
    private long followUpId;
    private String employeeId;
    private Instant dueDate;
    private boolean sent;
    private boolean done;
}

