package xis.crm.reminder;

import one.xis.sql.Param;
import one.xis.sql.Repository;
import one.xis.sql.Select;
import one.xis.sql.Update;

import java.util.List;

@Repository
public interface ReminderRepository {

    @Select("""
            select f.id as follow_up_id, c.name as customer_name, cast(f.due_date as varchar) as due_date,
                   f.task, f.reminder,
                   coalesce(cast(min(r.remind_at) as varchar), '') as next_reminder_at
            from follow_ups f
            join customers c on c.id = f.customer_id
            join employees e on e.id = f.employee_id
            left join follow_up_reminders r on r.follow_up_id = f.id
            where e.username = {username} and f.done = false
              and (f.due_date <= current_date
                   or exists (
                       select 1 from follow_up_reminders due
                       where due.follow_up_id = f.id
                         and due.remind_at <= current_timestamp
                   ))
            group by f.id, c.name, f.due_date, f.task, f.reminder
            order by f.due_date, f.id
            """)
    List<ReminderItem> findForUser(@Param("username") String username);

    @Select("""
            select r.id as reminder_id, e.username
            from follow_up_reminders r
            join follow_ups f on f.id = r.follow_up_id
            join employees e on e.id = f.employee_id
            where r.sent = false
              and f.done = false
              and r.remind_at <= current_timestamp
            order by r.remind_at, r.id
            """)
    List<DueReminder> findDueReminders();

    @Update("update follow_up_reminders set sent = true where id = {id}")
    boolean markReminderSent(@Param("id") long reminderId);
}
