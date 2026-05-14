package xis.crm.customer;

import lombok.Data;

@Data
public class Customer {
    private long id;
    private String name = "";
    private String segment = "";
    private String city = "";
    private String email = "";
    private String phone = "";
    private String stage = "";
    private int revenue;
    private String revenueText = "";
    private long ownerId;
    private String ownerName = "";
    private int openTasks;
    private String nextReminder = "none";
    private String notes = "";

    public void setRevenue(int revenue) {
        this.revenue = revenue;
        this.revenueText = "EUR " + revenue;
    }

    public String getRevenueText() {
        return revenueText.isBlank() ? "EUR " + revenue : revenueText;
    }

    public void setNextReminder(String nextReminder) {
        this.nextReminder = nextReminder == null ? "none" : nextReminder;
    }
}
