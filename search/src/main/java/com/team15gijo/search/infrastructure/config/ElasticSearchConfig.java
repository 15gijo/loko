package com.team15gijo.search.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.lang.NonNull;

@Configuration
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Value("${ELASTIC_USERNAME}")
    private String username;

    @Value("${ELASTIC_PASSWORD}")
    private String password;

    @Value("${ELASTIC_URL}")
    private String url;

    @Override
    @NonNull
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(url)
                .withBasicAuth(username, password)
                .build();
    }
}
