package org.example.pdnight.domain.chatRoom.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chatRoom.dto.ChatMessageDto;
import org.example.pdnight.domain.chatRoom.dto.ChatRoomResponseDto;
import org.example.pdnight.domain.chatRoom.entity.ChatMessage;
import org.example.pdnight.domain.chatRoom.entity.ChatParticipant;
import org.example.pdnight.domain.chatRoom.entity.ChatRoom;
import org.example.pdnight.domain.chatRoom.repository.ChatRoomParticipantRepository;
import org.example.pdnight.domain.chatRoom.repository.ChatRoomRedisRepository;
import org.example.pdnight.domain.chatRoom.repository.ChatRoomRepository;
import org.example.pdnight.domain.chatRoom.repository.ChattingRepository;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.participant.entity.PostParticipant;
import org.example.pdnight.domain.participant.repository.ParticipantRepository;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChattingService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final ChattingRepository chattingRepository;
    private final ChatRoomRedisRepository chatRoomRedisRepository;
    private final RedisPublisher redisPublisher;
    private final PostRepository postRepository;
    private final ParticipantRepository participantRepository;

    // 게시글 채팅방이 생성되었는지 확인
    public Boolean checkPostChatRoom(Long postId) {
        return chatRoomRepository.existsByPostId(postId);
    }

    // 채팅방 생성
    public ChatRoom create(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        return chatRoomRepository.save(chatRoom);
    }

    // 게시글 채팅방 생성
    @Transactional
    public void createFromPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
        ChatRoom chatRoom = ChatRoom.createFromPost(post.getTitle(), post.getId());
        chatRoomRepository.save(chatRoom);
    }

    // 게시글 참여자 목록 채팅방 참여자 목록에 저장
    @Transactional
    public void registration(Post post) {
        ChatRoom chatRoom = chatRoomRepository.findByPostId(post.getId());
        // 게시글 작성자 등록
        postAuthorRegistration(chatRoom, post.getAuthor().getId());

        // 참여자 등록
        List<PostParticipant> participants = participantRepository.findByPostAndStatus(post, JoinStatus.ACCEPTED);
        postParticipantRegistration(chatRoom, participants);
    }

    // 채팅방 목록 조회
    public List<ChatRoomResponseDto> list() {
        return chatRoomRepository.findAll().stream()
                .map(chatRoom -> ChatRoomResponseDto.from(chatRoom.getId(), chatRoom.getChatRoomName()))
                .toList();
    }

    // 채팅방 정보 조회
    public ChatRoomResponseDto roomInfo(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new BaseException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return new ChatRoomResponseDto(chatRoom.getId(), chatRoom.getChatRoomName());
    }

    // 채팅방 기록 출력
    public List<ChatMessageDto> messageRecord(String roomId) {
        List<ChatMessage> messages = chattingRepository.findByRoomId(roomId);
        return messages.stream().map(ChatMessageDto::from).toList();
    }

    // 채팅방 입장
    public void enterChatRoom(String roomId) {
        chatRoomRedisRepository.enterChatRoom(roomId);
    }

    // 게시글 채팅방 참여시 채팅방 참여자인지 확인
    public String chatRoomEnter(Long userId, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new BaseException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return validatedChatRoomEnter(chatRoom, userId);
    }

    // 메시지 보내기
    public void sendMessage(ChatMessageDto message) {
        // 메시지 기록
        ChatMessage chatMessage = ChatMessage.from(message);
        chattingRepository.save(chatMessage);

        ChannelTopic topic = chatRoomRedisRepository.getTopic(message.getRoomId());
        topic = getOrSubscribeTopic(topic, message);
        redisPublisher.publish(topic, message);
    }


    // -- HELPER 메서드 -- //

    private void postAuthorRegistration(ChatRoom chatRoom, Long authorId) {
        if (!chatRoomParticipantRepository.existsByChatRoomAndUserId(chatRoom, authorId)) {
            ChatParticipant chatRoomAuth = ChatParticipant.from(chatRoom, authorId);
            chatRoomParticipantRepository.save(chatRoomAuth);
        }
    }

    private void postParticipantRegistration(ChatRoom chatRoom, List<PostParticipant> participants) {
        for (PostParticipant participant : participants) {
            // 채팅방에 참여되지 않은 경우 등록
            if (!chatRoomParticipantRepository.existsByChatRoomAndUserId(chatRoom, participant.getUser().getId())) {
                ChatParticipant chatParticipant = ChatParticipant.from(chatRoom, participant.getUser().getId());
                chatRoomParticipantRepository.save(chatParticipant);
            }
        }
    }

    private String validatedChatRoomEnter(ChatRoom chatRoom, Long userId) {
        if (chatRoom.getPostId() != null) {
            if (!chatRoomParticipantRepository.existsByChatRoomAndUserId(chatRoom, userId)) {
                throw new BaseException(ErrorCode.CHAT_ROOM_NOT_PARTICIPANT);
            }
            return "게시글 채팅방에 참여 되었습니다.";
        }
        return "오픈 채팅방에 참여 되었습니다.";
    }

    private ChannelTopic getOrSubscribeTopic(ChannelTopic topic, ChatMessageDto message) {
        // 토픽이 없으면 구독 등록 후 다시 가져오기
        if (topic == null) {
            chatRoomRedisRepository.enterChatRoom(message.getRoomId());
            topic = chatRoomRedisRepository.getTopic(message.getRoomId());
        }
        return topic;
    }
}
