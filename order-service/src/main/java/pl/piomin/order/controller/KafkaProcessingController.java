package pl.piomin.order.controller;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Properties;

@RestController
public class KafkaProcessingController {

    private KafkaStreams streamsInnerJoin;
    private KafkaStreams streamsLeftJoin;
    private KafkaStreams streamsOuterJoin;
    private KafkaStreams streamTableInnerJoin;
    private KafkaStreams streamTableLeftJoin;

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

    @RequestMapping("/startStreamStreamLeftJoin/")
    public void startStreamStreamLeftJoin() {

        stop();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-stream-left-join");
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

    @RequestMapping("/startStreamStreamOuterJoin/")
    public void startStreamStreamOuterJoin() {

        stop();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-stream-outer-join");
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

    @RequestMapping("/startStreamTableInnerJoin/")
    public void startStreamTableInnerJoin() {

        stop();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-table-inner-join");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> leftSource = builder.stream("my-kafka-left-stream-topic");
        KTable<String, String> rightSource = builder.table("my-kafka-right-stream-topic");

        KStream<String, String> joined = leftSource.join(rightSource,
                (leftValue, rightValue) -> "left=" + leftValue + ", right=" + rightValue /* ValueJoiner */
        );

        joined.to("my-kafka-stream-table-inner-join-out");

        final Topology topology = builder.build();
        streamTableInnerJoin = new KafkaStreams(topology, props);
        streamTableInnerJoin.start();

    }

    @RequestMapping("/startStreamTableLeftJoin/")
    public void startStreamTableLeftJoin() {

        stop();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-table-left-join");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> leftSource = builder.stream("my-kafka-left-stream-topic");
        KTable<String, String> rightSource = builder.table("my-kafka-right-stream-topic");

        KStream<String, String> joined = leftSource.leftJoin(rightSource,
                (leftValue, rightValue) -> "left=" + leftValue + ", right=" + rightValue /* ValueJoiner */
        );

        joined.to("my-kafka-stream-table-left-join-out");

        final Topology topology = builder.build();
        streamTableLeftJoin = new KafkaStreams(topology, props);
        streamTableLeftJoin.start();

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
        if (streamTableInnerJoin != null) {
            streamTableInnerJoin.close();
        }
        if (streamTableLeftJoin != null) {
            streamTableLeftJoin.close();
        }
    }

}
