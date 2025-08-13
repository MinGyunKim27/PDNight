package org.example.pdnight.domain.post.infra.post;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.PostUseCase.TagPort;
import org.example.pdnight.domain.post.domain.tag.TagReader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TagAdaptor implements TagPort {

    private final TagReader tagReader;

    @Override
    public List<String> findAllTagNames(List<Long> tagIdList) {
        return tagReader.getNamesByIds(tagIdList);
    }
}
