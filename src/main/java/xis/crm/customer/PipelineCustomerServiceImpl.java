package xis.crm.customer;

import lombok.RequiredArgsConstructor;
import one.xis.context.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class PipelineCustomerServiceImpl implements PipelineCustomerService {
    private final PipelineCustomerRepository repository;

    @Override
    public List<PipelineCustomer> pipelineCustomers() {
        return repository.findPipelineCustomers().stream().map(this::toPipelineCustomer).toList();
    }

    private PipelineCustomer toPipelineCustomer(PipelineCustomerEntity entity) {
        var customer = new PipelineCustomer();
        customer.setId(entity.getId());
        customer.setName(entity.getName());
        customer.setSegment(entity.getSegment());
        customer.setCity(entity.getCity());
        customer.setStage(entity.getStage());
        customer.setRevenue(entity.getRevenue());
        customer.setRevenueText("EUR " + entity.getRevenue());
        customer.setOwnerId(entity.getOwnerId());
        customer.setOwnerName(entity.getOwnerName());
        customer.setOpenTasks(entity.getOpenTasks());
        customer.setNextReminder(entity.getNextReminder() == null ? "none" : entity.getNextReminder());
        return customer;
    }
}
