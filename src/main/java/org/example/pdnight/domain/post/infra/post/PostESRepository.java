package org.example.pdnight.domain.post.infra.post;

import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostESRepository extends ElasticsearchRepository <PostDocument, Long> {
    List<PostDocument> findByTitle(String title);
}
