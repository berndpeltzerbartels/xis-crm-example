package xis.crm.customer;

import xis.crm.contact.ContactEntry;

import java.util.List;

public record CustomerDetail(Customer customer, String email, String phone, String notes,
                             List<ContactEntry> contacts) {
}
