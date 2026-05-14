package xis.crm.followup;

import lombok.RequiredArgsConstructor;
import one.xis.Action;
import one.xis.Authenticated;
import one.xis.FormData;
import one.xis.Modal;
import one.xis.ModalResponse;
import one.xis.Parameter;
import one.xis.UserId;

@Modal("/customers/follow-up")
@Authenticated
@RequiredArgsConstructor
public class FollowUpModal {
    private final FollowUpService followUps;

    @FormData("followUp")
    FollowUpFormObject followUp(@Parameter("customerId") long customerId) {
        return followUps.newFollowUp(customerId);
    }

    @Action
    ModalResponse save(@FormData("followUp") FollowUpFormObject followUp, @UserId String userId) {
        followUps.addFollowUp(followUp, userId);
        return ModalResponse.close().reloadParent();
    }

    @Action
    ModalResponse cancel() {
        return ModalResponse.close();
    }
}
