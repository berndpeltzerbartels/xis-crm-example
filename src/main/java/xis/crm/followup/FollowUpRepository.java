package xis.crm.followup;

import one.xis.sql.CrudRepository;
import one.xis.sql.Param;
import one.xis.sql.Repository;
import one.xis.sql.Select;
import one.xis.sql.Update;

import java.util.List;

@Repository
interface FollowUpRepository extends CrudRepository<FollowUpEntity, Long> {

    @Select("""
            select f.id, f.customer_id, f.employee_id, e.name as employee_name,
                   f.due_date, f.task, f.done
            from follow_ups f
            join employees e on e.user_id = f.employee_id
            where f.customer_id = {customerId}
            order by f.done, f.due_date, f.id
            """)
    List<FollowUpRow> findByCustomer(@Param("customerId") long customerId);

    @Update("update follow_ups set done = true where id = {id}")
    void completeFollowUp(@Param("id") long id);
}
