package org.example.pdnight.domain.chat.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.domain.ChatMessage;
import org.example.pdnight.domain.chat.domain.ChatMessageReader;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.example.pdnight.domain.chat.domain.QChatMessage.chatMessage;


@Repository
@RequiredArgsConstructor
public class ChatMessageReaderImpl implements ChatMessageReader {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatMessage> findByRoomId(String roomId) {
        return queryFactory.select(chatMessage).from(chatMessage).where(chatMessage.roomId.eq(roomId)).fetch();
    }
}
