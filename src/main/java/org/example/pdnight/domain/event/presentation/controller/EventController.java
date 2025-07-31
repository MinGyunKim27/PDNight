package org.example.pdnight.domain.event.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.event.application.eventUserCase.EventService;
import org.example.pdnight.domain.event.presentation.dto.request.EventCreateRequest;
import org.example.pdnight.domain.event.presentation.dto.response.EventParticipantResponse;
import org.example.pdnight.domain.event.presentation.dto.response.EventResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 이벤트 조회
    @GetMapping("/api/events/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> findEventById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 조회 성공했습니다.", eventService.findEventById(id))
        );
    }

    // 이벤트 리스트 조회
    @GetMapping("/api/events")
    public ResponseEntity<ApiResponse<PagedResponse<EventResponse>>> findEventById(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 리스트 조회 성공했습니다.", eventService.findEventList(pageable))
        );
    }

    // 이벤트 참가 신청
    @PostMapping("/api/events/{id}/participants")
    public ResponseEntity<ApiResponse<Void>> addParticipant(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long userId = loginUser.getUserId();
        eventService.addParticipant(id, userId);
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 참가 신청 성공했습니다.", null)
        );
    }

    // ------------------------------ Admin 컨트롤러 합치기 -----------------------------

    // 이벤트 생성
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/events")
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @RequestBody EventCreateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 생성 성공했습니다.", eventService.createEvent(request))
        );
    }

    // 이벤트 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/api/admin/events/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable Long id,
            @RequestBody EventCreateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 수정 성공했습니다", eventService.updateEvent(id, request))
        );
    }

    // 이벤트 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/admin/events/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @PathVariable Long id
    ) {
        eventService.deleteEventById(id);
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 삭제 성공했습니다.", null)
        );
    }

    // 이벤트 참가 인원 리스트 조회
    @GetMapping("/api/admin/events/{id}/participants")
    public ResponseEntity<ApiResponse<PagedResponse<EventParticipantResponse>>> getEventParticipants(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 참가 인원 리스트 조회 성공했습니다.", eventService.findEventParticipantList(id, pageable))
        );
    }

}
