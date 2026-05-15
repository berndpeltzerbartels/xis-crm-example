package xis.crm.contact;

import lombok.RequiredArgsConstructor;
import one.xis.context.Component;
import xis.crm.employee.EmployeeService;

import java.time.LocalDate;
import java.util.List;

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
    public List<Contact> contacts(long customerId) {
        return contacts.findByCustomer(customerId).stream().map(this::toContact).toList();
    }

    @Override
    public void addContact(ContactFormObject contact, String username) {
        String employeeId = employees.getUserInfo(username).orElseThrow().getUserId();
        var entity = new ContactEntity();
        entity.setCustomerId(contact.getCustomerId());
        entity.setEmployeeId(employeeId);
        entity.setContactDate(contact.getContactDate());
        entity.setChannel(contact.getChannel());
        entity.setDescription(contact.getDescription());
        contacts.save(entity);
    }

    private Contact toContact(ContactEntry entry) {
        var contact = new Contact();
        contact.setId(entry.getId());
        contact.setCustomerId(entry.getCustomerId());
        contact.setEmployeeId(entry.getEmployeeId());
        contact.setContactDate(entry.getContactDate());
        contact.setEmployeeName(entry.getEmployeeName());
        contact.setChannel(entry.getChannel());
        contact.setDescription(entry.getDescription());
        return contact;
    }
}
