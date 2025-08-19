package org.example.pdnight.domain.post.application.tagUseCase;

import org.example.pdnight.domain.post.presentation.dto.request.TagRequest;
import org.example.pdnight.domain.post.presentation.dto.response.TagResponse;

import java.util.List;

public interface TagService {

    TagResponse createTag(TagRequest tagRequest);

    List<TagResponse> searchTagList(String tag);

}
