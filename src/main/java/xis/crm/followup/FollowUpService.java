package xis.crm.followup;

import one.xis.sql.Transactional;

import java.util.List;

public interface FollowUpService {
    FollowUpFormObject newFollowUp(long customerId);

    List<FollowUp> followUps(long customerId);

    @Transactional
    void addFollowUp(FollowUpFormObject followUp, String userId);

    @Transactional
    void completeFollowUp(long id, String userId);
}
