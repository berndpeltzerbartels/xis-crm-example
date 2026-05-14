package xis.crm.customer;

import lombok.Getter;
import lombok.Setter;
import one.xis.sql.Entity;

@Entity("customers")
@Getter
@Setter
public class CustomerEntity {
    private long id;
    private String name = "";
    private String segment = "";
    private String city = "";
    private String email = "";
    private String phone = "";
    private String stage = "";
    private int revenue;
    private long ownerId;
    private String notes = "";

    Customer toCustomer() {
        var customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setSegment(segment);
        customer.setCity(city);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setStage(stage);
        customer.setRevenue(revenue);
        customer.setOwnerId(ownerId);
        customer.setNotes(notes);
        return customer;
    }
}
