package org.example.pdnight.domain.chat.application.chatRoomUseCase;


import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.domain.ChatParticipant;
import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.domain.ChatRoomCommander;
import org.example.pdnight.domain.chat.domain.ChatRoomProducer;
import org.example.pdnight.domain.chat.presentation.dto.response.PostInfoResponse;
import org.example.pdnight.domain.post.domain.post.PostParticipant;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.enums.KafkaTopic;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.event.ChatroomCreatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatRoomCommanderService {
    private final ChatRoomCommander chatRoomCommander;
    private final ChatPort postStatusConfirmedPort;
    private final ChatRoomProducer producer;

    // 채팅방 생성
    public ChatRoom create(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        return chatRoomCommander.save(chatRoom);
    }

    // 게시글 채팅방 생성
    @Transactional
    public ChatRoom createFromPost(Long postId) {
        PostInfoResponse post = postStatusConfirmedPort.getPostInfoById(postId);

        ChatRoom findByPostId = chatRoomCommander.findByPostId(postId);
        if (findByPostId == null) {
            findByPostId = ChatRoom.createFromPost(post.getTitle(), postId);
            chatRoomCommander.save(findByPostId);
        }

        registration(findByPostId, post);

        List<Long> chatroomParticipants = findByPostId.getParticipants().stream().map(ChatParticipant::getUserId).toList();

        producer.produce(KafkaTopic.CHATROOM_CREATED.topicName(), new ChatroomCreatedEvent(chatroomParticipants , post.getAuthorId()));

        return findByPostId;
    }

    // 게시글 참여자 목록을 채팅방 참여자 목록에 저장
    @Transactional
    public void registration(ChatRoom chatRoom, PostInfoResponse post) {
        // 게시글 작성자 등록
        postAuthorRegistration(chatRoom, post.getAuthorId());

        // 참여자 등록
        postParticipantRegistration(chatRoom, post.getPostParticipants());
    }

    // 게시글 채팅방 참여시 채팅방 참여자인지 확인
    public String chatRoomEnterValid(Long userId, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomCommander.findById(chatRoomId).orElseThrow(() -> new BaseException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return validatedChatRoomEnter(chatRoom, userId);
    }

    // -- HELPER 메서드 -- //
    private void postAuthorRegistration(ChatRoom chatRoom, Long authorId) {
        if (chatRoom.getParticipants().stream().noneMatch(participant -> participant.getUserId().equals(authorId))) {
            ChatParticipant authorParticipant = ChatParticipant.from(chatRoom, authorId);
            chatRoom.addParticipants(authorParticipant);
        }
    }

    private void postParticipantRegistration(ChatRoom chatRoom, List<PostParticipant> participants) {
        List<Long> chatParticipantList = chatRoom.getParticipants().stream()
                .map(ChatParticipant::getUserId).toList();
        for (PostParticipant participant : participants) {
//             채팅방에 참여되지 않은 경우 등록
            if (!chatParticipantList.contains(participant.getUserId())) {
                chatRoom.addParticipants(ChatParticipant.from(chatRoom, participant.getUserId()));
            }
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
}
