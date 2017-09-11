package com.skspruce.detect.utils;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartitionInfo;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class KafkaUtil {

    private static String DEFAULT_BOOTSTRAP_SERVERS = "Spark1:9092,Spark2:9092,Spark3:9092";

    private static Properties adminProp = null;
    private static Properties producerProp = null;
    private static Properties consumerProp = null;
    private static Producer producer = null;
    private static KafkaConsumer consumer = null;


    static {
        adminProp = new Properties();
        adminProp.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                PropertiesUtil.getInstance().getString(PropertiesUtil.KAFKA_BOOTSTRAP_SERVERS,DEFAULT_BOOTSTRAP_SERVERS));

        producerProp = new Properties();
        producerProp.put("bootstrap.servers", PropertiesUtil.getInstance().getString(PropertiesUtil.KAFKA_BOOTSTRAP_SERVERS,DEFAULT_BOOTSTRAP_SERVERS));
        producerProp.put("acks", "all");
        producerProp.put("retries", 3);
        producerProp.put("batch.size", 16384);
        producerProp.put("linger.ms", 1);
        producerProp.put("buffer.memory", 33554432);
        producerProp.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProp.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        //org.apache.kafka.common.serialization.ByteArraySerializer
        //org.apache.kafka.common.serialization.ByteArrayDeserializer

        consumerProp = new Properties();
        consumerProp.put("bootstrap.servers", PropertiesUtil.getInstance().getString(PropertiesUtil.KAFKA_BOOTSTRAP_SERVERS,DEFAULT_BOOTSTRAP_SERVERS));
        consumerProp.put("group.id", "test");
        consumerProp.put("enable.auto.commit", "true");
        consumerProp.put("auto.commit.interval.ms", "1000");
        consumerProp.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProp.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
    }

    public static void main(String[] args) throws Exception {
        //boolean isCreated = createTopic("test", 3, (short) 2);
        System.out.println("###########");
        listAllTopics();
        System.out.println("###########");
        describeTopic("test");
        System.out.println("###########");
        deleteTopic("test");
        //System.out.println("isCreated:"+isCreated);
    }

    public synchronized static Producer getProducer() {
        if (producer == null) {
            producer = new KafkaProducer(producerProp);
        }
        return producer;
    }

    public synchronized static KafkaConsumer getConsumer() {
        if (consumer == null) {
            consumer = new KafkaConsumer(consumerProp);
        }
        return consumer;
    }

    /**
     * 创建topic
     *
     * @param topicName
     * @param numPartitions
     * @param replicationFactor
     */
    public static boolean createTopic(String topicName, int numPartitions, short replicationFactor) {
        boolean flag = true;
        try (AdminClient adminClient = AdminClient.create(adminProp)) {
            NewTopic nt = new NewTopic(topicName, numPartitions, replicationFactor);
            CreateTopicsResult result = adminClient.createTopics(Collections.singletonList(nt));
            try {
                result.all().get();
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            }
        }
        return flag;
    }

    public static void describeTopic(String topicName) throws Exception {
        try (AdminClient adminClient = AdminClient.create(adminProp)) {
            DescribeTopicsResult result = adminClient.describeTopics(Collections.singletonList(topicName));
            Map<String, TopicDescription> descs = result.all().get();
            for (Map.Entry<String, TopicDescription> entry : descs.entrySet()) {
                System.out.println(entry.getKey());
                TopicDescription value = entry.getValue();
                System.out.println(value.name());
                for (TopicPartitionInfo info : value.partitions()) {
                    System.out.println("leader:\t" + info.leader().host() + "\t" + info.leader().idString());
                    System.out.println("partition:\t" + info.partition());
                }
            }
        }
    }

    public static void listAllTopics() throws Exception {
        try (AdminClient adminClient = AdminClient.create(adminProp)) {
            ListTopicsOptions lto = new ListTopicsOptions();
            lto.listInternal(true);
            Set<String> topics = adminClient.listTopics(lto).names().get();
            for (String topic : topics) {
                System.out.println(topic);
            }
        }
    }

    public static boolean deleteTopic(String topicName) {
        boolean flag = true;
        try (AdminClient adminClient = AdminClient.create(adminProp)) {
            KafkaFuture<Void> all = adminClient.deleteTopics(Collections.singletonList(topicName)).all();
            try {
                all.get();
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            }
        }
        return flag;
    }
}
