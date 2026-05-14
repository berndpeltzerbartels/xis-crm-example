package xis.crm.reminder;

import lombok.RequiredArgsConstructor;
import one.xis.context.Component;
import one.xis.context.Init;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ReminderNotifier {
    private static final Logger log = LoggerFactory.getLogger(ReminderNotifier.class);

    private final ReminderService reminders;

    @Init
    void start() {
        var executor = Executors.newSingleThreadScheduledExecutor(task -> {
            var thread = new Thread(task, "crm-reminder-notifier");
            thread.setDaemon(true);
            return thread;
        });
       // executor.scheduleWithFixedDelay(this::publishDueReminders, 5, 30, TimeUnit.SECONDS);
    }

    private void publishDueReminders() {
        try {
            reminders.dueReminders().forEach(this::publishDueReminder);
        } catch (RuntimeException e) {
            log.warn("Could not publish CRM reminders", e);
        }
    }

    private void publishDueReminder(DueReminder reminder) {
        reminders.markReminderSent(reminder);
        reminders.publishReminders(reminder.username());
    }
}
