package xis.crm.customer;

import one.xis.sql.Repository;
import one.xis.sql.Select;

import java.util.List;

@Repository
interface PipelineCustomerRepository {

    @Select("""
            select c.id, c.name, c.segment, c.city, c.stage, c.revenue, c.owner_id,
                   e.name as owner_name,
                   count(f.id) as open_tasks,
                   cast(min(r.due_date) as varchar) as next_reminder
            from customers c
            join employees e on e.user_id = c.owner_id
            left join follow_ups f on f.customer_id = c.id and f.done = false
            left join follow_up_reminders r on r.follow_up_id = f.id and r.sent = false and r.done = false
            where c.stage = 'PIPELINE'
            group by c.id, c.name, c.segment, c.city, c.stage, c.revenue, c.owner_id, e.name
            order by c.name
            """)
    List<PipelineCustomerEntity> findPipelineCustomers();
}
