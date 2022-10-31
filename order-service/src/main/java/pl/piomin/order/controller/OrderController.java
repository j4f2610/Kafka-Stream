package pl.piomin.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;
import pl.piomin.base.domain.Order;
import pl.piomin.order.service.OrderGeneratorService;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);
    private AtomicLong id = new AtomicLong();
    private KafkaTemplate<Long, String> template;
    private StreamsBuilderFactoryBean kafkaStreamsFactory;
    private OrderGeneratorService orderGeneratorService;

    public OrderController(KafkaTemplate<Long, String> template,
                           StreamsBuilderFactoryBean kafkaStreamsFactory,
                           OrderGeneratorService orderGeneratorService) {
        this.template = template;
        this.kafkaStreamsFactory = kafkaStreamsFactory;
        this.orderGeneratorService = orderGeneratorService;
    }

    @PostMapping
    public Order create(@RequestBody Order order) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        order.setId(id.incrementAndGet());
        for (int i = 0; i < 100; i++) {
            template.send("orders", order.getId(), objectMapper.writeValueAsString(order)).addCallback(new KafkaSendCallback<>() {

                @Override
                public void onSuccess(SendResult<Long, String> result) {
                    LOG.info("=====onSuccess: {}", result);
                }

                @Override
                public void onFailure(KafkaProducerException ex) {
                    LOG.info("=====onFailure: {}", (Object) ex.getStackTrace());
                }
            });
        }
        return order;
    }

    @PutMapping
    public Order put(@RequestBody Order order) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        template.send("orders", order.getId(), objectMapper.writeValueAsString(order));
        LOG.info("Sent: {}", order);
        return order;
    }

    @PostMapping("/generate")
    public boolean create() {
        orderGeneratorService.generate();
        return true;
    }

    @GetMapping
    public List<Order> all() {
        List<Order> orders = new ArrayList<>();
        ReadOnlyKeyValueStore<Long, Order> store = kafkaStreamsFactory
                .getKafkaStreams()
                .store(StoreQueryParameters.fromNameAndType(
                        "orders",
                        QueryableStoreTypes.keyValueStore()));
        KeyValueIterator<Long, Order> it = store.all();
        it.forEachRemaining(kv -> orders.add(kv.value));
        return orders;
    }

    private static final String KEY = "FIXED-KEY";

    private static final Map<Integer, String> LEFT;
    static {
        LEFT = new HashMap<>();
        LEFT.put(1, null);
        LEFT.put(3, "A");
        LEFT.put(5, "B");
        LEFT.put(7, null);
        LEFT.put(9, "C");
        LEFT.put(12, null);
        LEFT.put(15, "D");
    }

    private static final Map<Integer, String> RIGHT;
    static {
        RIGHT = new HashMap<>();
        RIGHT.put(2, null);
        RIGHT.put(4, "a");
        RIGHT.put(6, "b");
        RIGHT.put(8, null);
        RIGHT.put(10, "c");
        RIGHT.put(11, null);
        RIGHT.put(13, null);
        RIGHT.put(14, "d");
    }

    @RequestMapping("/sendMessages/")
    public void sendMessages() {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);

        try {
            for (int i = 0; i < 15; i++) {
                // Every 10 seconds send a message
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {}

                if (LEFT.containsKey(i + 1)) {
                    producer.send(new ProducerRecord<String, String>("my-kafka-left-stream-topic", KEY, LEFT.get(i + 1)));
                }
                if (RIGHT.containsKey(i + 1)) {
                    producer.send(new ProducerRecord<String, String>("my-kafka-right-stream-topic", KEY, RIGHT.get(i + 1)));
                }

            }
        } finally {
            producer.close();
        }

    }

    private KafkaStreams streamsInnerJoin;

    @RequestMapping("/startStreamStreamInnerJoin/")
    public void startStreamStreamInnerJoin() {

        stop();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-stream-inner-join");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> leftSource = builder.stream("my-kafka-left-stream-topic");
        KStream<String, String> rightSource = builder.stream("my-kafka-right-stream-topic");

        KStream<String, String> joined = leftSource.join(rightSource,
                (leftValue, rightValue) -> "left=" + leftValue + ", right=" + rightValue, /* ValueJoiner */
                JoinWindows.of(Duration.ofMinutes(5))
                /* right value */
        );

        joined.to("my-kafka-stream-stream-inner-join-out");

        final Topology topology = builder.build();
        streamsInnerJoin = new KafkaStreams(topology, props);
        streamsInnerJoin.start();

    }
    private KafkaStreams streamsLeftJoin;
    @RequestMapping("/startStreamStreamLeftJoin/")
    public void startStreamStreamLeftJoin() {
        stop();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-stream-inner-join");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> leftSource = builder.stream("my-kafka-left-stream-topic");
        KStream<String, String> rightSource = builder.stream("my-kafka-right-stream-topic");

        KStream<String, String> joined = leftSource.leftJoin(rightSource,
                (leftValue, rightValue) -> "left=" + leftValue + ", right=" + rightValue, /* ValueJoiner */
                JoinWindows.of(Duration.ofMinutes(5))
                /* right value */
        );

        joined.to("my-kafka-stream-stream-left-join-out");
        final Topology topology = builder.build();
        streamsLeftJoin = new KafkaStreams(topology, props);
        streamsLeftJoin.start();
    }
    private KafkaStreams streamsOuterJoin;
    @RequestMapping("/startStreamStreamOuterJoin/")
    public void startStreamStreamOuterJoin() {
        stop();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-stream-inner-join");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> leftSource = builder.stream("my-kafka-left-stream-topic");
        KStream<String, String> rightSource = builder.stream("my-kafka-right-stream-topic");

        KStream<String, String> joined = leftSource.outerJoin(rightSource,
                (leftValue, rightValue) -> "left=" + leftValue + ", right=" + rightValue, /* ValueJoiner */
                JoinWindows.of(Duration.ofMinutes(5))
                /* right value */
        );

        joined.to("my-kafka-stream-stream-outer-join-out");
        final Topology topology = builder.build();
        streamsOuterJoin = new KafkaStreams(topology, props);
        streamsOuterJoin.start();
    }
    private void stop () {
        if (streamsInnerJoin != null) {
            streamsInnerJoin.close();
        }
        if (streamsLeftJoin != null) {
            streamsLeftJoin.close();
        }
        if (streamsOuterJoin != null) {
            streamsOuterJoin.close();
        }
//        if (streamTableInnerJoin != null) {
//            streamTableInnerJoin.close();
//        }
//        if (streamTableLeftJoin != null) {
//            streamTableLeftJoin.close();
//        }
    }
}
