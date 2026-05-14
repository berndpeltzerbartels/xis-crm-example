package xis.crm.followup;

import one.xis.sql.Transactional;

public interface FollowUpService {
    FollowUpFormObject newFollowUp(long customerId);

    @Transactional
    void addFollowUp(FollowUpFormObject followUp, String username);

    @Transactional
    void completeFollowUp(long id, String username);
}
