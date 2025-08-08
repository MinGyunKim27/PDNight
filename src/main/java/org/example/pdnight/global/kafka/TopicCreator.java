package org.example.pdnight.global.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Slf4j
public class TopicCreator {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (AdminClient admin = KafkaAdminClientFactory.create()) {
            String participateTopic = "participate.event";
            String socialTopic = "social.event";
            String inviteTopic = "invite.event";

            List<String> topics = List.of(participateTopic, socialTopic, inviteTopic);

            // 1. 현재 존재하는 토픽 목록 조회
            Set<String> existingTopics = admin.listTopics().names().get();

            for (String topicName : topics) {
                if (existingTopics.contains(topicName)) {
                    log.warn("토픽 이미 존재함: {}", topicName);
                } else {
                    // 2. 존재하지 않으면 새로 생성
                    NewTopic topic = new NewTopic(topicName, 3, (short) 3);
                    admin.createTopics(List.of(topic)).all().get();
                    log.info("토픽 생성 완료: {}", topicName);
                }
            }
        }
    }
}
