package org.example.pdnight.global.kafka;

import org.apache.kafka.clients.admin.AdminClient;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TopicDeleted {

    public static void main(String[] args) {
        try (AdminClient admin = KafkaAdminClientFactory.create()) {
            admin.deleteTopics(List.of("participate.event")).all().get();
            System.out.println("Deleted topic successfully");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
