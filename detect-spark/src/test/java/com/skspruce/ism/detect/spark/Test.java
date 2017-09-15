package com.skspruce.ism.detect.spark;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.skspruce.ism.detect.spark.utils.*;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class Test {
    public static void main(String[] args) throws Exception {
        //ESUtil.createMapping("monitor_index", "monitor_detect", ESUtil.getMapping());
        //insert2ES();
        //ESUtil.getIndex("monitor_index","monitor_detect","AV5ggGXTrFqvLzbCEtVU");
        sendData();
        //CassandraUtil.createKeySpace("test","SimpleStrategy","2");
        //Row row = CassandraUtil.queryToOne("select * from test.monitor_result where mas='test'");
        //System.out.println(row.getLong("begin_time"));
    }

    public static void insert2ES() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("天府广场A出口");
        list.add("天府广场B出口");
        list.add("天府软件园A区");
        list.add("成都科学城");
        list.add("四川科技管");
        list.add("阿里成都研究院");
        list.add("天华路");
        list.add("天府大道");
        list.add("华府大道");

        Random random = new Random();

        for (int i = 0; i < 30; i++) {
            XContentBuilder builder = jsonBuilder().startObject()
                    .field("user_mac", "a" + random.nextInt(9) + ":bb:cc:d" + random.nextInt(9) + ":ee:ff")
                    .field("area_id", random.nextInt(50))
                    .field("begin_time", System.currentTimeMillis())
                    .field("area_name", list.get(random.nextInt(9)))
                    .field("strategy_id", random.nextInt(30))
                    .endObject();
            ESUtil.addIndex("monitor_index", "monitor_detect", builder);
        }
    }

    public static void testConnection() {
        Session session = CassandraUtil.getSession();
        System.out.println(session.getLoggedKeyspace());
    }

    public static void sendData() throws Exception {
        Producer producer = KafkaUtil.getProducer();

        Cluster cluster = Cluster.builder().addContactPoints("192.168.20.155").build();
        List<Row> data = cluster.connect().execute("select day,time,ap_mac,content from ias.rtls_by_time limit 10000;").all();

        //List<Row> data = CassandraUtil.queryToList("select day,time,ap_mac,content from ias.rtls_by_time limit 100;");
        Random random = new Random();

        int index = 0;
        do {
            for (Row next : data) {
                Thread.sleep(1 * 1);
                System.out.println(next.getInt("day") + "\t" + next.getTimestamp("time").getTime() + "\t" + next.getLong("ap_mac"));
                System.out.println(next.getBytes("content"));
                ByteBuffer content = next.getBytes("content");
                byte[] apByte = new byte[6];
                content.position(8);
                content.get(apByte, 0, 6);
                for (byte b : apByte) {
                    System.out.println(b);
                }
                System.out.println("ap_mac:" + BytesUtil.toHex(apByte));

                byte[] targetByte = new byte[6];
                content.position(28);
                content.get(targetByte, 0, 6);
                for (byte b : targetByte) {
                    System.out.println(b);
                }
                System.out.println("user_mac:" + BytesUtil.toHex(targetByte));

                /*int index = random.nextInt(1000);
                if (index < 400) {
                    try (Connection conn = SQLHelper.getInstance().getDetectConnection()) {
                        conn.prepareStatement("insert into strategy (name,mac) values ('test_"
                                + random.nextInt(10000) + "','"
                                + MacUtil.formatMac(BytesUtil.toHex(targetByte)) + "')").execute();
                    }
                }*/

                byte[] kafkaByte = new byte[content.capacity()];
                content.flip();
                content.get(kafkaByte, 0, content.limit());
                for (int i = 0; i < 10; i++) {
                    producer.send(new ProducerRecord("test" + (random.nextInt(2) + 1),
                            "RTLS_" + BytesUtil.toHex(apByte) +
                                    "_" + BytesUtil.toHex(targetByte) +
                                    "_" + System.currentTimeMillis(), kafkaByte));
                }
            }
            index++;
        } while (index < 100);
    }
}
