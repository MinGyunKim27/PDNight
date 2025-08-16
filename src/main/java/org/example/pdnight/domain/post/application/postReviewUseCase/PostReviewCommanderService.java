package org.example.pdnight.domain.post.application.postReviewUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.postReview.PostReviewCommander;
import org.example.pdnight.domain.post.domain.postReview.PostReviewDocument;
import org.example.pdnight.domain.post.presentation.dto.request.PostReviewRequest;
import org.example.pdnight.domain.post.presentation.dto.request.PostReviewUpdateRequest;
import org.example.pdnight.domain.post.presentation.dto.response.PostParticipantInfo;
import org.example.pdnight.domain.post.presentation.dto.response.PostReviewResponse;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.example.pdnight.global.common.enums.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class PostReviewCommanderService {
    private final PostReviewCommander postReviewCommander;
    private final PostReviewPort postReviewPort;

    public PostReviewResponse createPostReview(Long userId, PostReviewRequest request) throws IOException {
        PostParticipantInfo participants = postReviewPort.findById(Long.parseLong(request.getPostId()));
        if (!participants.getAuthId().equals(userId) && participants.getParticipants().stream().noneMatch(p -> p.equals(userId))) {
            throw new BaseException(POST_REVIEW_NOT_PARTICIPANT);
        }

        List<String> imagesPath = new ArrayList<>();
        List<String> videosPath = new ArrayList<>();

        if(request.getImages() != null) {
            imagesPath = saveFile(request.getImages(), "images");
        }
        if(request.getVideos() != null) {
            videosPath = saveFile(request.getVideos(), "videos");
        }

        PostReviewDocument postReview = PostReviewDocument.create(request, userId, imagesPath, videosPath, LocalDateTime.now());

        PostReviewDocument savePostReview = postReviewCommander.save(postReview);
        return PostReviewResponse.create(savePostReview);
    }

    public PostReviewResponse updatePostReview(Long userId, String reviewId, PostReviewUpdateRequest request) throws IOException {
        PostReviewDocument postReview = postReviewCommander.findById(reviewId).orElseThrow(() -> new BaseException(POST_REVIEW_NOT_FOUND));
        if (!userId.equals(postReview.getAuthId())) {
            throw new BaseException(POST_REVIEW_FORBIDDEN);
        }
        List<String> imagesPath = saveFile(request.getImages(), "images");
        List<String> videosPath = saveFile(request.getImages(), "videos");

        postReview.update(request, imagesPath, videosPath);
        PostReviewDocument updatePostReview = postReviewCommander.save(postReview);
        return PostReviewResponse.create(updatePostReview);
    }

    public void deletePostReview(Long userId, String reviewId) {
        PostReviewDocument postReview = postReviewCommander.findById(reviewId).orElseThrow(() -> new BaseException(POST_REVIEW_NOT_FOUND));
        if (!userId.equals(postReview.getAuthId())) {
            throw new BaseException(POST_REVIEW_FORBIDDEN);
        }
        postReviewCommander.delete(postReview);
    }

    private List<String> saveFile(List<MultipartFile> files, String forder) throws IOException {
        List<String> paths = new ArrayList<>();
        String dir = "C:/PDNight/uploads/" + forder;
        Files.createDirectories(Paths.get(dir));
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue; // 빈 파일은 건너뜀

            String filePath = dir + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            paths.add(filePath);
        }
        return paths;
    }
}
