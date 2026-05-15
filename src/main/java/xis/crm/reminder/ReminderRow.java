package xis.crm.reminder;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
class ReminderRow {
    private long id;
    private long followUpId;
    private String employeeId;
    private Instant dueDate;
    private String customerName;
    private String task;
    private String nextReminderAt;
}
