package xis.crm.contact;

import one.xis.sql.Transactional;

import java.util.List;

public interface ContactService {
    ContactFormObject newContact(long customerId);

    List<Contact> contacts(long customerId);

    @Transactional
    void addContact(ContactFormObject contact, String username);
}
