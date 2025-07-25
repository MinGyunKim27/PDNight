package org.example.pdnight.domain.chatRoom.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chatRoom.entity.ChatRoom;
import org.example.pdnight.domain.chatRoom.entity.ChatRoomParticipant;
import org.example.pdnight.domain.chatRoom.repository.ChatRoomParticipantRepository;
import org.example.pdnight.domain.chatRoom.repository.ChatRoomRepository;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.participant.entity.PostParticipant;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.participant.repository.ParticipantRepository;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChattingService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final PostRepository postRepository;
    private final ParticipantRepository participantRepository;

    // 게시글 채팅방이 생성되었는지 확인
    public Boolean checkPostChatRoom(Long postId) {
        return chatRoomRepository.existsByPostId(postId);
    }

    // 게시글 채팅방 생성
    @Transactional
    public ChatRoom createFromPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
        ChatRoom chatRoom = new ChatRoom(post.getTitle(), post.getId());
        return chatRoomRepository.save(chatRoom);
    }

    // 게시글 참여자 목록 채팅방 참여자 목록에 저장
    @Transactional
    public void registration(Post post) {
        ChatRoom chatRoom = chatRoomRepository.findByPostId(post.getId());
        if(!chatRoomParticipantRepository.existsByChatRoomAndUserId(chatRoom, post.getAuthor().getId())) {
            ChatRoomParticipant chatRoomAuth = new ChatRoomParticipant(chatRoom, post.getAuthor().getId());
            chatRoomParticipantRepository.save(chatRoomAuth);
        }

        List<PostParticipant> participants = participantRepository.findByPostAndStatus(post, JoinStatus.ACCEPTED);
        for(PostParticipant participant : participants) {
            if(!chatRoomParticipantRepository.existsByChatRoomAndUserId(chatRoom, participant.getUser().getId())) {
                ChatRoomParticipant chatRoomParticipant = new ChatRoomParticipant(chatRoom, participant.getUser().getId());
                chatRoomParticipantRepository.save(chatRoomParticipant);
            }
        }
    }

    // 게시글 채팅방 참여시 채팅방 참여자인지 확인
    public String PostChatRoomEnter(Long userId, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new BaseException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        if(chatRoom.getPostId() != null) {
            if (!chatRoomParticipantRepository.existsByIdAndUserId(chatRoom.getId(), userId)) {
                throw new BaseException(ErrorCode.CHAT_ROOM_NOT_PARTICIPANT);
            }
        }
        return "채팅방에 참여 되었습니다.";
    }
}
