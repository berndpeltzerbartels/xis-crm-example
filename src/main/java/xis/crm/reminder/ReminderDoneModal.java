package xis.crm.reminder;

import lombok.RequiredArgsConstructor;
import one.xis.Action;
import one.xis.Authenticated;
import one.xis.FormData;
import one.xis.Modal;
import one.xis.ModalResponse;
import one.xis.Parameter;
import one.xis.UserId;
import xis.crm.followup.FollowUpService;

@Modal("/reminders/done")
@Authenticated
@RequiredArgsConstructor
public class ReminderDoneModal {
    private final FollowUpService followUps;

    @FormData("done")
    ReminderDoneForm done(@Parameter("followUpId") long followUpId) {
        var form = new ReminderDoneForm();
        form.setFollowUpId(followUpId);
        return form;
    }

    @Action
    ModalResponse save(@FormData("done") ReminderDoneForm form, @UserId String userId) {
        followUps.completeFollowUp(form.getFollowUpId(), userId);
        return ModalResponse.close().reloadParent();
    }

    @Action
    ModalResponse cancel() {
        return ModalResponse.close();
    }
}
