package org.example.pdnight.domain.post.presentation;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.tagUseCase.TagService;
import org.example.pdnight.domain.post.presentation.dto.request.TagRequest;
import org.example.pdnight.domain.post.presentation.dto.response.TagResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @PostMapping
    public ResponseEntity<ApiResponse<TagResponse>> createTag(
            @Validated @RequestBody TagRequest dto
    ) {
        TagResponse tagResponse = tagService.createTag(dto);
        System.out.println(dto.getTagName());

        return ResponseEntity.ok(ApiResponse.ok("태그가 성공적으로 생성 되었습니다", tagResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponse>>> searchTags(
            @RequestParam(required = false) String tagName
    ) {
        List<TagResponse> tagResponsesList = tagService.searchTagList(tagName);
        return ResponseEntity.ok(ApiResponse.ok("태그 검색에 성공했습니다!", tagResponsesList));
    }
}
