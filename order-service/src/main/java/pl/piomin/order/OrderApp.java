package pl.piomin.order;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pl.piomin.base.domain.Order;
import pl.piomin.order.service.OrderManageService;

import java.time.Duration;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableKafkaStreams
@EnableAsync
@EnableKafka
public class OrderApp {

    private static final Logger LOG = LoggerFactory.getLogger(OrderApp.class);

    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }

    @Bean
    public NewTopic orders() {
        return TopicBuilder.name("orders")
                .partitions(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name("payment-orders")
                .partitions(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic stockTopic() {
        return TopicBuilder.name("stock-orders")
                .partitions(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic promotionTopic() {
        return TopicBuilder.name(PROMOTION_ORDER)
                .partitions(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic myTopic() {
        return TopicBuilder.name("my-kafka-stream-stream-inner-join-out")
                .partitions(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic myLeftTopic() {
        return TopicBuilder.name("my-kafka-stream-stream-left-join-out")
                .partitions(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic myOuterTopic() {
        return TopicBuilder.name("my-kafka-stream-stream-outer-join-out")
                .partitions(3)
                .compact()
                .build();
    }

    @Autowired
    OrderManageService orderManageService;
    public final static String PROMOTION_ORDER = "promotion-orders";
    public final static String PAYMENT_ORDER = "payment-orders";
    public final static String STOCK_ORDER = "stock-orders";

    @Bean
    public KStream<Long, Order> stream(StreamsBuilder builder) {
        JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);
        KStream<Long, Order> stream = builder.stream(PAYMENT_ORDER, Consumed.with(Serdes.Long(), orderSerde));
        stream.join(builder.stream(STOCK_ORDER),
                        orderManageService::result,
                        JoinWindows.of(Duration.ofSeconds(50)),
                        StreamJoined.with(Serdes.Long(), orderSerde, orderSerde))
                .peek((k, o) -> LOG.info("Output COMBINE: {}", o));
        return stream;
    }

    @Bean
    public KStream<Long, Order> streamPromotion(StreamsBuilder builder) {
        JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);
        KStream<Long, Order> stream = builder.stream(PROMOTION_ORDER, Consumed.with(Serdes.Long(), orderSerde))
                .peek((k, o) -> LOG.info("Output COMBINE: {}", o));
        return stream;
    }

    @Bean
    public KTable<Long, Order> table(StreamsBuilder builder) {
        KeyValueBytesStoreSupplier store =
                Stores.persistentKeyValueStore("orders");
        JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);
        KStream<Long, Order> stream = builder
                .stream("orders", Consumed.with(Serdes.Long(), orderSerde));
        return stream.toTable(Materialized.<Long, Order>as(store)
                .withKeySerde(Serdes.Long())
                .withValueSerde(orderSerde));
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("kafkaSender-");
        executor.initialize();
        return executor;
    }
}
