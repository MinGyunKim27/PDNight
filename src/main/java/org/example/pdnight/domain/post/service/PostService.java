package org.example.pdnight.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.hobby.entity.Hobby;
import org.example.pdnight.domain.hobby.entity.PostHobby;
import org.example.pdnight.domain.hobby.repository.HobbyRepositoryQuery;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.dto.request.PostStatusRequestDto;
import org.example.pdnight.domain.post.dto.request.PostUpdateRequestDto;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.post.repository.PostRepositoryQueryImpl;
import org.example.pdnight.domain.techStack.entity.PostTech;
import org.example.pdnight.domain.techStack.entity.TechStack;
import org.example.pdnight.domain.techStack.repository.TechStackRepositoryQuery;
import org.example.pdnight.domain.user.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostRepositoryQueryImpl postRepositoryQuery;
    private final HobbyRepositoryQuery hobbyRepositoryQuery;
    private final TechStackRepositoryQuery techStackRepositoryQuery;

    @Transactional
    public PostResponseDto createPost(Long userId, PostRequestDto request) {
        //임시 메서드 User 도메인 작업에 따라 변동될 것
        User foundUser = getUserOrThrow(userRepository.findByIdAndIsDeletedFalse(userId));

        // List<Hobby> / List<TechStack> 생성 : DB 에서 있는거만 가져오기
        List<Hobby> hobbyList = new ArrayList<>();
        if (request.getHobbyIdList() != null && !request.getHobbyIdList().isEmpty()) {
            hobbyList = hobbyRepositoryQuery.findByIdList(request.getHobbyIdList());
        }
        List<TechStack> techStackList = new ArrayList<>();
        if (request.getTechStackIdList() != null && !request.getTechStackIdList().isEmpty()) {
            techStackList = techStackRepositoryQuery.findByIdList(request.getTechStackIdList());
        }

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

        // List<Hobby> -> List<PostHobby>  /  List<TechStack> -> List<PostTech>
        List<PostHobby> postHobbyList = hobbyList.stream()
                .map(hobby -> new PostHobby(post, hobby))
                .toList();
        List<PostTech> postTechList = techStackList.stream()
                .map(techStack -> new PostTech(post, techStack))
                .toList();

        // post 저장 : 취미, 기술 스택 저장
        post.setHobbyAndTech(postHobbyList, postTechList);
        Post savedPost = postRepository.save(post);

        return new PostResponseDto(savedPost);
    }

    //조회는 상태값 "OPEN" 인 게시글만 가능
    @Transactional(readOnly = true)
    public PostResponseDto findOpenedPost(Long id) {
        Post foundPost = getPostOrThrow(postRepository.findByIdAndStatus(id, PostStatus.OPEN));
        return new PostResponseDto(foundPost);
    }

    @Transactional
    public void deletePostById(Long userId, Long id) {
        Post foundPost = getPostOrThrow(postRepository.findById(id));
        validateAuthor(userId, foundPost);

        foundPost.unlinkReviews();
        postRepository.delete(foundPost);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    ) {
        return postRepository.findPostDtosBySearch(pageable, maxParticipants,
                ageLimit, jobCategoryLimit, genderLimit);
    }

    @Transactional
    public PostResponseDto updatePostDetails(Long userId, Long id, PostUpdateRequestDto request) {
        Post foundPost = getPostOrThrow(postRepository.findById(id));
        validateAuthor(userId, foundPost);

        // 취미 리스트 : DB 에서 있는거만 가져오기 -> List<UserHobby>
        List<PostHobby> postHobbyList = new ArrayList<>();
        if (request.getHobbyIdList() != null && !request.getHobbyIdList().isEmpty()) {
            postHobbyList = hobbyRepositoryQuery.findByIdList(request.getHobbyIdList())
                    .stream()
                    .map(hobby -> new PostHobby(foundPost, hobby))
                    .toList();
        }
        // 기술 스택 리스트 : DB 에서 있는거만 가져오기 -> List<UserTech>
        List<PostTech> postTechList = new ArrayList<>();
        if (request.getTechStackIdList() != null && !request.getTechStackIdList().isEmpty()) {
            postTechList = techStackRepositoryQuery.findByIdList(request.getTechStackIdList())
                    .stream()
                    .map(techStack -> new PostTech(foundPost, techStack))
                    .toList();
        }

        foundPost.updatePostIfNotNull(
                request.getTitle(),
                request.getTimeSlot(),
                request.getPublicContent(),
                request.getPrivateContent(),
                request.getMaxParticipants(),
                request.getGenderLimit(),
                request.getJobCategoryLimit(),
                request.getAgeLimit(),
                postHobbyList,
                postTechList
        );

        return new PostResponseDto(foundPost);
    }

    @Transactional
    public PostResponseDto changeStatus(Long userId, Long id, PostStatusRequestDto request) {
        //상태값 변경은 어떤 상태라도 불러와서 수정
        Post foundPost = getPostOrThrow(postRepository.findById(id));
        validateAuthor(userId, foundPost);

        //변동사항 있을시에만 업데이트
        if (!foundPost.getStatus().equals(request.getStatus())) {
            foundPost.updateStatus(request.getStatus());
        }

        return new PostResponseDto(foundPost);
    }

    public PagedResponse<PostResponseDto> findMyLikedPosts(Long userId, Pageable pageable) {
        Page<Post> myLikePost = postRepositoryQuery.getMyLikePost(userId, pageable);
        Page<PostResponseDto> postResponseDtos = myLikePost.map(PostResponseDto::toDto);
        return PagedResponse.from(postResponseDtos);
    }

    public PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> findMyConfirmedPosts(Long userId, JoinStatus joinStatus, Pageable pageable) {
        Page<PostWithJoinStatusAndAppliedAtResponseDto> myLikePost = postRepositoryQuery.getConfirmedPost(userId, joinStatus, pageable);
        return PagedResponse.from(myLikePost);
    }

    public PagedResponse<PostResponseDto> findMyWrittenPosts(Long userId, Pageable pageable) {
        Page<PostResponseDto> myLikePost = postRepositoryQuery.getWrittenPost(userId, pageable);
        return PagedResponse.from(myLikePost);
    }

    //이하 헬퍼 메서드
    private Post getPostOrThrow(Optional<Post> post) {
        return post.orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }

    private User getUserOrThrow(Optional<User> user) {
        return user.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateAuthor(Long userId, Post post) {
        if (!post.getAuthor().getId().equals(userId)) {
            throw new BaseException(ErrorCode.POST_FORBIDDEN);
        }
    }

}
