package xis.crm.reminder;

import lombok.RequiredArgsConstructor;
import one.xis.context.Component;
import one.xis.context.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ReminderNotifier {
    private static final Logger log = LoggerFactory.getLogger(ReminderNotifier.class);

    private final ReminderService reminders;

    @Scheduled(initialDelay = 5, fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
    void start() {
        publishDueReminders();
    }

    private void publishDueReminders() {
        try {
            reminders.dueReminders().forEach(this::publishDueReminder);
        } catch (RuntimeException e) {
            log.warn("Could not publish CRM reminders", e);
        }
    }

    private void publishDueReminder(Reminder reminder) {
        reminders.publishReminders(reminder.getEmployeeId());
        reminders.markReminderSent(reminder.getId());
    }
}
