package xis.crm.contact;

import one.xis.sql.CrudRepository;
import one.xis.sql.Param;
import one.xis.sql.Repository;
import one.xis.sql.Select;

import java.util.List;

@Repository
public interface ContactRepository extends CrudRepository<ContactEntity, Long> {

    @Select("""
            select c.id, c.customer_id, c.employee_id, cast(c.contact_date as varchar) as contact_date,
                   e.name as employee_name, c.channel, c.description
            from contacts c
            join employees e on e.id = c.employee_id
            where c.customer_id = {customerId}
            order by c.contact_date desc, c.id desc
            """)
    List<ContactEntry> findByCustomer(@Param("customerId") long customerId);

}
