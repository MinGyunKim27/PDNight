package org.example.pdnight.domain.user.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.application.techStackUseCase.TechStackService;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.request.TechStackRequest;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.response.TechStackResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "UserTag")
@RestController
@RequestMapping("/api/tech-stacks")
@RequiredArgsConstructor
public class TechStackController {
    public final TechStackService techStackService;

    @PostMapping
    @Operation(summary = "기술 스택 추가", description = "기술 스택을 추가한다")
    public ResponseEntity<ApiResponse<TechStackResponse>> createTechStack(
            @Validated @RequestBody TechStackRequest dto
    ) {
        TechStackResponse techStack = techStackService.createTechStack(dto);

        return ResponseEntity.ok(ApiResponse.ok("기술 스택이 성공적으로 생성 되었습니다", techStack));
    }

    @GetMapping
    @Operation(summary = "기술 스택 조회", description = "기술 스택 목록을 조회한다")
    public ResponseEntity<ApiResponse<List<TechStackResponse>>> getTechStackList(
            @RequestParam(required = false) String techStack
    ) {
        List<TechStackResponse> techStackResponseList = techStackService.searchTechStackList(techStack);
        return ResponseEntity.ok(ApiResponse.ok("기술 스택 검색에 성공했습니다!", techStackResponseList));
    }
}
