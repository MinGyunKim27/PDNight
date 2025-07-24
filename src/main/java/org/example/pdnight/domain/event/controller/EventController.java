package org.example.pdnight.domain.event.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.event.dto.request.EventCreateRequest;
import org.example.pdnight.domain.event.service.EventService;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 이벤트 생성
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createEvent(
            @RequestBody EventCreateRequest request
    ){
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 생성 성공했습니다.", eventService.createEvent(request))
        );
    }

    // 이벤트 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> findEventById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 조회 성공했습니다.", eventService.findEventById(id))
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateEvent(
            @PathVariable Long id,
            @RequestBody EventCreateRequest request
    ){
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 수정 성공했습니다", eventService.updateEvent(id, request))
        );
    }
}
