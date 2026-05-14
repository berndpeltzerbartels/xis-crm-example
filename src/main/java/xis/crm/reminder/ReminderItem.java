package xis.crm.reminder;

public record ReminderItem(long followUpId, String customerName, String dueDate, String task, String reminder,
                           String nextReminderAt) {
}
