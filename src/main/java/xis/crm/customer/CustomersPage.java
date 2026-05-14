package xis.crm.customer;

import lombok.RequiredArgsConstructor;
import one.xis.Action;
import one.xis.Authenticated;
import one.xis.ClientId;
import one.xis.FormData;
import one.xis.ModelData;
import one.xis.ModalResponse;
import one.xis.Page;
import one.xis.Parameter;
import one.xis.Roles;
import one.xis.UserContext;
import one.xis.UserId;
import one.xis.WelcomePage;
import xis.crm.contact.ContactModal;
import xis.crm.employee.Employee;
import xis.crm.employee.EmployeeService;
import xis.crm.employee.EmployeeModal;
//import xis.crm.followup.FollowUpModal;
//import xis.crm.reminder.ReminderDoneModal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Page("/customers.html")
@Authenticated
@WelcomePage
@RequiredArgsConstructor
class CustomersPage {
    private final CustomerService customers;
    private final EmployeeService employees;
    private final Map<String, Long> selectedCustomers = new ConcurrentHashMap<>();

    @ModelData
    boolean isAdmin() {
        return UserContext.getInstance().getRoles().contains("ADMIN");
    }

    @ModelData
    String employeeName(@UserId String userId) {
        return employees.getUserInfo(userId).map(Employee::getName).orElse(userId);
    }

    @ModelData
    String role() {
        return String.join(", ", UserContext.getInstance().getRoles());
    }

    @ModelData
    List<Customer> customers() {
        return customers.customers();
    }

    @ModelData
    CustomerDetail selectedCustomer(@ClientId String clientId) {
        return customers.customerDetail(selectedCustomerId(clientId));
    }

    @Action
    void selectCustomer(@Parameter("customerId") long customerId,
                        @ClientId String clientId) {
        selectedCustomers.put(clientId, customerId);
    }

    @Action
    ModalResponse editCustomer(@Parameter("customerId") long customerId) {
        return ModalResponse.open(EditCustomerModal.class).parameter("customerId", customerId);
    }

    @Action
    ModalResponse addContact(@Parameter("customerId") long customerId) {
        return ModalResponse.open(ContactModal.class).parameter("customerId", customerId);
    }

    /*
    @Action
    ModalResponse scheduleFollowUp(@Parameter("customerId") long customerId) {
        return ModalResponse.open(FollowUpModal.class).parameter("customerId", customerId);
    }

     */

    @Action
    @Roles("ADMIN")
    ModalResponse addEmployee() {
        return ModalResponse.open(EmployeeModal.class);
    }

    /*
    @Action
    ModalResponse completeFollowUp(@Parameter("followUpId") long followUpId) {
        return ModalResponse.open(ReminderDoneModal.class).parameter("followUpId", followUpId);
    }

     */

    private long selectedCustomerId(String clientId) {
        return selectedCustomers.computeIfAbsent(clientId, ignored -> firstCustomerId());
    }

    private long firstCustomerId() {
        return customers.customers().get(0).getId();
    }
}
