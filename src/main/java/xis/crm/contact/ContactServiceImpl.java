package xis.crm.contact;

import lombok.RequiredArgsConstructor;
import one.xis.context.Component;
import xis.crm.employee.EmployeeService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contacts;
    private final EmployeeService employees;

    @Override
    public ContactFormObject newContact(long customerId) {
        var contact = new ContactFormObject();
        contact.setCustomerId(customerId);
        contact.setContactDate(LocalDate.now().toString());
        contact.setChannel("Call");
        return contact;
    }

    @Override
    public void addContact(ContactFormObject contact, String username) {
        long employeeId = employees.getUserInfo(username).orElseThrow().getId();
        var entity = new ContactEntity();
        entity.setCustomerId(contact.getCustomerId());
        entity.setEmployeeId(employeeId);
        entity.setContactDate(contact.getContactDate());
        entity.setChannel(contact.getChannel());
        entity.setDescription(contact.getDescription());
        contacts.save(entity);
    }
}
