package com.skspruce.detect.utils;

import com.datastax.driver.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * cassandra操作工具类
 */
public class CassandraUtil {
    private static Logger logger = LoggerFactory.getLogger(CassandraUtil.class);

    private static Cluster cluster = null;
    private static Session session = null;
    private static String DEFAULT_HOSTS = "192.168.11.2";

    static {
        PoolingOptions poolingOptions = new PoolingOptions();
        //设置每个连接最大请求数
        poolingOptions.setMaxRequestsPerConnection(HostDistance.LOCAL, 32);
        //设置与集群中机器最少两个连接
        poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, 2);
        //设置与集群中机器最多四个连接
        poolingOptions.setMaxConnectionsPerHost(HostDistance.LOCAL, 4);
        //配置hosts
        String[] hosts = PropertiesUtil.getInstance().getString(PropertiesUtil.CASSANDRA_URL, DEFAULT_HOSTS).split(",");
        Cluster.Builder builder = Cluster.builder();
        for (String host : hosts) {
            builder.addContactPoints(host);
        }
        cluster = builder.withPoolingOptions(poolingOptions).build();
    }

    /**
     * 获取连接session,查询时请在CQL中包含keySpace
     *
     * @return {@code Session}
     */
    public synchronized static Session getSession() {
        if (session == null || session.isClosed()) {
            session = cluster.connect();
        }

        return session;
    }

    /**
     * 获取cluster
     *
     * @return {@code Cluster}
     */
    public synchronized static Cluster getCluster() {
        return cluster;
    }

    /**
     * 关闭集群所有连接
     */
    public static void close() {
        if (cluster != null) {
            cluster.close();
        }
    }

    /**
     * 创建键空间
     *
     * @param name              keySpace name
     * @param strategy          strategy class
     * @param replicationFactor replication factor
     * @return true or false
     */
    public static boolean createKeySpace(String name, String strategy, String replicationFactor) {
        boolean flag = true;
        String cql = "create keyspace " + name
                + " with replication={'class':'" + strategy + "','replication_factor':" + replicationFactor + "};";
        try {
            getSession().execute(cql);
            logger.info("created keySpace " + name + ",CQL is:" + cql);
        } catch (Exception e) {
            logger.error("create keySpace error,CQL is" + cql, e);
            flag = false;
        }
        return flag;
    }

    /**
     * 判断键空间是否存在
     *
     * @param keySpace
     * @return true or false
     */
    public static boolean isKSExists(String keySpace) {
        Set<String> kys = new HashSet<>();
        try {
            List<KeyspaceMetadata> keyspaces = cluster.getMetadata().getKeyspaces();
            for (KeyspaceMetadata km : keyspaces) {
                kys.add(km.getName());
            }
        } catch (Exception e) {
            logger.error("get cluster metadata error,KeySpace is:" + keySpace, e);
        }
        return kys.contains(keySpace);
    }

    /**
     * 删除键空间
     *
     * @param keySpace
     * @return true or false
     */
    public static boolean dropKeySpace(String keySpace) {
        String cql = "drop keyspace " + keySpace;
        boolean flag = true;
        try {
            getSession().execute(cql);
        } catch (Exception e) {
            logger.error("dropKeySpace error,CQL is:" + cql, e);
        }
        return flag;
    }

    /**
     * 查询数据,返回迭代器
     *
     * @param cql
     * @return {@code Iterator<Row>} or null
     */
    public static Iterator<Row> queryToIterator(String cql) {
        Iterator<Row> iter = null;
        try {
            iter = getSession().execute(cql).iterator();
        } catch (Exception e) {
            logger.error("queryToIterator error,CQL is:" + cql, e);
        }
        return iter;
    }

    /**
     * 查询数据,返回list
     *
     * @param cql
     * @return {@code List<Row>}
     */
    public static List<Row> queryToList(String cql) {
        List<Row> list = null;
        try {
            list = getSession().execute(cql).all();
        } catch (Exception e) {
            logger.error("queryToList error,CQL is:" + cql, e);
        }
        return list;
    }

    /**
     * 查询数据,返回排序后第一条
     *
     * @param cql
     * @return {@code Row}
     */
    public static Row queryToOne(String cql) {
        Row row = null;
        try {
            row = getSession().execute(cql).one();
        } catch (Exception e) {
            logger.error("queryToOne error,CQL is:" + cql, e);
        }
        return row;
    }

    /**
     * 数据插入或更新
     *
     * @param cql
     * @return true or false
     */
    public static boolean insertOrUpdate(String cql) {
        boolean flag = true;
        try {
            getSession().execute(cql);
        } catch (Exception e) {
            logger.error("insertOrUpdate data to cassandra error,CQL is:" + cql, e);
            flag = false;
        }
        return flag;
    }

    /**
     * 批量插入或更新
     *
     * @param cqls {@code List<String>}
     * @return true or false
     */
    public static boolean batchInsertOrUpdate(List<String> cqls) {
        boolean flag = true;
        BatchStatement batch = new BatchStatement();
        try {
            for (String cql : cqls) {
                batch.add(new SimpleStatement(cql));
            }
            getSession().execute(batch);
        } catch (Exception e) {
            logger.error("batchInsertOrUpdate data to cassandra error,CQL like:" + cqls.get(0), e);
            flag = false;
        }
        return flag;
    }

}
