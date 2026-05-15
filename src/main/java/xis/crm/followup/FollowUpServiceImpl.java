package xis.crm.followup;

import lombok.RequiredArgsConstructor;
import one.xis.context.Component;
import xis.crm.employee.EmployeeService;
import xis.crm.reminder.ReminderService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FollowUpServiceImpl implements FollowUpService {

    private final FollowUpRepository followUps;
    private final EmployeeService employees;
    private final ReminderService reminders;

    @Override
    public FollowUpFormObject newFollowUp(long customerId) {
        var followUp = new FollowUpFormObject();
        followUp.setCustomerId(customerId);
        followUp.setDueDate(LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        followUp.setTask("Send follow-up");
        return followUp;
    }

    @Override
    public List<FollowUp> followUps(long customerId) {
        return followUps.findByCustomer(customerId).stream().map(this::toFollowUp).toList();
    }

    @Override
    public void addFollowUp(FollowUpFormObject followUp, String userId) {
        var entity = new FollowUpEntity();
        entity.setCustomerId(followUp.getCustomerId());
        entity.setEmployeeId(employees.getUserInfo(userId).orElseThrow().getUserId());
        entity.setDueDate(LocalDateTime.parse(followUp.getDueDate()).atZone(ZoneId.systemDefault()).toInstant());
        entity.setTask(followUp.getTask());
        entity = followUps.save(entity);
        reminders.createReminder(entity.getId(), entity.getEmployeeId(), entity.getDueDate());
        reminders.publishReminders(userId);
    }

    @Override
    public void completeFollowUp(long id, String username) {
        followUps.completeFollowUp(id);
        reminders.completeRemindersForFollowUp(id);
        reminders.publishReminders(username);
    }

    private FollowUp toFollowUp(FollowUpRow row) {
        var followUp = new FollowUp();
        followUp.setId(row.getId());
        followUp.setCustomerId(row.getCustomerId());
        followUp.setEmployeeId(row.getEmployeeId());
        followUp.setEmployeeName(row.getEmployeeName());
        followUp.setDueDate(row.getDueDate().atZone(ZoneId.systemDefault()).toLocalDateTime());
        followUp.setTask(row.getTask());
        followUp.setDone(row.isDone());
        return followUp;
    }
}
