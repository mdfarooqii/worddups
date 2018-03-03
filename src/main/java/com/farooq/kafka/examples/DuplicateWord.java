package com.farooq.kafka.examples;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

import java.util.Optional;
import java.util.Properties;

public class DuplicateWord {
    public static void main(String[] args) {

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "ktable-application-new-4");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        //config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        KStreamBuilder kStreamBuilder = new KStreamBuilder();


        //Create a statestore for transformer
        StateStoreSupplier countStore = Stores.create("word_dups")
                .withKeys(Serdes.String())
                .withValues(Serdes.String())
                .persistent()
                .build();

        kStreamBuilder.addStateStore(countStore);


        KStream<String, String> inputStream = kStreamBuilder.stream("input-words-topic-1");
        KStream<String, String> outputStream = inputStream.transform(new WordCountTransformerSupplier(countStore.name()), countStore.name());
        outputStream.print(Serdes.String(),Serdes.String());

        KafkaStreams streams = new KafkaStreams(kStreamBuilder, config);
        streams.start();


        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        while(true){
            System.out.println(streams.toString());
            try {
                Thread.sleep(45000);
            } catch (InterruptedException e) {
                break;
            }
        }





    }


    private static final class WordCountTransformerSupplier
            implements TransformerSupplier<String, String, KeyValue<String, String>> {

        final private String stateStoreName;

        public WordCountTransformerSupplier(String stateStoreName) {
            this.stateStoreName = stateStoreName;
        }

        @Override
        public Transformer<String, String, KeyValue<String, String>> get() {
            return new Transformer<String, String, KeyValue<String, String>>() {

                private KeyValueStore<String, String> stateStore;

                @SuppressWarnings("unchecked")
                @Override
                public void init(ProcessorContext context) {
                    stateStore = (KeyValueStore<String, String>) context.getStateStore(stateStoreName);
                }

                @Override
                public KeyValue<String, String> transform(String key, String value) {
                    // For simplification (and unlike the traditional wordcount) we assume that the value is
                    // a single word, i.e. we don't split the value by whitespace into potentially one or more
                    // words.

                    String oldValue = stateStore.get(key);
                    System.out.println(String.format("oldvalue [%s] for key [%s]",oldValue,key));
                    if(oldValue!=null && oldValue.equalsIgnoreCase(value)){
                        System.out.println("inside match value["+value +"] oldvalue ["+oldValue+"]");
                       return null;
                    }
                    stateStore.put(key,value);
                    return new KeyValue<>(key,value);
                }

                @Override
                public KeyValue<String, String> punctuate(long timestamp) {
                    // Not needed
                    return null;
                }

                @Override
                public void close() {
                    // Note: The store should NOT be closed manually here via `stateStore.close()`!
                    // The Kafka Streams API will automatically close stores when necessary.
                }
            };
        }

    }

}
