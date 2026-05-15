package xis.crm.customer;

import one.xis.context.IntegrationTestContext;
import one.xis.context.TestClient;
import one.xis.test.dom.Element;
import one.xis.test.dom.InputElement;
import one.xis.test.dom.SelectElement;
import one.xis.test.dom.TextareaElement;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import xis.crm.employee.EmployeeEntity;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CustomersPageTest {

    private IntegrationTestContext context;

    @BeforeEach
    void setUp() {
        context = createContext("SALES");
    }

    private IntegrationTestContext createContext(String role) {
        var dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:crm-page-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");

        var user = new EmployeeEntity();
        user.setUserId("mara");
        user.setName("Mara Stein");
        user.setRole(role);
        user.setRoles(Set.of(role));

        return IntegrationTestContext.builder()
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
        assertEquals("1 accounts", document.getElementById("customer-count").getInnerText());
        assertEquals("Nordlicht Logistics", document.getElementById("selected-customer-name").getInnerText());

        document.getElementById("customer-4").click();

        assertEquals("Blue Pepper Retail", document.getElementById("selected-customer-name").getInnerText());
        assertEquals("PIPELINE", document.getElementById("selected-customer-stage").getInnerText());
        assertTrue(client.getSessionStorage().getItem("crmState").contains("\"selectedCustomerId\":4"));
    }

    @Test
    void editCustomerUsesSelectedCustomer() {
        assertSelectedCustomerParameter("Edit customer", "Edit customer", "id", "4");
    }

    @Test
    void addContactUsesSelectedCustomer() {
        assertSelectedCustomerParameter("Add contact", "Add contact", "customerId", "4");
    }

    @Test
    void scheduleFollowUpUsesSelectedCustomer() {
        assertSelectedCustomerParameter("Schedule", "Schedule follow-up", "customerId", "4");
    }

    @Test
    void editCustomerSavesCustomerAndReloadsPage() {
        var client = context.openPage("/customers.html");
        var document = client.getDocument();

        document.getElementById("customer-4").click();
        button(client, "Edit customer").click();
        input(client, "name").setValue("Blue Pepper Solutions");
        input(client, "city").setValue("Hamburg");
        select(client, "stage").setValue("WON");
        input(client, "revenue").setValue("123456");
        textarea(client, "notes").setValue("Contract signed after rollout planning.");
        button(client, "Save").click();

        assertEquals("Blue Pepper Solutions", client.getDocument().getElementById("selected-customer-name").getInnerText());
        assertTrue(client.getDocument().getElementById("selected-customer-contact").getInnerText().contains("Hamburg"));
        assertEquals("WON", client.getDocument().getElementById("selected-customer-stage").getInnerText());
        assertEquals("123456 EUR", client.getDocument().getElementById("selected-customer-revenue").getInnerText());
        assertTextOccurs(client, ".notes p", "Contract signed after rollout planning.");
    }

    @Nested
    class CustomerModalTests {

        @Test
        void invalidCustomerShowsFieldMessagesAndStaysOpen() {
            var client = openSelectedCustomer();

            button(client, "Edit customer").click();
            input(client, "name").setValue("");
            input(client, "email").setValue("broken-email");
            input(client, "phone").setValue("abc");
            button(client, "Save").click();

            assertEquals("Edit customer", heading(client, "Edit customer").getInnerText());
            assertFieldError(client, "name");
            assertFieldError(client, "email");
            assertFieldError(client, "phone");
        }
    }

    @Nested
    class ContactModalTests {

        @Test
        void savesContactAndReloadsPage() {
            var client = openSelectedCustomer();

            button(client, "Add contact").click();
            input(client, "contactDate").setValue("2026-05-13");
            textarea(client, "description").setValue("Walked through commercial rollout risks.");
            button(client, "Save contact").click();

            assertTextOccurs(client, ".timeline p", "Walked through commercial rollout risks.");
        }

        @Test
        void validationErrorsStayInModal() {
            var client = openSelectedCustomer();

            button(client, "Add contact").click();
            textarea(client, "description").setValue("short");
            button(client, "Save contact").click();

            assertEquals("Add contact", heading(client, "Add contact").getInnerText());
            assertFieldError(client, "description");
        }
    }

    @Nested
    class FollowUpModalTests {

        @Test
        void savesFollowUpAndReloadsPage() {
            var client = openSelectedCustomer();

            button(client, "Schedule").click();
            input(client, "dueDate").setValue("2026-05-20T15:45");
            input(client, "task").setValue("Send board-ready offer");
            button(client, "Schedule").click();

            assertTextOccurs(client, ".tasks strong", "Send board-ready offer");
            assertTextOccurs(client, ".tasks span", "2026-05-20T15:45");
        }

        @Test
        void validationErrorsStayInModal() {
            var client = openSelectedCustomer();

            button(client, "Schedule").click();
            input(client, "dueDate").setValue("");
            input(client, "task").setValue("short");
            button(client, "Schedule").click();

            assertEquals("Schedule follow-up", heading(client, "Schedule follow-up").getInnerText());
            assertFieldError(client, "dueDate");
            assertFieldError(client, "task");
        }

        @Test
        void completeFollowUpMarksTaskDoneAndReloadsPage() {
            var client = openSelectedCustomer();

            button(client, "Done").click();

            assertEquals("0", client.getDocument().getElementById("selected-customer-open-tasks").getInnerText());
        }
    }

    @Nested
    class EmployeeModalTests {

        @Test
        void invalidEmployeeShowsValidationErrorsAndStaysOpen() {
            context = createContext("ADMIN");
            var client = context.openPage("/customers.html");

            button(client, "Add employee").click();
            input(client, "name").setValue("A");
            input(client, "userId").setValue("not allowed");
            input(client, "password").setValue("123");
            button(client, "Create employee").click();

            assertEquals("Add employee", heading(client, "Add employee").getInnerText());
            assertFieldError(client, "name");
            assertFieldError(client, "userId");
            assertFieldError(client, "password");
        }
    }

    private void assertSelectedCustomerParameter(String actionText, String modalTitle,
                                                 String fieldName, String expectedValue) {
        var client = openSelectedCustomer();
        button(client, actionText).click();

        assertEquals(modalTitle, heading(client, modalTitle).getInnerText());
        assertEquals(expectedValue, input(client, fieldName).getValue());
    }

    private TestClient openSelectedCustomer() {
        var client = context.openPage("/customers.html");
        client.getDocument().getElementById("customer-4").click();
        return client;
    }

    private void assertFieldError(TestClient client, String binding) {
        assertFalse(fieldMessage(client, binding).getInnerText().isBlank());
        assertTrue(formElement(client, binding).getAttribute("class").contains("error"));
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

    private SelectElement select(TestClient client, String binding) {
        return (SelectElement) client.getDocument().querySelectorAll("select").stream()
                .filter(element -> binding.equals(element.getAttribute("xis:binding")))
                .findFirst()
                .orElseThrow();
    }

    private Element formElement(TestClient client, String binding) {
        return Stream.of("input", "textarea", "select")
                .flatMap(selector -> client.getDocument().querySelectorAll(selector).stream())
                .filter(element -> binding.equals(element.getAttribute("xis:binding")))
                .findFirst()
                .orElseThrow();
    }

    private Element fieldMessage(TestClient client, String binding) {
        return client.getDocument().getElementsByTagName("xis:message").stream()
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .filter(element -> binding.equals(element.getAttribute("message-for")))
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
