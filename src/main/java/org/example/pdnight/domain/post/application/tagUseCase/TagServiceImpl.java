package org.example.pdnight.domain.post.application.tagUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.presentation.dto.request.TagRequest;
import org.example.pdnight.domain.post.presentation.dto.response.TagResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagCommanderService commanderService;
    private final TagReaderService readerService;

    @Override
    public TagResponse createTag(TagRequest tagRequest) {
        return commanderService.createTag(tagRequest);
    }

    @Override
    public List<TagResponse> searchTagList(String tag) {
        return readerService.searchTags(tag);
    }
}
