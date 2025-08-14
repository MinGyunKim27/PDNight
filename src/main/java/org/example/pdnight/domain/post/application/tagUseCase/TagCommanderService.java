package org.example.pdnight.domain.post.application.tagUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.tag.Tag;
import org.example.pdnight.domain.post.domain.tag.TagCommander;
import org.example.pdnight.domain.post.presentation.dto.request.TagRequest;
import org.example.pdnight.domain.post.presentation.dto.response.TagResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TagCommanderService {

    private final TagCommander tagCommander;

    @Transactional
    public TagResponse createTag(TagRequest dto) {
        validateExistTag(dto);

        Tag tag = Tag.create(dto.getTagName());

        Tag save = tagCommander.save(tag);
        return TagResponse.from(save);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // validate
    private void validateExistTag(TagRequest dto) {
        boolean exists = tagCommander.existsTagByName(dto.getTagName());
        if (exists) {
            throw new BaseException(ErrorCode.TAG_ALREADY_EXISTS);
        }
    }
}
