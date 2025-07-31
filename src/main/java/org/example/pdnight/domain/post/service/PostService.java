package org.example.pdnight.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chatRoom.service.ChattingService;
import org.example.pdnight.domain.comment.repository.CommentRepository;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.post.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.dto.request.PostStatusRequestDto;
import org.example.pdnight.domain.post.dto.request.PostUpdateRequestDto;
import org.example.pdnight.domain.post.dto.response.PostCreateAndUpdateResponseDto;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain.post.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.post.repository.PostRepositoryQuery;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.infra.userInfra.UserJpaRepository;
import org.example.pdnight.global.constant.CacheName;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserJpaRepository userJpaRepository;
    private final PostRepositoryQuery postRepositoryQuery;
    private final CommentRepository commentRepository;
    private final ChattingService chattingService;

    //포스트 작성
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.SEARCH_POST, allEntries = true),
            @CacheEvict(value = CacheName.SUGGESTED_POST, allEntries = true),
            @CacheEvict(value = CacheName.WRITTEN_POST, allEntries = true)
    })
    public PostCreateAndUpdateResponseDto createPost(Long userId, PostRequestDto request) {
        //임시 메서드 User 도메인 작업에 따라 변동될 것
        User foundUser = getUserById(userId);

        Post post = Post.createPost(
                foundUser,
                request.getTitle(),
                request.getTimeSlot(),
                request.getPublicContent(),
                request.getPrivateContent(),
                request.getMaxParticipants(),
                request.getGenderLimit(),
                request.getJobCategoryLimit(),
                request.getAgeLimit()
        );

        Post savedPost = postRepository.save(post);
        return PostCreateAndUpdateResponseDto.from(savedPost);
    }

    //조회는 상태값 "OPEN" 인 게시글만 가능
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.ONE_POST, key = "#id")
    public PostResponseWithApplyStatusDto findOpenedPost(Long id) {
        return postRepositoryQuery.getOpenedPostById(id);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.SEARCH_POST, allEntries = true),
            @CacheEvict(value = CacheName.ONE_POST, key = "#id"),
            @CacheEvict(value = CacheName.LIKED_POST, allEntries = true),
            @CacheEvict(value = CacheName.CONFIRMED_POST, allEntries = true),
            @CacheEvict(value = CacheName.WRITTEN_POST, allEntries = true),
            @CacheEvict(value = CacheName.SUGGESTED_POST, allEntries = true),
    })
    public void deletePostById(Long userId, Long id) {
        Post foundPost = getPostByIdOrElseThrow(id);
        validateAuthor(userId, foundPost);

        //자식 댓글들 먼저 일괄 삭제 외래키 제약 제거
        commentRepository.deleteAllByChildrenPostId(id);
        //postId 기준 댓글 일괄 삭제 메서드 외래키 제약 제거
        commentRepository.deleteAllByPostId(id);
        postRepository.delete(foundPost);
    }

    //게시물 조건 검색
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.SEARCH_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #maxParticipants, #ageLimit, #jobCategoryLimit, #genderLimit, #hobbyIdList, #techStackIdList}"
    )
    public PagedResponse<PostResponseWithApplyStatusDto> getPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit,
            List<Long> hobbyIdList,
            List<Long> techStackIdList
    ) {

        Page<PostResponseWithApplyStatusDto> postDtosBySearch = postRepositoryQuery.findPostDtosBySearch(pageable, maxParticipants,
                ageLimit, jobCategoryLimit, genderLimit, hobbyIdList, techStackIdList);
        return PagedResponse.from(postDtosBySearch);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.SEARCH_POST, allEntries = true),
            @CacheEvict(value = CacheName.ONE_POST, key = "#id"),
            @CacheEvict(value = CacheName.LIKED_POST, allEntries = true),
            @CacheEvict(value = CacheName.CONFIRMED_POST, allEntries = true),
            @CacheEvict(value = CacheName.WRITTEN_POST, allEntries = true),
            @CacheEvict(value = CacheName.SUGGESTED_POST, allEntries = true),
    })
    public PostCreateAndUpdateResponseDto updatePostDetails(Long userId, Long id, PostUpdateRequestDto request) {
        Post foundPost = getPostByIdOrElseThrow(id);
        validateAuthor(userId, foundPost);

        foundPost.updatePostIfNotNull(
                request.getTitle(),
                request.getTimeSlot(),
                request.getPublicContent(),
                request.getPrivateContent(),
                request.getMaxParticipants(),
                request.getGenderLimit(),
                request.getJobCategoryLimit(),
                request.getAgeLimit()
        );

        return PostCreateAndUpdateResponseDto.from(foundPost);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.SEARCH_POST, allEntries = true),
            @CacheEvict(value = CacheName.ONE_POST, key = "#id"),
            @CacheEvict(value = CacheName.LIKED_POST, allEntries = true),
            @CacheEvict(value = CacheName.CONFIRMED_POST, allEntries = true),
            @CacheEvict(value = CacheName.WRITTEN_POST, allEntries = true),
            @CacheEvict(value = CacheName.SUGGESTED_POST, allEntries = true),
    })
    public PostResponseDto changeStatus(Long userId, Long id, PostStatusRequestDto request) {
        //상태값 변경은 어떤 상태라도 불러와서 수정
        Post foundPost = getPostByIdWithoutStatusLimit(id);
        validateAuthor(userId, foundPost);

        //변동사항 있을시에만 업데이트
        if (!foundPost.getStatus().equals(request.getStatus())) {
            foundPost.updateStatus(request.getStatus());
            //모임 성사로 변경시 채팅방 생성
            if (request.getStatus().equals(PostStatus.CONFIRMED)) {
                // 게시글로 생성된 채팅방이 없는 경우 생성
                if (!chattingService.checkPostChatRoom(foundPost.getId())) {
                    chattingService.createFromPost(foundPost.getId());
                }
                chattingService.registration(foundPost);

            }
        }

        return PostResponseDto.from(foundPost);
    }

    // 내가 좋아요 누른 게시물 조회
    @Cacheable(
            value = CacheName.LIKED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponseWithApplyStatusDto> findMyLikedPosts(Long userId, Pageable pageable) {
        Page<PostResponseWithApplyStatusDto> myLikePost = postRepositoryQuery.getMyLikePost(userId, pageable);
        return PagedResponse.from(myLikePost);
    }

    // 내 성사된/ 신청한 게시물 조회
    @Cacheable(
            value = CacheName.CONFIRMED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId, #joinStatus}"
    )
    public PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> findMyConfirmedPosts(Long userId, JoinStatus joinStatus, Pageable pageable) {
        Page<PostWithJoinStatusAndAppliedAtResponseDto> myLikePost = postRepositoryQuery.getConfirmedPost(userId, joinStatus, pageable);
        return PagedResponse.from(myLikePost);
    }

    //내 작성 게시물 조회
    @Cacheable(
            value = CacheName.WRITTEN_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponseWithApplyStatusDto> findMyWrittenPosts(Long userId, Pageable pageable) {
        Page<PostResponseWithApplyStatusDto> myLikePost = postRepositoryQuery.getWrittenPost(userId, pageable);
        return PagedResponse.from(myLikePost);
    }

    //추천 게시물 조회
    @Cacheable(
            value = CacheName.SUGGESTED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponseWithApplyStatusDto> getSuggestedPosts(Long userId, Pageable pageable) {
        Page<PostResponseWithApplyStatusDto> suggestedPost = postRepositoryQuery.getSuggestedPost(userId, pageable);
        return PagedResponse.from(suggestedPost);
    }


    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // -- HELPER 메서드 -- //
    // get

    public Post getPostByIdOrElseThrow(Long id) {
        return postRepositoryQuery.getPostByIdNotClose(id)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }

    public Post getPostByIdWithoutStatusLimit(Long id) {
        return postRepository.findPostById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }


    private User getUserById(Long userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    // validate
    void validateAuthor(Long userId, Post post) {
        if (!post.getAuthor().getId().equals(userId)) {
            throw new BaseException(ErrorCode.POST_FORBIDDEN);
        }
    }

}
