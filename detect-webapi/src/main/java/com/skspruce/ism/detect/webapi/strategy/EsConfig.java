package com.skspruce.ism.detect.webapi.strategy;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

@Configuration
public class EsConfig {
    @Value("${elasticsearch.host}")
    private String EsHost;

    @Value("${elasticsearch.port}")
    private int EsPort;

    @Value("${elasticsearch.clustername}")
    private String EsClusterName;

    @Bean
    public Client client() throws Exception {

        Settings settings = Settings.builder().put("cluster.name", EsClusterName).build();

        TransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName(EsHost), EsPort);
        return new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);
//        TransportClient client = new TransportClient.Builder().settings(settings).build();
//        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(EsHost), EsPort));
//        return client;
    }
}
