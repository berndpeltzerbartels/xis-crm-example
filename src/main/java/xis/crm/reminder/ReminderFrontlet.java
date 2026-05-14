package xis.crm.reminder;

import lombok.RequiredArgsConstructor;
import one.xis.Action;
import one.xis.Authenticated;
import one.xis.Frontlet;
import one.xis.ModelData;
import one.xis.ModalResponse;
import one.xis.Parameter;
import one.xis.RefreshOnUpdateEvents;
import one.xis.UserId;
import xis.crm.CrmEvents;

import java.util.List;

@Frontlet
@Authenticated
@RefreshOnUpdateEvents(CrmEvents.REMINDERS)
@RequiredArgsConstructor
class ReminderFrontlet {
    private final ReminderService reminders;

    @ModelData
    List<ReminderItem> reminders(@UserId String userId) {
        return reminders.reminders(userId);
    }

    @ModelData
    boolean hasReminders(@UserId String userId) {
        return !reminders.reminders(userId).isEmpty();
    }

    @Action
    ModalResponse complete(@Parameter("followUpId") long followUpId) {
        return ModalResponse.open(ReminderDoneModal.class).parameter("followUpId", followUpId);
    }
}
