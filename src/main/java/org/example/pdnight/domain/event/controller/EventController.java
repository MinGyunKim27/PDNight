package org.example.pdnight.domain.event.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.event.dto.request.EventCreateRequest;
import org.example.pdnight.domain.event.service.EventService;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 이벤트 생성
    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createEvent(
            @RequestBody EventCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ){
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 생성 성공했습니다.", eventService.createEvent(request))
        );
    }
}
