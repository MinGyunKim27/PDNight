package org.example.pdnight.domain.recommendation.infra;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserProfileESRepository extends ElasticsearchRepository<UserProfileDocument, Long> {
}
