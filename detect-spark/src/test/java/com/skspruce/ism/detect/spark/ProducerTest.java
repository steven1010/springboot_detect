package com.skspruce.ism.detect.spark;

import com.skspruce.ism.detect.spark.utils.KafkaUtil;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Random;

public class ProducerTest {

    public static void main(String[] args) {
        sendData();
    }

    public static void sendData() {
        Producer producer = KafkaUtil.getProducer();
        for (int i = 0; i < 100; i++)
            producer.send(new ProducerRecord<String, String>("test" + (new Random().nextInt(2) + 1), "rtls", Integer.toString(i)));

        producer.close();
    }
}
