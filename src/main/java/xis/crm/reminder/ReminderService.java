package xis.crm.reminder;

import one.xis.sql.Transactional;

import java.time.Instant;
import java.util.List;

public interface ReminderService {
    List<Reminder> reminders(String username);

    List<Reminder> dueReminders();

    @Transactional
    void createReminder(long followUpId, String employeeId, Instant dueDate);

    @Transactional
    void markReminderSent(long reminderId);

    @Transactional
    void completeReminder(long reminderId);

    @Transactional
    void completeRemindersForFollowUp(long followUpId);

    void publishReminders(String employeeId);
}
