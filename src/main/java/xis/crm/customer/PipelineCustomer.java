package xis.crm.customer;

import lombok.Data;

@Data
public class PipelineCustomer {
    private long id;
    private String name;
    private String segment;
    private String city;
    private CustomerStage stage;
    private int revenue;
    private String revenueText;
    private String ownerId;
    private String ownerName;
    private int openTasks;
    private String nextReminder;
}
