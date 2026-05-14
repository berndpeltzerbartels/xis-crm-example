package xis.crm.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerFormObject {
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

    static CustomerFormObject from(CustomerEntity customer) {
        var object = new CustomerFormObject();
        object.setId(customer.getId());
        object.setName(customer.getName());
        object.setSegment(customer.getSegment());
        object.setCity(customer.getCity());
        object.setEmail(customer.getEmail());
        object.setPhone(customer.getPhone());
        object.setStage(customer.getStage());
        object.setRevenue(customer.getRevenue());
        object.setOwnerId(customer.getOwnerId());
        object.setNotes(customer.getNotes());
        return object;
    }
}
