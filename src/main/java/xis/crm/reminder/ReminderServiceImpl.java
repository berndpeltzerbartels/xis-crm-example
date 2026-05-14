package xis.crm.reminder;

import lombok.RequiredArgsConstructor;
import one.xis.RefreshEventPublisher;
import one.xis.context.Component;
import xis.crm.CrmEvents;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminders;
    private final RefreshEventPublisher refreshEvents;

    @Override
    public List<ReminderItem> reminders(String username) {
        return reminders.findForUser(username);
    }

    @Override
    public List<DueReminder> dueReminders() {
        return reminders.findDueReminders();
    }

    @Override
    public void markReminderSent(DueReminder reminder) {
        reminders.markReminderSent(reminder.reminderId());
    }

    @Override
    public void publishReminders(String username) {
        refreshEvents.publishToUser(CrmEvents.REMINDERS, username);
    }
}
