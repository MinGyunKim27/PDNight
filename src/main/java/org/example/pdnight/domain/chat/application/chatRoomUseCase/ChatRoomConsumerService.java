package org.example.pdnight.domain.chat.application.chatRoomUseCase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.domain.ChatParticipant;
import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.domain.ChatRoomCommander;
import org.example.pdnight.domain.chat.domain.ChatRoomProducer;
import org.example.pdnight.global.common.enums.KafkaTopic;
import org.example.pdnight.global.event.ChatroomCreatedEvent;
import org.example.pdnight.global.event.PostConfirmedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatRoomConsumerService {

    private final ChatRoomCommander chatRoomCommander;
    private final ChatRoomProducer producer;

    @Transactional
    public void handlePostConfirmed(PostConfirmedEvent event) {

        ChatRoom findByPostId = chatRoomCommander.findByPostId(event.postId());
        if (findByPostId == null) {
            findByPostId = ChatRoom.createFromPost(event.postTitle(), event.postId());
            chatRoomCommander.save(findByPostId);
        }

        if (findByPostId.getParticipants().stream().noneMatch(participant -> participant.getUserId().equals(event.authorId()))) {
            ChatParticipant authorParticipant = ChatParticipant.from(findByPostId, event.authorId());
            findByPostId.addParticipants(authorParticipant);
        }

        List<Long> chatParticipantList = findByPostId.getParticipants().stream()
                .map(ChatParticipant::getUserId).toList();
        for (Long participantId : event.confirmedUserIds()) {
            //채팅방에 참여되지 않은 경우 등록
            if (!chatParticipantList.contains(participantId)) {
                findByPostId.addParticipants(ChatParticipant.from(findByPostId, participantId));
            }
        }
        List<Long> chatroomParticipants = findByPostId.getParticipants().stream().map(ChatParticipant::getUserId).toList();

        producer.produce(KafkaTopic.CHATROOM_CREATED.topicName(), new ChatroomCreatedEvent(chatroomParticipants, event.authorId()));
    }

}