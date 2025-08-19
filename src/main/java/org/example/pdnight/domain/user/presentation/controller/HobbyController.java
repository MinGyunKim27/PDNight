package org.example.pdnight.domain.user.presentation.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.application.hobbyUseCase.HobbyService;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.request.HobbyRequest;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.response.HobbyResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "UserTag")
@RestController
@RequestMapping("/api/hobbies")
@RequiredArgsConstructor
public class HobbyController {
    private final HobbyService hobbyService;

    @PostMapping
    @Operation(summary = "취미 추가", description = "취미을 추가한다")
    public ResponseEntity<ApiResponse<HobbyResponse>> createHobby(
            @Validated @RequestBody HobbyRequest dto
    ){
        HobbyResponse hobby = hobbyService.createHobby(dto);

        return ResponseEntity.ok(ApiResponse.ok("취미가 추가 되었습니다.",hobby));
    }

    @GetMapping
    @Operation(summary = "취미 조회", description = "취미 목록을 조회한다")
    private ResponseEntity<ApiResponse<List<HobbyResponse>>> searchHobby(
            @RequestParam (required = false)  String searchHobby
    ){
        System.out.println(searchHobby);
        List<HobbyResponse> hobbyResponses = hobbyService.searchHobby(searchHobby);
        return ResponseEntity.ok(ApiResponse.ok("취미 리스트가 출력 되었습니다.", hobbyResponses));
    }
}
