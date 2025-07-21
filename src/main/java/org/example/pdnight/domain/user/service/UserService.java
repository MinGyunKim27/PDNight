package org.example.pdnight.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.hobby.entity.Hobby;
import org.example.pdnight.domain.hobby.repository.HobbyRepository;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.techStack.entity.TechStack;
import org.example.pdnight.domain.techStack.repository.TechStackRepository;
import org.example.pdnight.domain.post.repository.PostRepositoryQueryImpl;
import org.example.pdnight.domain.user.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.user.dto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.dto.response.UserEvaluationResponse;
import org.example.pdnight.domain.user.dto.response.UserResponseDto;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final HobbyRepository hobbyRepository;
    private final TechStackRepository techStackRepository;
    private final PostRepositoryQueryImpl postRepositoryQuery;
    private final UserRepository userRepository;

    public PagedResponse<PostResponseDto> findMyLikedPosts(Long userId, Pageable pageable){
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

    public UserResponseDto getMyProfile(Long userId){
        // id로 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // UserResponseDto로 변환하여 반환
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateMyProfile(Long userId, UserUpdateRequest request){
        Hobby hobby = null;
        TechStack techStack = null;

        if(request.getHobbyId() != null){
            hobby = hobbyRepository.findById(request.getHobbyId()).orElseThrow(
                    () -> new BaseException(ErrorCode.HOBBY_NOT_FOUND)
            );
        }

        if(request.getTechStackId() != null){
             techStack = techStackRepository.findById(request.getTechStackId()).orElseThrow(
                    () -> new BaseException(ErrorCode.TECH_STACK_NOT_FOUND)
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // 수정 로직
        user.updateProfile(request, hobby, techStack);
        userRepository.save(user);

        return new UserResponseDto(user);
    }

    @Transactional
    public void updatePassword(Long userId, UserPasswordUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        boolean match = BCrypt.verifyer()
                .verify(request.getOldPassword().toCharArray(), user.getPassword())
                .verified;
        if (!match) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        // 비밀번호 암호화
        String encodedPassword = BCrypt.withDefaults().hashToString(10, request.getNewPassword().toCharArray());
        user.changePassword(encodedPassword);
        userRepository.save(user);
    }

    public UserResponseDto getProfile(Long id){
        // id로 유저 조회
        User user = userRepository.findById(id).orElseThrow(
                ()-> new BaseException(ErrorCode.USER_NOT_FOUND));

        // UserResponseDto로 변환하여 반환
        return new UserResponseDto(user);

    }

    public UserEvaluationResponse getEvaluation(Long id){
        // id로 유저 조회
        User user = userRepository.findById(id).orElseThrow(
                ()-> new BaseException(ErrorCode.USER_NOT_FOUND));

        return new UserEvaluationResponse(user);
    }
}
