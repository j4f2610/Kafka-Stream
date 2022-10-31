package vn.eton.promotion;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import pl.piomin.base.domain.Order;
import vn.eton.promotion.service.PromotionService;

@SpringBootApplication
@EnableKafka
@RequiredArgsConstructor
public class PromotionApp {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionApp.class);

    public static void main(String[] args) {
        SpringApplication.run(PromotionApp.class, args);
    }

    private final PromotionService promotionService;

    private final KafkaTemplate<Long, Order> template;
    public final static String PROMOTION_ORDER="promotion-orders";


    @KafkaListener(id = "orders", topics = "orders", groupId = "promotion")
    public void onEvent(Order o) {
        LOG.info("Promotion Received: {}", o);
        switch (o.getStatus()) {
            case "COMPLETE" -> {
//                var pro = promotionService.findById(o.getPromotionId());
//                pro.setUsedCount(pro.getUsedCount() + 1);
//                promotionService.create(pro);
//                LOG.info("Promotion UPDATE: {}", pro);
            }
        }
    }
}