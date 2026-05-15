package xis.crm.reminder;

import lombok.RequiredArgsConstructor;
import one.xis.RefreshEventPublisher;
import one.xis.context.Component;
import xis.crm.CrmEvents;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository repository;
    private final RefreshEventPublisher refreshEvents;

    @Override
    public List<Reminder> reminders(String username) {
        return repository.findOpenForEmployee(username).stream().map(this::toReminder).toList();
    }

    @Override
    public List<Reminder> dueReminders() {
        return repository.findDueReminders().stream().map(this::toReminder).toList();
    }

    @Override
    public void createReminder(long followUpId, String employeeId, Instant dueDate) {
        var reminder = new ReminderEntity();
        reminder.setFollowUpId(followUpId);
        reminder.setEmployeeId(employeeId);
        reminder.setDueDate(dueDate);
        reminder.setSent(false);
        reminder.setDone(false);
        repository.save(reminder);
    }

    @Override
    public void markReminderSent(long reminderId) {
        var entity = repository.findById(reminderId).orElseThrow();
        entity.setSent(true);
        repository.save(entity);
    }

    @Override
    public void completeRemindersForFollowUp(long followUpId) {
        repository.completeForFollowUp(followUpId);
    }

    @Override
    public void completeReminder(long reminderId) {
        var entity = repository.findById(reminderId).orElseThrow();
        entity.setDone(true);
        entity.setSent(true);
        repository.save(entity);
    }

    @Override
    public void publishReminders(String username) {
        refreshEvents.publishToUser(CrmEvents.REMINDERS, username);
    }

    private Reminder toReminder(ReminderEntity reminderEntity) {
        var reminder = new Reminder();
        reminder.setId(reminderEntity.getId());
        reminder.setFollowUpId(reminderEntity.getFollowUpId());
        reminder.setEmployeeId(reminderEntity.getEmployeeId());
        reminder.setDueDate(reminderEntity.getDueDate().atZone(ZoneId.systemDefault()).toLocalDateTime());
        return reminder;
    }

    private Reminder toReminder(ReminderRow reminderRow) {
        var reminder = new Reminder();
        reminder.setId(reminderRow.getId());
        reminder.setFollowUpId(reminderRow.getFollowUpId());
        reminder.setEmployeeId(reminderRow.getEmployeeId());
        reminder.setDueDate(reminderRow.getDueDate().atZone(ZoneId.systemDefault()).toLocalDateTime());
        reminder.setCustomerName(reminderRow.getCustomerName());
        reminder.setTask(reminderRow.getTask());
        reminder.setNextReminderAt(reminderRow.getNextReminderAt());
        return reminder;
    }

}
