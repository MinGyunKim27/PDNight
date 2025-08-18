package org.example.pdnight.domain.recommendation.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.pdnight.domain.recommendation.application.UserProfileESService;
import org.example.pdnight.domain.recommendation.presentaion.event.ParticipationConfirmed;
import org.example.pdnight.domain.recommendation.presentaion.event.PostLiked;
import org.example.pdnight.domain.recommendation.presentaion.event.UserProfileEdited;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProfileConsumer {

    private final UserProfileESService service;

    @KafkaListener(topics = "user.profile.edited",groupId = "user-profile-group")
    public void on(ConsumerRecord<String, UserProfileEdited> record) {
        UserProfileEdited e = record.value();
        try {
            service.upsertProfile(e.userId(), e.hobbies(), e.skills());
        } catch (Exception ex) {
            log.error("[user.profile.edited] topic={} partition={} offset={} key={} payload={} cause={}",
                    record.topic(), record.partition(), record.offset(), record.key(), e, ex.toString(), ex);
            throw ex; // DLT로 보내기 위해 반드시 rethrow
        }
    }

    @KafkaListener(topics = "user.post.liked",groupId = "user-profile-group")
    public void on(PostLiked e) {
        service.incrementLikedTags(e.userId(), e.postTags());
    }

    @KafkaListener(topics = "user.participation.confirmed",groupId = "user-profile-group")
    public void on(ParticipationConfirmed e) {
        for(Long i : e.userIds()){
            service.incrementJoinedTags(i, e.postTags());
        }
    }

    @KafkaListener(topics = "user.profile.edited.DLT",groupId = "user-profile-DLT-group")
    public void onDLT(UserProfileEdited e) {
        service.upsertProfile(e.userId(), e.hobbies(), e.skills());
    }

    @KafkaListener(topics = "user.post.liked.DLT",groupId = "user-profile-DLT-group")
    public void onDLT(PostLiked e) {
        service.incrementLikedTags(e.userId(), e.postTags());
    }

    @KafkaListener(topics = "user.participation.confirmed.DLT",groupId = "user-profile-DLT-group")
    public void onDLT(ParticipationConfirmed e) {
        for(Long i : e.userIds()){
            service.incrementJoinedTags(i, e.postTags());
        }
    }
}