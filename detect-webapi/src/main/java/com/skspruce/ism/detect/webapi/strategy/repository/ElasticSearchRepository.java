package com.skspruce.ism.detect.webapi.strategy.repository;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public abstract class ElasticSearchRepository {

    @Autowired
    Client client;

    protected SearchRequestBuilder prepareSearch() {
        return client.prepareSearch(getIndex()).setTypes(getType());
    }


    /**
     * Sub-Class should override this method and provide the underlying index name.
     * @return index name.
     */
    protected abstract String getIndex();

    /**
     * Sub-Class should override this method and provide the underlying type name of index.
     * @return type name
     */
    protected abstract String getType();


}

