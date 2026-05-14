package xis.crm.customer;

import one.xis.sql.CrudRepository;
import one.xis.sql.Repository;
import one.xis.sql.Select;

import java.util.List;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, Long> {

    @Select("""
            select c.*
            from customers c
            order by (select count(*) from follow_ups f where f.customer_id = c.id and f.done = false) desc,
                     c.name
            """)
    List<CustomerEntity> findCustomers();

    @Select("""
            select c.id, c.name, c.segment, c.city, c.email, c.phone, c.stage, c.revenue,
                   'EUR ' || c.revenue as revenue_text,
                   c.owner_id, e.name as owner_name,
                   cast(count(f.id) as int) as open_tasks,
                   coalesce(cast(min(f.due_date) as varchar), 'none') as next_reminder,
                   c.notes
            from customers c
            join employees e on e.id = c.owner_id
            left join follow_ups f on f.customer_id = c.id and f.done = false
            group by c.id, c.name, c.segment, c.city, c.email, c.phone, c.stage, c.revenue,
                     c.owner_id, e.name, c.notes
            order by count(f.id) desc, c.name
            """)
    List<Customer> findCustomerSummaries();
}
