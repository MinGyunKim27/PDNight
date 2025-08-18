package org.example.pdnight.domain.post.application.tagUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.tag.Tag;
import org.example.pdnight.domain.post.domain.tag.TagReader;
import org.example.pdnight.domain.post.presentation.dto.response.TagResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TagReaderService {

    private final TagReader tagReader;

    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<TagResponse> searchTags(String tagName) {
        List<Tag> tagList = tagReader.searchTags(tagName);
        return tagList.stream()
                .map(TagResponse::from)
                .toList();
    }
}
