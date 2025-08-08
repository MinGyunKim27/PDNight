package org.example.pdnight.global.kafka;

import org.apache.kafka.clients.admin.AdminClient;

import java.util.concurrent.ExecutionException;

public class TopicGetAll {

    public static void main(String[] args) {
        try (AdminClient admin = KafkaAdminClientFactory.create()) {
            admin.listTopics().names().get().forEach(System.out::println);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
