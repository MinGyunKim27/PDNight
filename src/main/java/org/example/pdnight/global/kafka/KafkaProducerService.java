package org.example.pdnight.global.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private KafkaTemplate kafkaTemplate;

    public void sendEvent(String topic, String message, Long userId) {
        String traceId = UUID.randomUUID().toString();

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        record.headers().add(new RecordHeader("traceId", traceId.getBytes(StandardCharsets.UTF_8)));
        record.headers().add(new RecordHeader("userId", String.valueOf(userId).getBytes(StandardCharsets.UTF_8)));

        kafkaTemplate.send(record);
    }
}
