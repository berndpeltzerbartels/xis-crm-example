package xis.crm.customer;

import one.xis.sql.CrudRepository;
import one.xis.sql.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, Long> {

}
