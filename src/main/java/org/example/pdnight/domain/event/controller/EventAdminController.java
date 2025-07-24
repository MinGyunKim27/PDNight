package org.example.pdnight.domain.event.controller;

import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.event.dto.request.EventCreateRequest;
import org.example.pdnight.domain.event.service.EventService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

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

    // 이벤트 리스트 조회
    @GetMapping
    public ResponseEntity<ApiResponse<?>> findEventById(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 리스트 조회 성공했습니다.", eventService.findEventList(pageable))
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

    // 이벤트 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteEvent(
            @PathVariable Long id
    ){
        eventService.deleteEventById(id);
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 삭제 성공했습니다.", null)
        );
    }
}
