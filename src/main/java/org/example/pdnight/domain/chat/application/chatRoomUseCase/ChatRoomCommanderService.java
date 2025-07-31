package org.example.pdnight.domain.chat.application.chatRoomUseCase;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.chat.domain.ChatMessage;
import org.example.pdnight.domain.chat.domain.ChatParticipant;
import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.domain.ChatRoomCommandQuery;
import org.example.pdnight.domain.chat.infra.ChatRoomRedisRepository;
import org.example.pdnight.domain.chat.presentation.dto.request.ChatMessageDto;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostParticipant;
import org.example.pdnight.global.common.enums.ErrorCode;

import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomCommanderService {
    private final ChatRoomCommandQuery chatRoomCommandQuery;

    // 채팅방 생성
    public ChatRoom create(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        return chatRoomCommandQuery.save(chatRoom);
    }

    // 게시글 채팅방 생성
    @Transactional
    public void createFromPost(Long postId) {
        ChatRoom chatRoom = ChatRoom.createFromPost("df", postId);
        chatRoomCommandQuery.save(chatRoom);
    }

    // 게시글 참여자 목록 채팅방 참여자 목록에 저장
    @Transactional
    public void registration(Post post) {
        ChatRoom chatRoom = chatRoomCommandQuery.findByPostId(post.getId());
        // 게시글 작성자 등록
        postAuthorRegistration(chatRoom, post.getAuthorId());

        // 참여자 등록
        List<PostParticipant> participants = new ArrayList<>();
//                participantRepository.findByPostAndStatus(post, JoinStatus.ACCEPTED);
        postParticipantRegistration(chatRoom, participants);
    }

    // 채팅방 입장
    public void enterChatRoom(String roomId) {
        chatRoomCommandQuery.enterChatRoom(roomId);
    }

    // 게시글 채팅방 참여시 채팅방 참여자인지 확인
    public String chatRoomEnter(Long userId, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomCommandQuery.findById(chatRoomId).orElseThrow(() -> new BaseException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return validatedChatRoomEnter(chatRoom, userId);
    }

    // 메시지 보내기
    public void sendMessage(ChatMessageDto message) {
        // 메시지 기록
        ChatMessage chatMessage = ChatMessage.from(message.getRoomId(), message.getSender(), message.getMessage(), message.getMessageType());

//        chattingRepository.save(chatMessage);

        ChannelTopic topic = chatRoomCommandQuery.getTopic(message.getRoomId());
        topic = getOrSubscribeTopic(topic, message);
        publish(topic, message);
    }

    // -- HELPER 메서드 -- //
    private void postAuthorRegistration(ChatRoom chatRoom, Long authorId) {
        if (chatRoom.getParticipants().stream().noneMatch(participant -> participant.getUserId().equals(authorId))) {
            ChatParticipant authorParticipant = ChatParticipant.from(chatRoom, authorId);
            chatRoom.addParticipants(authorParticipant);
        }
    }

    private void postParticipantRegistration(ChatRoom chatRoom, List<PostParticipant> participants) {
        for (PostParticipant participant : participants) {
            // 채팅방에 참여되지 않은 경우 등록
//            if (!chatRoomCommandQuery.existsByChatRoomAndUserId(chatRoom, participant.getUserId())) {
//                ChatParticipant chatParticipant = ChatParticipant.from(chatRoom, participant.getUserId());
//                chatRoomCommandQuery.saveParticipant(chatParticipant);
//            }
        }
    }

    private String validatedChatRoomEnter(ChatRoom chatRoom, Long userId) {
        if (chatRoom.getPostId() != null) {
            List<ChatParticipant> participants = chatRoom.getParticipants();
            if (participants.stream().noneMatch(participant -> participant.getUserId().equals(userId))) {
                throw new BaseException(ErrorCode.CHAT_ROOM_NOT_PARTICIPANT);
            }
            return "게시글 채팅방에 참여 되었습니다.";
        }
        return "오픈 채팅방에 참여 되었습니다.";
    }

    private ChannelTopic getOrSubscribeTopic(ChannelTopic topic, ChatMessageDto message) {
        // 토픽이 없으면 구독 등록 후 다시 가져오기
        if (topic == null) {
            chatRoomCommandQuery.enterChatRoom(message.getRoomId());
            topic = chatRoomCommandQuery.getTopic(message.getRoomId());
        }
        return topic;
    }

    // ---------------- redis publisher --------------
    private final RedisTemplate<String, Object> redisTemplate;

    // 메시지를 발행
    public void publish(ChannelTopic topic, ChatMessageDto message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }

}
