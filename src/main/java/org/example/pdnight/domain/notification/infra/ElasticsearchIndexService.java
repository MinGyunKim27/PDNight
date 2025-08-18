package org.example.pdnight.domain.notification.infra;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ElasticsearchIndexService {

    private final ElasticsearchClient elasticsearchClient; // Java API 클라이언트
    private final ObjectMapper objectMapper;

    /**
     * 단일 문서 인덱싱
     */
    public void indexPost(PostDocument postDocument) {
        try {
            elasticsearchClient.index(i -> i
                    .index("posts") // 인덱스명
                    .id(postDocument.getId().toString())
                    .document(postDocument)
            );
            log.info("Indexed post to Elasticsearch: {}", postDocument.getId());
        } catch (Exception e) {
            log.error("Failed to index post to Elasticsearch", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 여러 문서를 Bulk 인덱싱
     */
    public void bulkIndexPosts(List<PostDocument> postDocuments) {
        if (postDocuments == null || postDocuments.isEmpty()) {
            return;
        }

        try {
            BulkResponse bulkResponse = elasticsearchClient.bulk(b -> {
                for (PostDocument post : postDocuments) {
                    b.operations(op -> op
                            .index(idx -> idx
                                    .index("posts")
                                    .id(post.getId().toString())
                                    .document(post)
                            )
                    );
                }
                return b;
            });

            if (bulkResponse.errors()) {
                bulkResponse.items().forEach(item -> {
                    if (item.error() != null) {
                        log.error("Failed to index document: {}", item.error().reason());
                    }
                });
            } else {
                log.info("Successfully bulk indexed {} posts", postDocuments.size());
            }

        } catch (Exception e) {
            log.error("Failed to bulk index posts to Elasticsearch", e);
            throw new RuntimeException(e);
        }
    }
}

