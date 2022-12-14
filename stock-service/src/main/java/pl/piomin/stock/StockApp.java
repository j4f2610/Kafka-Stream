package pl.piomin.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import pl.piomin.base.domain.Order;
import pl.piomin.stock.domain.Product;
import pl.piomin.stock.repository.ProductRepository;
import pl.piomin.stock.service.OrderManageService;

import javax.annotation.PostConstruct;
import java.util.Random;

@SpringBootApplication
@EnableKafka
public class StockApp {

    private static final Logger LOG = LoggerFactory.getLogger(StockApp.class);

    public static void main(String[] args) {
        SpringApplication.run(StockApp.class, args);
    }

    @Autowired
    OrderManageService orderManageService;
    @Autowired
    private KafkaTemplate<Long, Order> template;
    @KafkaListener(id = "orders", topics = "orders", groupId = "stock")
    public void onEvent(Order o) {
        LOG.info("Received: {}" , o);
        o.setStatus("REJECT");
        template.send("stock-orders", o.getId(), o);
        LOG.info("STOCK VERIFY: {}", o);
    }

    @Autowired
    private ProductRepository repository;

    @PostConstruct
    public void generateData() {
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            int count = r.nextInt(1000);
            Product p = new Product(null, "Product" + i, count, 0);
            repository.save(p);
        }
    }
}
