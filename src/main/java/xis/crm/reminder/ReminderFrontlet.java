package xis.crm.reminder;

import lombok.RequiredArgsConstructor;
import one.xis.*;
import xis.crm.CrmEvents;

import java.util.List;

@Frontlet
@Authenticated
@RefreshOnUpdateEvents(CrmEvents.REMINDERS)
@RequiredArgsConstructor
class ReminderFrontlet {
    private final ReminderService reminderService;

    @ModelData
    @SharedValue("reminders")
    List<Reminder> reminders(@UserId String userId) {
        return reminderService.reminders(userId);
    }

    @ModelData
    boolean hasReminders(@SharedValue("reminders") List<Reminder> reminders) {
        return !reminders.isEmpty();
    }

    @Action
    void complete(@Parameter("reminderId") long reminderId) {
        reminderService.completeReminder(reminderId);
    }
}
