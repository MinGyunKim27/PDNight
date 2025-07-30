package org.example.pdnight.domain1.eventParticipant.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.common.dto.ApiResponse;
import org.example.pdnight.domain1.common.dto.PagedResponse;
import org.example.pdnight.domain1.eventParticipant.dto.response.EventParticipantResponse;
import org.example.pdnight.domain1.eventParticipant.service.EventParticipantService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin/events/{id}/participants")
@RequiredArgsConstructor
public class EventParticipantAdminController {

    private final EventParticipantService eventParticipantService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<EventParticipantResponse>>> getEventParticipants(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 참가 인원 리스트 조회 성공했습니다.", eventParticipantService.findEventParticipantList(id, pageable))
        );
    }
}
