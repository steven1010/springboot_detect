package com.skspruce.ism.detect.webapi.strategy.util;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * ES操作工具类
 */
public class ESUtil {
    private static Logger logger = LoggerFactory.getLogger(ESUtil.class);

    private static TransportClient client = null;


    static {
        //不明觉厉......
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        Settings settings = Settings.builder()
                .put("cluster.name", PropertiesUtil.getInstance().getString(PropertiesUtil.ES_CLUSTER_NAME, "elasticsearch")).build();
        try {
            String[] hosts = PropertiesUtil.getInstance().getString(PropertiesUtil.ES_CLUSTER_HOSTS, "master:9300").split(",");
            List<InetSocketTransportAddress> list = new ArrayList<>(hosts.length);
            for (String host : hosts) {
                String[] hp = host.split(":");
                list.add(new InetSocketTransportAddress(InetAddress.getByName(hp[0]), Integer.valueOf(hp[1])));
            }
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddresses(list.toArray(new InetSocketTransportAddress[list.size()]));
        } catch (UnknownHostException e) {
            logger.error("connect elasticsearch error:", e);
        }
    }

    public static TransportClient getClient(){
        return client;
    }

    /**
     * 关闭连接
     */
    public static void close() {
        client.close();
    }

    /**
     * 创建index
     *
     * @param index
     * @param shards
     * @param replicas
     * @return true or false
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static boolean createIndex(String index, int shards, int replicas) throws ExecutionException, InterruptedException {
        if (!isIndexExists(index)) {
            CreateIndexResponse response = client.admin().indices().prepareCreate(index)
                    .setSettings(Settings.builder().put("number_of_shards", shards).put("number_of_replicas", replicas)).execute().get();

            return response.isAcknowledged();
        }
        return true;
    }

    /**
     * 判断index是否存在
     *
     * @param index
     * @return true or false
     */
    public static boolean isIndexExists(String index) {
        boolean flag = true;
        try {
            IndicesExistsRequest request = new IndicesExistsRequest(index);
            IndicesExistsResponse exists = client.admin().indices().exists(request).actionGet();
            flag = exists.isExists();
        } catch (Exception e) {
            flag = false;
            logger.error("is index exists error:", e);
        }
        return flag;
    }

    /**
     * 判断index是否存在
     *
     * @param index
     * @param type
     * @return true or false
     */
    public static boolean isTypeExists(String index, String type) {
        boolean flag = true;
        try {
            TypesExistsRequest request = new TypesExistsRequest(new String[]{index}, type);
            TypesExistsResponse response = client.admin().indices().typesExists(request).actionGet();
            flag = response.isExists();
        } catch (Exception e) {
            flag = false;
            logger.error("is type exists error:", e);
        }
        return flag;
    }

    /**
     * 创建mapping
     *
     * @param index
     * @param type
     * @param mapping
     * @return true
     */
    public static boolean createMapping(String index, String type, XContentBuilder mapping) {
        boolean flag = true;
        try {
            PutMappingRequest request = Requests.putMappingRequest(index).type(type).source(mapping);
            PutMappingResponse response = client.admin().indices().putMapping(request).actionGet();
        } catch (Exception e) {
            logger.error("create mapping error:", e);
            flag = false;
        }
        return flag;
    }

    /**
     * 仅用于获取创建strategy_event的mapping
     *
     * @return
     * @throws IOException
     */
    public static XContentBuilder getMapping() throws IOException {
        XContentBuilder builder = jsonBuilder()
                .startObject()
                .startObject("properties")
                .startObject("user_mac").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject()
                .startObject("begin_time").field("type", "long").field("store", "yes").field("index", "not_analyzed").endObject()
                .startObject("area_id").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
                .startObject("strategy_id").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
                .startObject("status").field("type", "integer").field("store", "yes").field("index", "not_analyzed").endObject()
                .startObject("area_name").field("type", "string").field("store", "yes").endObject()
                .endObject()
                .endObject();
        return builder;
    }

    /**
     * 以{@code XContentBuilder}方式添加索引
     *
     * @param index
     * @param type
     * @param obj
     * @return true or false
     */
    public static boolean addIndex(String index, String type, XContentBuilder obj) {
        boolean flag = true;
        try {
            IndexResponse response = client.prepareIndex(index, type).setSource(obj).get();
            int status = response.status().getStatus();
            if (status != 200) {
                flag = false;
            }
        } catch (Exception e) {
            flag = false;
            logger.error("add index error:", e);
        }

        return flag;
    }

    /**
     * 根据ID获取数据
     *
     * @param index
     * @param type
     * @param id
     * @return {@code Map<String, Object>} or null
     */
    public static Map<String, Object> getIndex(String index, String type, String id) {
        Map<String, Object> result = null;
        try {
            GetResponse response = client.prepareGet(index, type, id).get();
            result = response.getSource();
        } catch (Exception e) {
            logger.error("getIndex error,params is index:{} type:{} id:{}", index, type, id, e);
        }
        return result;
    }

    /**
     * 根据ID删除数据
     *
     * @param index
     * @param type
     * @param id
     * @return true or false
     */
    public static boolean deleteIndex(String index, String type, String id) {
        boolean flag = true;
        try {
            DeleteResponse response = client.prepareDelete(index, type, id).get();
            int status = response.status().getStatus();
            if (status != 200) {
                flag = false;
            }
        } catch (Exception e) {
            logger.error("deleteIndex error,,params is index:{} type:{} id:{}", index, type, id, e);
            flag = false;
        }

        return flag;
    }

    /**
     * 根据ID更新数据
     *
     * @param index
     * @param type
     * @param id
     * @param builder
     * @return true or false
     */
    public static boolean updateById(String index, String type, String id, XContentBuilder builder) {
        boolean flag = true;
        try {
            UpdateResponse response = client.prepareUpdate(index, type, id).setDoc(builder).get();
            int status = response.status().getStatus();
            if (status != 200) {
                flag = false;
            }
        } catch (Exception e) {
            logger.error("updateById error:", e);
            flag = false;
        }
        return flag;
    }

    /**
     * 更新数据,如果不存在,则插入
     *
     * @param index
     * @param type
     * @param id
     * @param insertBuildr
     * @param updateBuilder
     * @return true or false
     */
    public static boolean upsert(String index, String type, String id, XContentBuilder insertBuildr, XContentBuilder updateBuilder) {
        boolean flag = true;
        try {
            IndexRequest indexRequest = new IndexRequest(index, type, id).source(insertBuildr);
            UpdateRequest updateRequest = new UpdateRequest(index, type, id).doc(updateBuilder).upsert(indexRequest);
            int status = client.update(updateRequest).get().status().getStatus();
            if (status != 200) {
                flag = false;
            }
        } catch (Exception e) {
            logger.error("upsert error:", e);
            flag = false;
        }
        return flag;
    }

    public static void queryAll() {
        QueryBuilder qb = QueryBuilders.matchAllQuery();
        SearchResponse response = client.prepareSearch("monitor_index")
                .setTypes("monitor_type")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(qb)
                //.setQuery(QueryBuilders.termQuery("multi", "test"))                 // Query
                //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
                .setFrom(0).setSize(3).setExplain(true)
                .get();
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit sh : hits) {
            System.out.println(sh.getSource().toString());
        }
    }
}
