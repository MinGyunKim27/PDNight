package org.example.pdnight.domain1.techStack.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.common.dto.ApiResponse;
import org.example.pdnight.domain1.techStack.dto.request.TechStackRequestDto;
import org.example.pdnight.domain1.techStack.dto.response.TechStackResponseDto;
import org.example.pdnight.domain1.techStack.service.TechStackService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/techStacks")
@RequiredArgsConstructor
public class TechStackController {
    public final TechStackService techStackService;

    @PostMapping
    public ResponseEntity<ApiResponse<TechStackResponseDto>> createTechStack(
            @Validated @RequestBody TechStackRequestDto dto
            ){
        TechStackResponseDto techStack = techStackService.createTechStack(dto);

        return ResponseEntity.ok(ApiResponse.ok("기술 스택이 성공적으로 생성 되었습니다",techStack));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TechStackResponseDto>>> getTechStackList(
            @RequestParam (required = false) String techStack
    ){
        List<TechStackResponseDto> techStackResponseDtoList = techStackService.searchTechStackList(techStack);
        return ResponseEntity.ok(ApiResponse.ok("기술 스택 검색에 성공했습니다!", techStackResponseDtoList));
    }
}
