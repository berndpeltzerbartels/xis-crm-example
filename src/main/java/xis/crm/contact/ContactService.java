package xis.crm.contact;

import one.xis.sql.Transactional;

public interface ContactService {
    ContactFormObject newContact(long customerId);

    @Transactional
    void addContact(ContactFormObject contact, String username);
}
