package xis.crm.reminder;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Reminder {
    private long id;
    private long followUpId;
    private String employeeId;
    private LocalDateTime dueDate;
    private String customerName;
    private String task;
    private String nextReminderAt;
}
