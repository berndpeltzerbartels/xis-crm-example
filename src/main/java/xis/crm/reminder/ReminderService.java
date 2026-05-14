package xis.crm.reminder;

import one.xis.sql.Transactional;

import java.util.List;

public interface ReminderService {
    List<ReminderItem> reminders(String username);

    List<DueReminder> dueReminders();

    @Transactional
    void markReminderSent(DueReminder reminder);

    void publishReminders(String username);
}
