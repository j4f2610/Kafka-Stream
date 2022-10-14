package vn.eton.promotion.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import vn.eton.promotion.domain.Promotion;
import vn.eton.promotion.service.PromotionService;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionController.class);
    private AtomicLong id = new AtomicLong();
    //    private KafkaTemplate<Long, Order> template;
//    private StreamsBuilderFactoryBean kafkaStreamsFactory;
    private final PromotionService promotionService;


//    public PromotionController(KafkaTemplate<Long, Order> template,
//                               StreamsBuilderFactoryBean kafkaStreamsFactory,
//                               OrderGeneratorService orderGeneratorService) {
//        this.template = template;
//        this.kafkaStreamsFactory = kafkaStreamsFactory;
//        this.orderGeneratorService = orderGeneratorService;
//    }

    @PostMapping
    public Promotion create(@RequestBody Promotion promotion) {
        promotion.setId(id.incrementAndGet());
        var pro = promotionService.create(promotion);
        LOG.info("Create promotion: {}", pro);
        return pro;
    }

//    @PostMapping("/generate")
//    public boolean create() {
//        orderGeneratorService.generate();
//        return true;
//    }

    @GetMapping
    public Iterable<Promotion> all() {
        return promotionService.getList();
    }
}
