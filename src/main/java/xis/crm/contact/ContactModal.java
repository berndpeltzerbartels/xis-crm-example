package xis.crm.contact;

import lombok.RequiredArgsConstructor;
import one.xis.Action;
import one.xis.Authenticated;
import one.xis.FormData;
import one.xis.Modal;
import one.xis.ModalResponse;
import one.xis.Parameter;
import one.xis.UserId;

@Modal("/customers/contact")
@Authenticated
@RequiredArgsConstructor
public class ContactModal {
    private final ContactService contacts;

    @FormData("contact")
    ContactFormObject contact(@Parameter("customerId") long customerId) {
        return contacts.newContact(customerId);
    }

    @Action
    ModalResponse save(@FormData("contact") ContactFormObject contact, @UserId String userId) {
        contacts.addContact(contact, userId);
        return ModalResponse.close().reloadParent();
    }

    @Action
    ModalResponse cancel() {
        return ModalResponse.close();
    }
}
