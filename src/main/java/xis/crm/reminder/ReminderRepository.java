package xis.crm.reminder;

import one.xis.sql.CrudRepository;
import one.xis.sql.Param;
import one.xis.sql.Repository;
import one.xis.sql.Select;
import one.xis.sql.Update;

import java.util.List;

@Repository
interface ReminderRepository extends CrudRepository<ReminderEntity, Long> {

    @Select("""
            select *
            from follow_up_reminders
            where sent = false and done = false and due_date <= current_timestamp
            order by due_date, id
            """)
    List<ReminderEntity> findDueReminders();

    @Select("""
            select r.id, r.follow_up_id, r.employee_id, r.due_date,
                   c.name as customer_name,
                   f.task,
                   cast(r.due_date as varchar) as next_reminder_at
            from follow_up_reminders r
            join follow_ups f on f.id = r.follow_up_id
            join customers c on c.id = f.customer_id
            where r.employee_id = {employeeId} and r.done = false
            order by r.due_date, r.id
            """)
    List<ReminderRow> findOpenForEmployee(@Param("employeeId") String employeeId);

    @Update("update follow_up_reminders set done = true, sent = true where follow_up_id = {followUpId}")
    void completeForFollowUp(@Param("followUpId") long followUpId);
}
