package xis.crm.customer;

import lombok.RequiredArgsConstructor;
import one.xis.*;
import xis.crm.CrmState;
import xis.crm.contact.Contact;
import xis.crm.contact.ContactModal;
import xis.crm.employee.EmployeeEntity;
import xis.crm.employee.EmployeeModal;
import xis.crm.employee.EmployeeService;
import xis.crm.contact.ContactService;
import xis.crm.followup.FollowUp;
import xis.crm.followup.FollowUpModal;
import xis.crm.followup.FollowUpService;

import java.util.List;

@Page("/customers.html")
@Authenticated
@WelcomePage
@RequiredArgsConstructor
class CustomersPage {
    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final PipelineCustomerService pipelineCustomerService;
    private final ContactService contactService;
    private final FollowUpService followUpService;

    @ModelData
    @SharedValue("customers")
    List<Customer> customers() {
        return customerService.customers();
    }

    @ModelData
    String employeeName(@UserId String userId) {
        return employeeService.getUserInfo(userId).map(EmployeeEntity::getName).orElse(userId);
    }

    @ModelData
    boolean isAdmin(UserContext userContext) {
        return userContext.getRoles().contains("ADMIN");
    }


    @ModelData
    String role(UserContext userContext) {
        return String.join(", ", userContext.getRoles());
    }

    @ModelData
    List<PipelineCustomer> pipelineCustomers() {
        return pipelineCustomerService.pipelineCustomers();
    }

    @ModelData
    @SharedValue("selectedCustomer")
    Customer selectedCustomer(@SharedValue("customers") List<Customer> customers,
                              @QueryParameter("customerId") @NullAllowed Long selectedCustomerId,
                              @SessionStorage("crmState") CrmState crmState) {
        var effectiveCustomerId = selectedCustomerId == null ? crmState.getSelectedCustomerId() : selectedCustomerId;
        if (effectiveCustomerId == null) {
            var firstCustomer = customers.get(0);
            crmState.setSelectedCustomerId(firstCustomer.getId());
            return firstCustomer;
        }
        return customers.stream()
                .filter(customer -> customer.getId() == effectiveCustomerId)
                .findFirst()
                .map(customer -> {
                    crmState.setSelectedCustomerId(customer.getId());
                    return customer;
                })
                .orElse(customers.get(0));
    }

    @ModelData
    String selectedCustomerOwnerName(@SharedValue("selectedCustomer") Customer customer) {
        return employeeService.employee(customer.getOwnerId()).getName();
    }

    @ModelData
    @SharedValue("contacts")
    List<Contact> contacts(@SharedValue("selectedCustomer") Customer customer) {
        return contactService.contacts(customer.getId());
    }

    @ModelData
    @SharedValue("followUps")
    List<FollowUp> followUps(@SharedValue("selectedCustomer") Customer customer) {
        return followUpService.followUps(customer.getId());
    }

    @ModelData
    long openTaskCount(@SharedValue("followUps") List<FollowUp> followUps) {
        return followUps.stream().filter(followUp -> !followUp.isDone()).count();
    }

    @ModelData
    String nextReminder(@SharedValue("followUps") List<FollowUp> followUps) {
        return followUps.stream()
                .filter(followUp -> !followUp.isDone())
                .map(FollowUp::getDueDate)
                .findFirst()
                .map(Object::toString)
                .orElse("none");
    }

    @Action
    ModalResponse editCustomer(@Parameter("customerId") long customerId) {
        return ModalResponse.open(EditCustomerModal.class).parameter("customerId", customerId);
    }

    @Action
    ModalResponse addContact(@Parameter("customerId") long customerId) {
        return ModalResponse.open(ContactModal.class).parameter("customerId", customerId);
    }


    @Action
    ModalResponse scheduleFollowUp(@Parameter("customerId") long customerId) {
        return ModalResponse.open(FollowUpModal.class).parameter("customerId", customerId);
    }

    @Action
    void completeFollowUp(@Parameter("followUpId") long followUpId, @UserId String userId) {
        followUpService.completeFollowUp(followUpId, userId);
    }

    @Action
    @Roles("ADMIN")
    ModalResponse addEmployee() {
        return ModalResponse.open(EmployeeModal.class);
    }

}
