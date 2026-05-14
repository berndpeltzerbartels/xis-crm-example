package xis.crm.followup;

import lombok.RequiredArgsConstructor;
import one.xis.context.Component;
import xis.crm.employee.EmployeeService;
import xis.crm.reminder.ReminderService;

import java.time.LocalDate;

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
        followUp.setDueDate(LocalDate.now().plusDays(3).toString());
        followUp.setReminder("09:00");
        followUp.setTask("Send follow-up");
        return followUp;
    }

    @Override
    public void addFollowUp(FollowUpFormObject followUp, String username) {
        var entity = new FollowUp();
        entity.setCustomerId(followUp.getCustomerId());
        entity.setEmployeeId(employees.getUserInfo(username).orElseThrow().getId());
        entity.setDueDate(followUp.getDueDate());
        entity.setTask(followUp.getTask());
        entity.setReminder(followUp.getReminder());
        followUps.createFollowUp(entity);
        reminders.publishReminders(username);
    }

    @Override
    public void completeFollowUp(long id, String username) {
        followUps.completeFollowUp(id);
        reminders.publishReminders(username);
    }
}
