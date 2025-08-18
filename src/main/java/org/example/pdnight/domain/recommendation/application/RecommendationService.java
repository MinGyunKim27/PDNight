package org.example.pdnight.domain.recommendation.application;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.example.pdnight.domain.recommendation.domain.UserDocumentReaderService;
import org.example.pdnight.domain.recommendation.infra.UserProfileDocument;
import org.example.pdnight.domain.recommendation.infra.UserProfileESRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecommendationService {
    private final UserProfileESRepository userProfileRepo; // 네가 쓰는 ES repo or operations.get()
    private final UserDocumentReaderService userDocumentReaderService;

    /**
     * 사용자 정보에 기반해 게시물 추천 해 주는 서비스
     *
     * @param userId   사용자 Id
     * @param pageable 페이저블
     * @return 페이저블에 맞춘 추천 게시물 정보
     */
    public Page<PostDocument> recommendForUserNormalized(long userId, Pageable pageable) {

        UserProfileDocument profile = userProfileRepo.findById(userId).orElse(null);

        return userDocumentReaderService.recommendForUserNormalized(profile, pageable);
    }
}
