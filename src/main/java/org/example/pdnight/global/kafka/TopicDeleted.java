package org.example.pdnight.global.kafka;

import org.apache.kafka.clients.admin.AdminClient;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class TopicDeleted {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String deletedTopic = sc.next();

        // 토픽이 존재하는지 확인 하고 삭제하는 메서드
        try (AdminClient admin = KafkaAdminClientFactory.create()) {
            // 현재 존재하는 토픽들 불러오기
            Set<String> existingTopics = admin.listTopics().names().get();
            // 존재하면 Error Throw
            if (!existingTopics.contains(deletedTopic)) {
                throw new RuntimeException("Topic " + deletedTopic + " does not exist");
            }
            // 토픽 삭제 진행
            admin.deleteTopics(List.of(deletedTopic)).all().get();
            System.out.println("Deleted topic successfully");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
