package org.example.pdnight.domain.user.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.application.techStackUseCase.TechStackService;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

import org.example.pdnight.domain.user.presentation.dto.techStackDto.request.TechStackRequest;
import org.example.pdnight.domain.user.presentation.dto.techStackDto.response.TechStackResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tech-stacks")
@RequiredArgsConstructor
public class TechStackController {
    public final TechStackService techStackService;

    @PostMapping
    public ResponseEntity<ApiResponse<TechStackResponse>> createTechStack(
            @Validated @RequestBody TechStackRequest dto
            ){
        TechStackResponse techStack = techStackService.createTechStack(dto);

        return ResponseEntity.ok(ApiResponse.ok("기술 스택이 성공적으로 생성 되었습니다",techStack));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TechStackResponse>>> getTechStackList(
            @RequestParam (required = false) String techStack
    ){
        List<TechStackResponse> techStackResponseList = techStackService.searchTechStackList(techStack);
        return ResponseEntity.ok(ApiResponse.ok("기술 스택 검색에 성공했습니다!", techStackResponseList));
    }
}
