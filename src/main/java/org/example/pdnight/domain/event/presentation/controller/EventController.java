package org.example.pdnight.domain.event.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.event.presentation.dto.response.EventResponse;
import org.example.pdnight.domain.event.application.eventUserCase.EventServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventServiceImpl eventService;

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

/* ---------------------------- 이벤트 참가 컨트롤러 합쳐야함 ----------------------------
@RestController
@RequestMapping("/api/events/{id}/participants")
@RequiredArgsConstructor
public class EventParticipantController {

    private final EventParticipantService eventParticipantService;

    // 이벤트 참가 신청
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addParticipant(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ){
        Long userId = loginUser.getUserId();
        eventParticipantService.addParticipant(id, userId);
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 참가 신청 성공했습니다.", null)
        );
    }
}
*/

/* --------------------- 어드민 컨트롤러 합치기 ---------------------
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private final EventService eventService;

    // 이벤트 생성
    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @RequestBody EventCreateRequest request
    ){
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 생성 성공했습니다.", eventService.createEvent(request))
        );
    }

    // 이벤트 리스트 조회
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<EventResponse>>> findEventById(
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
    public ResponseEntity<ApiResponse<EventResponse>> findEventById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 조회 성공했습니다.", eventService.findEventById(id))
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable Long id,
            @RequestBody EventCreateRequest request
    ){
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 수정 성공했습니다", eventService.updateEvent(id, request))
        );
    }

    // 이벤트 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @PathVariable Long id
    ){
        eventService.deleteEventById(id);
        return ResponseEntity.ok(
                ApiResponse.ok("이벤트 삭제 성공했습니다.", null)
        );
    }
}
 */


/* ---------------------------- 이벤트 참가 어드민 컨트롤러 합쳐야함 ----------------------------
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

 */
