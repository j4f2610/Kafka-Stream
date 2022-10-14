package vn.eton.promotion.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.piomin.base.domain.Order;
import vn.eton.promotion.domain.Promotion;
import vn.eton.promotion.repository.PromotionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private static final String SOURCE = "promotion";
    private static final Logger LOG = LoggerFactory.getLogger(PromotionService.class);
    private final PromotionRepository repository;
    private KafkaTemplate<Long, Order> template;

    //    public PromotionService(PromotionRepository repository, KafkaTemplate<Long, Order> template) {
//        this.repository = repository;
//        this.template = template;
//    }
    public Promotion create(Promotion model) {
        return repository.save(model);
    }

    public Iterable<Promotion> getList() {
        return repository.findAll();
    }

    public Promotion findById(Long id) {
        return repository.findById(id).get();
    }
//    public void reserve(Order order) {
//        Promotion customer = repository.findById(order.getCustomerId()).orElseThrow();
//        LOG.info("Found: {}", customer);
//        if (order.getPrice() < customer.getAmountAvailable()) {
//            order.setStatus("ACCEPT");
//            customer.setAmountReserved(customer.getAmountReserved() + order.getPrice());
//            customer.setAmountAvailable(customer.getAmountAvailable() - order.getPrice());
//        } else {
//            order.setStatus("REJECT");
//        }
//        order.setSource(SOURCE);
//        repository.save(customer);
//        template.send("payment-orders", order.getId(), order);
//        LOG.info("Sent: {}", order);
//    }
//
//    public void confirm(Order order) {
//        Promotion customer = repository.findById(order.getCustomerId()).orElseThrow();
//        LOG.info("Found: {}", customer);
//        if (order.getStatus().equals("CONFIRMED")) {
//            customer.setAmountReserved(customer.getAmountReserved() - order.getPrice());
//            repository.save(customer);
//        } else if (order.getStatus().equals("ROLLBACK") && !order.getSource().equals(SOURCE)) {
//            customer.setAmountReserved(customer.getAmountReserved() - order.getPrice());
//            customer.setAmountAvailable(customer.getAmountAvailable() + order.getPrice());
//            repository.save(customer);
//        }
//
//    }
}
