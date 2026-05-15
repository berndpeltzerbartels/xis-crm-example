package xis.crm.followup;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowUp {
    private long id;
    private long customerId;
    private String employeeId;
    private String employeeName;
    private LocalDateTime dueDate;
    private String task = "";
    private boolean done;
}
