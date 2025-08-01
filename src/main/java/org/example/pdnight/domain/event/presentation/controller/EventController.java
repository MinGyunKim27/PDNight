package org.example.pdnight.domain.event.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.event.application.eventUseCase.EventService;
import org.example.pdnight.domain.event.presentation.dto.request.EventCreateRequest;
import org.example.pdnight.domain.event.presentation.dto.response.EventParticipantResponse;
import org.example.pdnight.domain.event.presentation.dto.response.EventResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 이벤트 조회
    @GetMapping("/events/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> findEventById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 조회 성공했습니다.", eventService.findEventById(id))
        );
    }

    // 이벤트 리스트 조회
    @GetMapping("/events")
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
    @PostMapping("/events/{id}/participants")
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

    // 내가 참가한 이벤트 조회
    @GetMapping("/my/participant-events")
    public ResponseEntity<ApiResponse<PagedResponse<EventResponse>>> getMyParticipantEvents(
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @PageableDefault() Pageable pageable
    ) {
        Long userId = loginUser.getUserId();
        PagedResponse<EventResponse> myParticipantEvents = eventService.findMyParticipantEvents(userId, pageable);
        return ResponseEntity.ok(
                ApiResponse.ok("참가한 이벤트 목록 조회 성공했습니다.", myParticipantEvents)
        );
    }

    // ------------------------------ Admin 컨트롤러 합치기 -----------------------------

    // 이벤트 생성
    @PostMapping("/admin/events")
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @RequestBody EventCreateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 생성 성공했습니다.", eventService.createEvent(request))
        );
    }

    // 이벤트 수정
    @PatchMapping("/admin/events/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable Long id,
            @RequestBody EventCreateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 수정 성공했습니다", eventService.updateEvent(id, request))
        );
    }

    // 이벤트 삭제
    @DeleteMapping("/admin/events/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @PathVariable Long id
    ) {
        eventService.deleteEventById(id);
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 삭제 성공했습니다.", null)
        );
    }

    // 이벤트 참가 인원 리스트 조회
    @GetMapping("/admin/events/{id}/participants")
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
