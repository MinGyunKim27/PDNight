package org.example.pdnight.global.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(
        basePackages = "org.example.pdnight.domain.post.infra.post"
)
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    //connectedTo 안에 들어갈 내용
    @Value("${spring.elasticsearch.uris:localhost:9200}")
    private String uris;

    //Elastic Search와 연결
    @Override
    @NonNull
    public ClientConfiguration clientConfiguration() {
        String[] hosts = uris.replace(" ", "").split(",");
        return ClientConfiguration.builder()
                .connectedTo(hosts)
                .build();
    }
}
