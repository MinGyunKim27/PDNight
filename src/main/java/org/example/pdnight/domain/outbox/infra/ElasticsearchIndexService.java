package org.example.pdnight.domain.outbox.infra;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ElasticsearchIndexService {

    private final ElasticsearchClient elasticsearchClient; // Java API 클라이언트
    private final ObjectMapper objectMapper;

    public void indexPost(PostDocument postDocument) {
        try { // 쓰는 이유
            elasticsearchClient.index(i -> i
                    .index("posts") // ES 인덱스 이름
                    .id(postDocument.getId().toString())
                    .document(postDocument)
            );
            log.info("Indexed post to Elasticsearch: {}", postDocument.getId());
        } catch (Exception e) {
            log.error("Failed to index post to Elasticsearch", e);
            throw new RuntimeException(e);
        }
    }
}
