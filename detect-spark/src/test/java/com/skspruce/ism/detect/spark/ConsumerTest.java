package com.skspruce.ism.detect.spark;

import com.skspruce.ism.detect.spark.utils.BytesUtil;
import com.skspruce.ism.detect.spark.utils.KafkaUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;

public class ConsumerTest {
    public static void main(String[] args) {
        getData();
    }

    public static void getData(){
        KafkaConsumer consumer = KafkaUtil.getConsumer();
        consumer.subscribe(Arrays.asList("DetectData_User"));
        while (true) {
            ConsumerRecords<String, byte[]> records = consumer.poll(100);
            for (ConsumerRecord<String, byte[]> record : records) {
                System.out.printf("topic = %s, offset = %d, key = %s, value = %s%n", record.topic(), record.offset(), record.key(), record.value());
                byte[] value = record.value();
                byte[] copy = BytesUtil.copy(value, 8, 6);
                System.out.println(BytesUtil.toHex(copy));
            }
        }
    }
}
