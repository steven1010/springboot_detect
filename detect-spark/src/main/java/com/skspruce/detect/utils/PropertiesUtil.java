package com.skspruce.detect.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * 读取配置文件
 */
public class PropertiesUtil {
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static PropertiesUtil instance = null;
    private Properties prop = null;

    public static String FILE_NAME = "detect.properties";
    public static String ES_CLUSTER_NAME = "es.cluster.name";
    public static String ES_CLUSTER_HOSTS = "es.cluster.hosts";
    public static String ES_CLUSTER_CLIENT_INIT = "es.cluster.client.init";
    public static String ES_CLUSTER_INDEX = "es.cluster.index";
    public static String ES_CLUSTER_TYPE = "es.cluster.type";
    public static String CASSANDRA_URL = "cassandra.hosts";
    public static String KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap_servers";

    public static void main(String[] args) {
        System.out.println(getInstance().getString(ES_CLUSTER_NAME));
    }

    private PropertiesUtil() {
        prop = new Properties();
        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream(FILE_NAME));
        } catch (IOException e) {
            logger.error("read properties file error:", e);
            throw new RuntimeException(e);
        }
        logger.info("init properties file " + FILE_NAME);
    }

    public synchronized static PropertiesUtil getInstance() {
        if (instance == null) {
            instance = new PropertiesUtil();
        }
        return instance;
    }

    public String getString(String key) {
        return prop.getProperty(key).trim();
    }

    public String getString(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue).trim();
    }

    public Integer getInteger(String key) {
        return Integer.valueOf(prop.getProperty(key).trim());
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return Integer.valueOf(prop.getProperty(key, defaultValue.toString()).trim());
    }
}
