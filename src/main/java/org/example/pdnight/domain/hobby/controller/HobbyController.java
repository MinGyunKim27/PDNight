package org.example.pdnight.domain.hobby.controller;


import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.hobby.dto.request.HobbyRequest;
import org.example.pdnight.domain.hobby.dto.response.HobbyResponse;
import org.example.pdnight.domain.hobby.service.HobbyService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hobby")
@RequiredArgsConstructor
public class HobbyController {
    private final HobbyService hobbyService;

    @PostMapping
    public ResponseEntity<ApiResponse<HobbyResponse>> createHobby(
            @Validated @RequestBody HobbyRequest dto
    ){
        HobbyResponse hobby = hobbyService.createHobby(dto);

        return ResponseEntity.ok(ApiResponse.ok("취미가 추가 되었습니다.",hobby));
    }

    @GetMapping
    private ResponseEntity<ApiResponse<List<HobbyResponse>>> searchHobby(
            @RequestParam String searchHobby
    ){
        System.out.println(searchHobby);
        List<HobbyResponse> hobbyResponses = hobbyService.searchHobby(searchHobby);
        return ResponseEntity.ok(ApiResponse.ok("취미 리스트가 출력 되었습니다.", hobbyResponses));
    }
}
