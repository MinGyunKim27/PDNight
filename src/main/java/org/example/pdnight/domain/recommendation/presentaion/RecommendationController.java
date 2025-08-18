package org.example.pdnight.domain.recommendation.presentaion;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.example.pdnight.domain.recommendation.application.RecommendationService;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Pageable;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PostDocument>>> recommendation(
            @AuthenticationPrincipal CustomUserDetails loggedInUser,
            @PageableDefault(size = 10,page = 0,sort = "createAt",direction = Sort.Direction.DESC) Pageable pageable
            ){
        Page<PostDocument> postDocuments = recommendationService.recommendForUserNormalized(loggedInUser.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("추천 게시물이 성공적으로 조회 되었습니다!",PagedResponse.from(postDocuments)));
    }
}
