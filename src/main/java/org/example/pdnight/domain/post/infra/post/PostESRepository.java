package org.example.pdnight.domain.post.infra.post;

import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PostESRepository extends ElasticsearchRepository <PostDocument, Long> {
    List<PostDocument> findByTitle(String title);
}
