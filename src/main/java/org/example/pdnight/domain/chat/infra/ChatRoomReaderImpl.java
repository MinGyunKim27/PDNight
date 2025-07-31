package org.example.pdnight.domain.chat.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.domain.ChatRoom;
import org.example.pdnight.domain.chat.domain.ChatRoomReader;
import org.example.pdnight.domain.post.domain.post.Post;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.example.pdnight.domain.chat.domain.QChatRoom.chatRoom;
import static org.example.pdnight.domain.post.domain.post.QPost.post;

@Repository
@RequiredArgsConstructor
public class ChatRoomReaderImpl implements ChatRoomReader {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatRoom> findAll() {
        return queryFactory.select(chatRoom).from(chatRoom).fetch();
    }

    @Override
    public Optional<ChatRoom> findById(Long roomId) {
        ChatRoom findChatRoom = queryFactory.select(chatRoom).from(chatRoom).where(chatRoom.id.eq(roomId)).fetchOne();
        return Optional.ofNullable(findChatRoom);
    }
}
