package org.example.pdnight.domain.event.application.eventUseCase;

import org.example.pdnight.domain.event.domain.EventReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EventReaderServiceTest {
    @InjectMocks
    private EventReaderService eventReaderService;

    @Mock
    private EventReader eventReader;

    @Test
    @DisplayName("이벤트 조회")
    void 이벤트_조회(){

    }

    @Test
    @DisplayName("이벤트 리스트 조회")
    void 이벤트_리스트_조회 () {

    }
}
