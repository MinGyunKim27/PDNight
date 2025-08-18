package org.example.pdnight.domain.recommendation.domain;

import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.example.pdnight.domain.recommendation.infra.UserProfileDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserDocumentReaderService {
    Page<PostDocument> recommendForUserNormalized(UserProfileDocument profile, Pageable pageable);
}
