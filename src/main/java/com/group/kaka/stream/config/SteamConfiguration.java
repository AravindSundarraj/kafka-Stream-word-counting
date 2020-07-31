package com.group.kaka.stream.config;

import com.group.kaka.stream.domain.Count;
import com.group.kaka.stream.partitioner.CountRepartitioner;
import com.group.kaka.stream.transformer.CountTransformer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class SteamConfiguration {

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaProperties.getClientId());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
/*
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
*/
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KStream<String, Count> kStream(StreamsBuilder kStreamBuilder) {
        KStream<String, String> stream = kStreamBuilder.stream("WORD_TOPIC");

        KStream<String, String> streamKeyValue = stream.through("INTERMEDIATE_TOPIC" ,
        Produced.with(Serdes.String() , Serdes.String() , new CountRepartitioner()) );

        StoreBuilder<KeyValueStore<String, Long>> wordCountsStore =
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("count-store"),
                        Serdes.String(),
                        Serdes.Long())
                        .withCachingEnabled();

        kStreamBuilder.addStateStore(wordCountsStore);

        KStream<String, String> word = streamKeyValue.flatMapValues((v) ->
                Arrays.asList(v.split(" ")));

        KStream<String, Count> c = word.transformValues(() -> new CountTransformer("count-store")
                , "count-store");

        c.peek((k, s) ->
                System.out.println("The count is :" + s)
        );


        return c;


    }


}
