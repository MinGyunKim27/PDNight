package org.example.pdnight.global.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
public class TopicCreator {

    // 토픽 생성을 진행하는 코드 ( 직접 실행 해 줘야 돌아간다 )
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (AdminClient admin = KafkaAdminClientFactory.create()) {
            List<TopicInfo> topics = NotificationTopic.getAllTopics();

            // 1. 현재 존재하는 토픽 목록 조회
            Set<String> existingTopics = admin.listTopics().names().get();

            // 맵 자료형 분리 해서 집어 넣기
            for (TopicInfo topic : topics) {
                String topicName = topic.topic();
                int partition = topic.partition();

                if (existingTopics.contains(topicName)) {
                    log.warn("토픽 이미 존재함: {}", topicName);
                } else {
                    // 2. 존재하지 않으면 새로 생성
                    NewTopic newTopic = new NewTopic(topicName, partition, (short) 3);
                    admin.createTopics(List.of(newTopic)).all().get();
                    log.info("토픽 생성 완료: {}", topicName);
                }
            }
        }
    }
}
