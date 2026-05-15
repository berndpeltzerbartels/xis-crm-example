package xis.crm.customer;

import lombok.Getter;
import lombok.Setter;
import one.xis.sql.Entity;

@Entity(value = "customers", allowUnmappedFields = true)
@Getter
@Setter
class PipelineCustomerEntity {
    private long id;
    private String name;
    private String segment;
    private String city;
    private CustomerStage stage;
    private int revenue;
    private String ownerId;
    private String ownerName;
    private int openTasks;
    private String nextReminder;
}
