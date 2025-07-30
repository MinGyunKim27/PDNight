package org.example.pdnight.domain1.event.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.common.dto.ApiResponse;
import org.example.pdnight.domain1.event.dto.response.EventResponse;
import org.example.pdnight.domain1.event.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 이벤트 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> findEventById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 조회 성공했습니다.", eventService.findEventById(id))
        );
    }
}
