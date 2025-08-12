package org.example.pdnight.domain.kafka;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@ActiveProfiles("test")
@EmbeddedKafka(
        count = 3,
        topics = {"test-topic", "test-topic.DLT"},
        brokerProperties = {
                "auto.create.topics.enable=true",
                "offsets.topic.replication.factor=1",
                "transaction.state.log.replication.factor=1",
                "transaction.state.log.min.isr=1"
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 테스트 마다 Kafka 컨텍스트 초기화
public class KafkaTest {

    private static final String TOPIC = "test-topic";
    private final CountDownLatch latch = new CountDownLatch(1);
    private final CountDownLatch latchDlt = new CountDownLatch(1);

    private String receivedMessage;
    private String receivedDltMessage;

    @Autowired(required = false) // 런타임시 빈주입되어서 required = false 안쓰면 컴파일러 에러남
    private EmbeddedKafkaBroker embeddedKafka; // 선언해줘야 작동함

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = TOPIC, groupId = "test-group")
    public void listen(String message) {

        if(message.equals("DLT")){
            throw new RuntimeException("예외 발생");
        }
        receivedMessage = message;
        latch.countDown();
    }

    @KafkaListener(topics = TOPIC + ".DLT", groupId = "test-group", containerFactory = "dltListenerContainerFactory")
    public void listenDlt(String message) {
        receivedDltMessage = message;
        latchDlt.countDown();
    }

    @Test
    @DisplayName("카프카 송/수신 테스트")
    void kafkaSendReceiveTest() throws Exception {
        CompletableFuture<SendResult<String, Object>> send =
                kafkaTemplate.send(TOPIC, "Test");

        System.out.println("Send 성공: topic = " + send.get().getRecordMetadata().topic() +
                ", partition = " + send.get().getRecordMetadata().partition() +
                ", offset = " + send.get().getRecordMetadata().offset());

        boolean messageReceived = latch.await(30, TimeUnit.SECONDS);
        assertTrue(messageReceived, "메시지를 받지 못했습니다.");
        assertEquals("Test", receivedMessage);
    }

    @Test
    @DisplayName("카프카 DLT 테스트")
    void kafkaDLTTest() throws Exception {
        CompletableFuture<SendResult<String, Object>> send = kafkaTemplate.send(TOPIC, "DLT");

        System.out.println("DLT Send 성공: topic = " + send.get().getRecordMetadata().topic() +
                ", partition = " + send.get().getRecordMetadata().partition() +
                ", offset = " + send.get().getRecordMetadata().offset());

        boolean messageReceivedDlt = latchDlt.await(30, TimeUnit.SECONDS);
        assertTrue(messageReceivedDlt, "메시지를 받지 못했습니다.");
        assertEquals("DLT", receivedDltMessage);
    }

}