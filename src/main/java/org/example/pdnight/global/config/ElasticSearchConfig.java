package org.example.pdnight.global.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "org.springframework.data.elasticsearch.repository")
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    // connectedTo는 "host:port" 형식을 기대 (스킴 없이)
    @Value("${spring.elasticsearch.uris:localhost:9200}")
    private String uris; // 예: "3.37.21.115:9200" 또는 "host1:9200,host2:9200"

    @Override
    @NonNull
    public ClientConfiguration clientConfiguration() {
        String[] hosts = uris.replace(" ", "").split(",");
        return ClientConfiguration.builder()
                .connectedTo(hosts)   // 보안 OFF일 때
                // .usingSsl()        // 보안 ON이면 사용 + 인증서 신뢰 설정 추가 필요
                .build();
    }
}
