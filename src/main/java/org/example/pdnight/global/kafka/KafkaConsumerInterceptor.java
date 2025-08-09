package org.example.pdnight.global.kafka;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class KafkaConsumerInterceptor implements ConsumerInterceptor<String, String> {

    @Override
    public ConsumerRecords<String, String> onConsume(ConsumerRecords<String, String> records) {
        for (ConsumerRecord<String, String> record : records) {
            // Kafka 메시지 헤더에서 traceId 추출
            String traceId = getHeader(record, "traceId");
            if (traceId != null) {
                MDC.put("traceId", traceId);
            }

            // 필요 시 userId도 헤더에서 추출
            String userId = getHeader(record, "userId");
            if (userId != null) {
                MDC.put("userId", userId);
            }
        }
        return records;
    }

    private String getHeader(ConsumerRecord<String, String> record, String key) {
        Header header = record.headers().lastHeader(key);
        return header != null ? new String(header.value(), StandardCharsets.UTF_8) : null;
    }

    @Override
    public void close() {
        MDC.clear();
    }

    @Override public void configure(Map<String, ?> configs) {}
    @Override public void onCommit(Map offsets) {}
}
