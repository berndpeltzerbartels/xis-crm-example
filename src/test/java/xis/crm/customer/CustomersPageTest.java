package xis.crm.customer;

import one.xis.context.IntegrationTestContext;
import one.xis.context.TestClient;
import one.xis.test.dom.Element;
import one.xis.test.dom.InputElement;
import one.xis.test.dom.TextareaElement;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xis.crm.employee.Employee;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomersPageTest {

    private IntegrationTestContext context;

    @BeforeEach
    void setUp() {
        var dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:crm-page-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");

        var user = new Employee();
        user.setUserId("mara");
        user.setName("Mara Stein");
        user.setRole("SALES");
        user.setRoles(Set.of("SALES"));

        context = IntegrationTestContext.builder()
                .withSingleton(dataSource)
                .withPackage("xis.crm")
                .withLoggedInUser(user, "demo")
                .build();
    }

    @Test
    void showsFirstCustomerUntilUserSelectsAnotherCustomer() {
        var client = context.openPage("/customers.html");
        var document = client.getDocument();

        assertEquals("Mara Stein", document.getElementById("employee-name").getInnerText());
        assertEquals("SALES", document.getElementById("employee-role").getInnerText());
        assertEquals("4 accounts", document.getElementById("customer-count").getInnerText());
        assertEquals("Blue Pepper Retail", document.getElementById("selected-customer-name").getInnerText());
        assertNull(context.getSessionStorage().getItem("crmWorkspace"));

        document.getElementById("customer-1").click();

        assertEquals("Nordlicht Logistics", document.getElementById("selected-customer-name").getInnerText());
        assertEquals("Proposal", document.getElementById("selected-customer-stage").getInnerText());
        assertNull(context.getSessionStorage().getItem("crmWorkspace"));
    }

    @Test
    void editCustomerUsesSelectedCustomer() {
        assertSelectedCustomerParameter("Edit customer", "Edit customer", "id", "1");
    }

    @Test
    void addContactUsesSelectedCustomer() {
        assertSelectedCustomerParameter("Add contact", "Add contact", "customerId", "1");
    }

    @Test
    void scheduleFollowUpUsesSelectedCustomer() {
        assertSelectedCustomerParameter("Schedule", "Schedule follow-up", "customerId", "1");
    }

    @Test
    void editCustomerSavesCustomerAndReloadsPage() {
        var client = context.openPage("/customers.html");
        var document = client.getDocument();

        document.getElementById("customer-1").click();
        button(client, "Edit customer").click();
        input(client, "name").setValue("Nordlicht Solutions");
        input(client, "stage").setValue("Won");
        textarea(client, "notes").setValue("Contract signed after rollout planning.");
        button(client, "Save").click();

        assertEquals("Nordlicht Solutions", client.getDocument().getElementById("selected-customer-name").getInnerText());
        assertEquals("Won", client.getDocument().getElementById("selected-customer-stage").getInnerText());
        assertTextOccurs(client, ".notes p", "Contract signed after rollout planning.");
    }

    @Test
    void addContactSavesContactAndReloadsPage() {
        var client = context.openPage("/customers.html");
        var document = client.getDocument();

        document.getElementById("customer-1").click();
        button(client, "Add contact").click();
        input(client, "contactDate").setValue("2026-05-13");
        textarea(client, "description").setValue("Walked through commercial rollout risks.");
        button(client, "Save contact").click();

        assertTextOccurs(client, ".timeline p", "Walked through commercial rollout risks.");
    }

    @Test
    void scheduleFollowUpSavesFollowUpAndReloadsPage() {
        var client = context.openPage("/customers.html");
        var document = client.getDocument();

        document.getElementById("customer-1").click();
        button(client, "Schedule").click();
        input(client, "dueDate").setValue("2026-05-20");
        input(client, "reminder").setValue("15:45");
        input(client, "task").setValue("Send board-ready offer");
        button(client, "Schedule").click();

        assertTextOccurs(client, ".tasks strong", "Send board-ready offer");
        assertTextOccurs(client, ".tasks span", "Reminder 15:45");
    }

    @Test
    void completeFollowUpMarksTaskDoneAndReloadsPage() {
        var client = context.openPage("/customers.html");
        var document = client.getDocument();

        document.getElementById("customer-1").click();
        button(client, "Done").click();
        assertEquals("Complete reminder", heading(client, "Complete reminder").getInnerText());
        button(client, "Mark done").click();

        assertEquals("0", document.getElementById("selected-customer-open-tasks").getInnerText());
    }

    private void assertSelectedCustomerParameter(String actionText, String modalTitle,
                                                 String fieldName, String expectedValue) {
        var client = context.openPage("/customers.html");
        var document = client.getDocument();

        document.getElementById("customer-1").click();
        button(client, actionText).click();

        assertEquals(modalTitle, heading(client, modalTitle).getInnerText());
        assertEquals(expectedValue, input(client, fieldName).getValue());
    }

    private Element button(TestClient client, String text) {
        return client.getDocument().querySelectorAll("button").stream()
                .filter(element -> text.equals(element.getInnerText()))
                .reduce((first, second) -> second)
                .orElseThrow();
    }

    private InputElement input(TestClient client, String binding) {
        return (InputElement) client.getDocument().querySelectorAll("input").stream()
                .filter(element -> binding.equals(element.getAttribute("xis:binding")))
                .findFirst()
                .orElseThrow();
    }

    private TextareaElement textarea(TestClient client, String binding) {
        return (TextareaElement) client.getDocument().querySelectorAll("textarea").stream()
                .filter(element -> binding.equals(element.getAttribute("xis:binding")))
                .findFirst()
                .orElseThrow();
    }

    private Element heading(TestClient client, String text) {
        return client.getDocument().querySelectorAll("h2").stream()
                .filter(element -> text.equals(element.getInnerText()))
                .findFirst()
                .orElseThrow();
    }

    private String textIn(TestClient client, String selector) {
        return client.getDocument().querySelector(selector).getInnerText();
    }

    private void assertTextOccurs(TestClient client, String selector, String text) {
        assertTrue(client.getDocument().querySelectorAll(selector).stream()
                .anyMatch(element -> text.equals(element.getInnerText()) || element.getInnerText().contains(text)));
    }
}
