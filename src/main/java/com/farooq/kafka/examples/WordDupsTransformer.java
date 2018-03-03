package com.farooq.kafka.examples;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

public class WordDupsTransformer implements Transformer<String, String, KeyValue<String, String>> {

    private ProcessorContext processorContext;
    private KeyValueStore<String,String> keyValueStore;

    public void init(ProcessorContext processorContext) {
        this.processorContext = processorContext;

        //.keyValueStore = (KeyValueStore<String,String>) processorContext.getStateStore("");
    }

    public KeyValue<String, String> transform(String key, String value) {
        //String oldValue = this.keyValueStore.get(key);
        System.out.println("Key value " + key +" value "+ value);

        return new KeyValue<>(key,value+"transformed");
    }

    public KeyValue<String, String> punctuate(long l) {
        return null;
    }

    public void close() {

    }
}
