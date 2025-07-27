package org.example.pdnight.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.comment.repository.CommentRepository;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.global.constant.CacheName;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.hobby.entity.Hobby;
import org.example.pdnight.domain.hobby.entity.PostHobby;
import org.example.pdnight.domain.hobby.repository.HobbyRepositoryQuery;
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
import org.example.pdnight.domain.techStack.entity.PostTech;
import org.example.pdnight.domain.techStack.entity.TechStack;
import org.example.pdnight.domain.techStack.repository.TechStackRepositoryQuery;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostRepositoryQuery PostRepositoryQuery;
    private final CommentRepository commentRepository;
    private final HobbyRepositoryQuery hobbyRepositoryQuery;
    private final TechStackRepositoryQuery techStackRepositoryQuery;

    //포스트 작성
    @Caching(evict = {
            @CacheEvict(value = CacheName.SEARCH_POST, allEntries = true),
            @CacheEvict(value = CacheName.SUGGESTED_POST, allEntries = true),
            @CacheEvict(value = CacheName.WRITTEN_POST, allEntries = true)
    })
    @Transactional
    public PostCreateAndUpdateResponseDto createPost(Long userId, PostRequestDto request) {
        //임시 메서드 User 도메인 작업에 따라 변동될 것
        User foundUser = getUserOrThrow(userRepository.findByIdAndIsDeletedFalse(userId));

        // List<Hobby> / List<TechStack> 생성 : DB 에서 있는거만 가져오기
        List<Hobby> hobbyList = getHobbyList(request);

        List<TechStack> techStackList = getTechStackList(request);

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

        // List<Hobby> -> Set<PostHobby>  /  List<TechStack> -> Set<PostTech>
        Set<PostHobby> postHobbies = getPostHobbySet(hobbyList, post);

        Set<PostTech> postTechs = getPostTechSet(techStackList, post);

        // post 저장 : 취미, 기술 스택 저장
        post.setHobbyAndTech(postHobbies, postTechs);
        Post savedPost = postRepository.save(post);
        return PostCreateAndUpdateResponseDto.from(savedPost);
    }

    //조회는 상태값 "OPEN" 인 게시글만 가능
    @Cacheable(value = CacheName.ONE_POST, key = "#id")
    @Transactional(readOnly = true)
    public PostResponseWithApplyStatusDto findOpenedPost(Long id) {
        return PostRepositoryQuery.getOpenedPostById(id);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheName.SEARCH_POST, allEntries = true),
            @CacheEvict(value = CacheName.ONE_POST, key = "#id"),
            @CacheEvict(value = CacheName.LIKED_POST, allEntries = true),
            @CacheEvict(value = CacheName.CONFIRMED_POST, allEntries = true),
            @CacheEvict(value = CacheName.WRITTEN_POST, allEntries = true),
            @CacheEvict(value = CacheName.SUGGESTED_POST, allEntries = true),
    })
    @Transactional
    public void deletePostById(Long userId, Long id) {
        Post foundPost = getPostById(id);
        validateAuthor(userId, foundPost);

        foundPost.unlinkReviews();

        //자식 댓글들 먼저 일괄 삭제 외래키 제약 제거
        commentRepository.deleteAllByChildrenPostId(id);
        //postId 기준 댓글 일괄 삭제 메서드 외래키 제약 제거
        commentRepository.deleteAllByPostId(id);
        postRepository.delete(foundPost);
    }

    //게시물 조건 검색
    @Cacheable(
            value = CacheName.SEARCH_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #maxParticipants, #ageLimit, #jobCategoryLimit, #genderLimit, #hobbyIdList, #techStackIdList}"
    )
    @Transactional(readOnly = true)
    public PagedResponse<PostResponseWithApplyStatusDto> getPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit,
            List<Long> hobbyIdList,
            List<Long> techStackIdList
    ) {

        Page<PostResponseWithApplyStatusDto> postDtosBySearch = PostRepositoryQuery.findPostDtosBySearch(pageable, maxParticipants,
                ageLimit, jobCategoryLimit, genderLimit, hobbyIdList, techStackIdList);
        return PagedResponse.from(postDtosBySearch);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheName.SEARCH_POST, allEntries = true),
            @CacheEvict(value = CacheName.ONE_POST, key = "#id"),
            @CacheEvict(value = CacheName.LIKED_POST, allEntries = true),
            @CacheEvict(value = CacheName.CONFIRMED_POST, allEntries = true),
            @CacheEvict(value = CacheName.WRITTEN_POST, allEntries = true),
            @CacheEvict(value = CacheName.SUGGESTED_POST, allEntries = true),
    })
    @Transactional
    public PostCreateAndUpdateResponseDto updatePostDetails(Long userId, Long id, PostUpdateRequestDto request) {
        Post foundPost = getPostById(id);
        validateAuthor(userId, foundPost);

        // 취미 리스트 : DB 에서 있는거만 가져오기 -> Set<UserHobby>
        Set<PostHobby> postHobbies = getUserHobbyByIdList(request.getHobbyIdList(), foundPost);

        // 기술 스택 리스트 : DB 에서 있는거만 가져오기 -> List<UserTech>
        Set<PostTech> postTechs = getUserTechByIdList(request.getTechStackIdList(), foundPost);

        foundPost.updatePostIfNotNull(
                request.getTitle(),
                request.getTimeSlot(),
                request.getPublicContent(),
                request.getPrivateContent(),
                request.getMaxParticipants(),
                request.getGenderLimit(),
                request.getJobCategoryLimit(),
                request.getAgeLimit(),
                postHobbies,
                postTechs
        );

        return PostCreateAndUpdateResponseDto.from(foundPost);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheName.SEARCH_POST, allEntries = true),
            @CacheEvict(value = CacheName.ONE_POST, key = "#id"),
            @CacheEvict(value = CacheName.LIKED_POST, allEntries = true),
            @CacheEvict(value = CacheName.CONFIRMED_POST, allEntries = true),
            @CacheEvict(value = CacheName.WRITTEN_POST, allEntries = true),
            @CacheEvict(value = CacheName.SUGGESTED_POST, allEntries = true),
    })
    @Transactional
    public PostResponseDto changeStatus(Long userId, Long id, PostStatusRequestDto request) {
        //상태값 변경은 어떤 상태라도 불러와서 수정
        Post foundPost = getPostById(id);
        validateAuthor(userId, foundPost);

        //변동사항 있을시에만 업데이트
        if (!foundPost.getStatus().equals(request.getStatus())) {
            foundPost.updateStatus(request.getStatus());
        }

        return PostResponseDto.from(foundPost);
    }

    // 내가 좋아요 누른 게시물 조회
    @Cacheable(
            value = CacheName.LIKED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponseWithApplyStatusDto> findMyLikedPosts(Long userId, Pageable pageable) {
        Page<PostResponseWithApplyStatusDto> myLikePost = PostRepositoryQuery.getMyLikePost(userId, pageable);
        return PagedResponse.from(myLikePost);
    }

    // 내 성사된/ 신청한 게시물 조회
    @Cacheable(
            value = CacheName.CONFIRMED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId, #joinStatus}"
    )
    public PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> findMyConfirmedPosts(Long userId, JoinStatus joinStatus, Pageable pageable) {
        Page<PostWithJoinStatusAndAppliedAtResponseDto> myConfirmedPost = PostRepositoryQuery.getConfirmedPost(userId, joinStatus, pageable);
        return PagedResponse.from(myConfirmedPost);
    }

    //내 작성 게시물 조회
    @Cacheable(
            value = CacheName.WRITTEN_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponseWithApplyStatusDto> findMyWrittenPosts(Long userId, Pageable pageable) {
        Page<PostResponseWithApplyStatusDto> myWrittenPost = PostRepositoryQuery.getWrittenPost(userId, pageable);
        return PagedResponse.from(myWrittenPost);
    }

    //추천 게시물 조회
    @Cacheable(
            value = CacheName.SUGGESTED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponseWithApplyStatusDto> getSuggestedPosts(Long userId, Pageable pageable) {
        Page<PostResponseWithApplyStatusDto> suggestedPost = PostRepositoryQuery.getSuggestedPost(userId, pageable);
        return PagedResponse.from(suggestedPost);
    }


    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // -- HELPER 메서드 -- //
    // get

    public Post getPostById(Long id) {
        return postRepository.findByIdAndStatus(id, PostStatus.OPEN)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }


    private User getUserOrThrow(Optional<User> user) {
        return user.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    // validate
    private void validateAuthor(Long userId, Post post) {
        if (!post.getAuthor().getId().equals(userId)) {
            throw new BaseException(ErrorCode.POST_FORBIDDEN);
        }
    }

    private Set<PostHobby> getPostHobbySet(List<Hobby> hobbyList, Post post) {
        return hobbyList.stream()
                .map(hobby -> new PostHobby(post, hobby))
                .collect(Collectors.toSet());
    }

    private Set<PostTech> getPostTechSet(List<TechStack> techStackList, Post post) {
        return techStackList.stream()
                .map(techStack -> new PostTech(post, techStack))
                .collect(Collectors.toSet());
    }

    private List<Hobby> getHobbyList(PostRequestDto request) {
        List<Hobby> hobbyList = new ArrayList<>();
        if (request.getHobbyIdList() != null && !request.getHobbyIdList().isEmpty()) {
            hobbyList = hobbyRepositoryQuery.findByIdList(request.getHobbyIdList());
        }
        return hobbyList;
    }

    private List<TechStack> getTechStackList(PostRequestDto request) {
        List<TechStack> techStackList = new ArrayList<>();
        if (request.getTechStackIdList() != null && !request.getTechStackIdList().isEmpty()) {
            techStackList = techStackRepositoryQuery.findByIdList(request.getTechStackIdList());
        }
        return techStackList;
    }

    // ----------- 중간 테이블 용 Helper 메서드 --------------------------//


    private Set<PostTech> getUserTechByIdList(List<Long> ids, Post post) {
        Set<PostTech> postTechs = new HashSet<>();
        if (ids != null && !ids.isEmpty()) {
            postTechs = techStackRepositoryQuery.findByIdList(ids)
                    .stream()
                    .map(techStack -> PostTech.from(post, techStack))
                    .collect(Collectors.toSet());
        }

        return postTechs;
    }

    private Set<PostHobby> getUserHobbyByIdList(List<Long> ids, Post post) {
        Set<PostHobby> postHobbies = new HashSet<>();
        if (ids != null && !ids.isEmpty()) {
            postHobbies = hobbyRepositoryQuery.findByIdList(ids)
                    .stream()
                    .map(hobby -> PostHobby.create(post, hobby))
                    .collect(Collectors.toSet());
        }
        return postHobbies;
    }


}
