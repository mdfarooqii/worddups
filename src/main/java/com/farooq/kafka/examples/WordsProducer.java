package com.farooq.kafka.examples;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class WordsProducer {
    public static void main(String[] args) {
        Properties properties = new Properties();

        // kafka bootstrap server
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        /*properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
*/
        Producer<String, String> producer = new KafkaProducer<>(properties);

        int i = 0;
        while (i <3) {
            System.out.println("Producing batch: " + i);
            try {
                System.out.println("Producing batch: " + i);
                producer.send(new ProducerRecord<>("input-words-topic-1", "farooq_key", "farooq"));
                Thread.sleep(1000);
                producer.send(newRandomString("aysha"));
                Thread.sleep(1000);
                producer.send(newRandomString("hiba"));
                Thread.sleep(1000);
                producer.send(newRandomString("shaina"));
                Thread.sleep(10000);
                producer.send(newRandomString("Srini-D-after-duplicates-"+i));
                System.out.println("sent batch: " + i);
                i += 1;

            } catch (InterruptedException e) {
                break;
            }
        }
        producer.close();
    }

    public static ProducerRecord<String, String> newRandomString(String name) {
        // creates an empty json {}
       /* ObjectNode transaction = JsonNodeFactory.instance.objectNode();

        // we write the data to the json document
        transaction.put("name", name);*/

        return new ProducerRecord<>("input-words-topic-1", "after_success_"+name+"_key", name);
    }
}
