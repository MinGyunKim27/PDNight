package org.example.pdnight.domain.outbox.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.outbox.domain.OutboxCommander;
import org.example.pdnight.domain.outbox.domain.OutboxEvent;
import org.example.pdnight.domain.outbox.enums.OutboxStatus;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxCommanderService {

    private final OutboxCommander outboxCommander;
    private final ObjectMapper objectMapper;

    public void saveOutboxEvent(String aggregateType, Long aggregateId, String eventType, Object payloadObject){
        try{
            String json = objectMapper.writeValueAsString(payloadObject);

            OutboxEvent outboxEvent = OutboxEvent.create(
                    aggregateType,
                    aggregateId,
                    eventType,
                    json,
                    OutboxStatus.PENDING
            );

            outboxCommander.save(outboxEvent);
        } catch (Exception e){
            throw new BaseException(ErrorCode.FAILED_TO_SAVE_OUTBOX_EVENT);
        }
    }

}
