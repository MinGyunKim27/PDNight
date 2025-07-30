package org.example.pdnight.domain1.post.service;

import org.example.pdnight.domain1.chatRoom.service.ChattingService;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.comment.repository.CommentRepository;
import org.example.pdnight.domain1.common.dto.PagedResponse;
import org.example.pdnight.global.constant.CacheName;
import org.example.pdnight.domain1.common.enums.ErrorCode;
import org.example.pdnight.domain1.common.enums.JobCategory;
import org.example.pdnight.domain1.common.enums.JoinStatus;
import org.example.pdnight.domain1.common.exception.BaseException;
import org.example.pdnight.domain1.hobby.entity.Hobby;
import org.example.pdnight.domain1.hobby.entity.PostHobby;
import org.example.pdnight.domain1.hobby.repository.HobbyRepositoryQuery;
import org.example.pdnight.domain1.post.dto.request.PostRequestDto;
import org.example.pdnight.domain1.post.dto.request.PostStatusRequestDto;
import org.example.pdnight.domain1.post.dto.request.PostUpdateRequestDto;
import org.example.pdnight.domain1.post.dto.response.PostCreateAndUpdateResponseDto;
import org.example.pdnight.domain1.post.dto.response.PostResponseDto;
import org.example.pdnight.domain1.post.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain1.post.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.post.enums.AgeLimit;
import org.example.pdnight.domain1.post.enums.Gender;
import org.example.pdnight.domain1.post.enums.PostStatus;
import org.example.pdnight.domain1.post.repository.PostRepository;
import org.example.pdnight.domain1.post.repository.PostRepositoryQuery;
import org.example.pdnight.domain1.techStack.entity.PostTech;
import org.example.pdnight.domain1.techStack.entity.TechStack;
import org.example.pdnight.domain1.techStack.repository.TechStackRepositoryQuery;
import org.example.pdnight.domain1.user.entity.User;
import org.example.pdnight.domain1.user.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostRepositoryQuery postRepositoryQuery;
    private final CommentRepository commentRepository;
    private final HobbyRepositoryQuery hobbyRepositoryQuery;
    private final TechStackRepositoryQuery techStackRepositoryQuery;
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

        foundPost.unlinkReviews();

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
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    // validate
    void validateAuthor(Long userId, Post post) {
        if (!post.getAuthor().getId().equals(userId)) {
            throw new BaseException(ErrorCode.POST_FORBIDDEN);
        }
    }

    private Set<PostHobby> getPostHobbySet(List<Hobby> hobbyList, Post post) {
        return hobbyList.stream()
                .map(hobby -> PostHobby.create(post, hobby))
                .collect(Collectors.toSet());
    }

    private Set<PostTech> getPostTechSet(List<TechStack> techStackList, Post post) {
        return techStackList.stream()
                .map(techStack -> PostTech.from(post, techStack))
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
