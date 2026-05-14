package xis.crm.followup;

import one.xis.sql.CrudRepository;
import one.xis.sql.Insert;
import one.xis.sql.Param;
import one.xis.sql.Repository;
import one.xis.sql.Select;
import one.xis.sql.Update;

import java.util.List;

@Repository
public interface FollowUpRepository extends CrudRepository<FollowUp, Long> {

    @Select("""
            select f.id, f.customer_id, f.employee_id, cast(f.due_date as varchar) as due_date,
                   e.name as employee_name, f.task, f.reminder, f.done
            from follow_ups f
            join employees e on e.id = f.employee_id
            where f.customer_id = {customerId}
            order by f.done, f.due_date, f.id
            """)
    List<FollowUp> findByCustomer(@Param("customerId") long customerId);

    @Select("select coalesce(max(id), 0) + 1 from follow_ups")
    long nextFollowUpId();

    @Insert("""
            insert into follow_ups (id, customer_id, employee_id, due_date, task, reminder, done)
            values ({id}, {customerId}, {employeeId}, {dueDate}, {task}, {reminder}, false)
            """)
    int insertFollowUp(@Param("id") long id,
                       @Param("customerId") long customerId,
                       @Param("employeeId") long employeeId,
                       @Param("dueDate") String dueDate,
                       @Param("task") String task,
                       @Param("reminder") String reminder);

    @Insert("""
            insert into follow_up_reminders (follow_up_id, remind_at, label, sent)
            values ({followUpId}, parsedatetime({dueDate} || ' ' || {reminder}, 'yyyy-MM-dd HH:mm'), {label}, false)
            """)
    int insertReminder(@Param("followUpId") long followUpId,
                       @Param("dueDate") String dueDate,
                       @Param("reminder") String reminder,
                       @Param("label") String label);

    @Update("update follow_ups set done = true where id = {id}")
    boolean completeFollowUp(@Param("id") long id);

    default long createFollowUp(FollowUp followUp) {
        long id = nextFollowUpId();
        String reminder = normalizedReminder(followUp.getReminder());
        insertFollowUp(id, followUp.getCustomerId(), followUp.getEmployeeId(), followUp.getDueDate(),
                followUp.getTask(), reminder);
        insertReminder(id, followUp.getDueDate(), reminder, "Due date " + reminder);
        return id;
    }

    private String normalizedReminder(String reminder) {
        return reminder == null || reminder.isBlank() ? "09:00" : reminder;
    }
}
