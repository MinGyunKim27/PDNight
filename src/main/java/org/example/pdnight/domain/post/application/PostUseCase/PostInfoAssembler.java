package org.example.pdnight.domain.post.application.PostUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostTag;
import org.example.pdnight.domain.post.domain.tag.TagReader;
import org.example.pdnight.domain.post.presentation.dto.response.PostInfo;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostInfoAssembler {

    private final TagReader tagReader;

    public PostResponse toDto(Post post) {
        List<Long> tagIdList = post.getPostTagList().stream()
                .map(PostTag::getTagId)
                .toList();

        List<String> tagNameList = tagReader.getNamesByIds(tagIdList);

        return PostResponse.from(post, tagNameList);
    }

    public PostResponse toDtoWithCount(Post post, int acceptedParticipantsCount, int participantsCount) {
        List<Long> tagIdList = post.getPostTagList().stream()
                .map(PostTag::getTagId)
                .toList();

        List<String> tagNameList = tagReader.getNamesByIds(tagIdList);

        return PostResponse.toDtoWithCount(post, tagNameList, acceptedParticipantsCount, participantsCount);
    }


    public PostInfo toInfoDto(Post post) {
        List<Long> tagIdList = post.getPostTagList().stream()
                .map(PostTag::getTagId)
                .toList();

        List<String> tagNameList = tagReader.getNamesByIds(tagIdList);

        return PostInfo.toDto(
                post.getId(),
                post.getAuthorId(),
                post.getTitle(),
                post.getTimeSlot(),
                post.getPublicContent(),
                post.getStatus(),
                post.getMaxParticipants(),
                post.getGenderLimit(),
                post.getJobCategoryLimit(),
                tagNameList,
                post.getAgeLimit(),
                post.getIsFirstCome(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    //toDto
    public List<PostResponse> toDtoList(Page<Post> page) {
        List<Post> posts = page.getContent();

        Set<Long> tagIdList = posts.stream()
                .flatMap(p -> p.getPostTagList().stream())
                .map(PostTag::getTagId)
                .collect(Collectors.toSet());

        Map<Long, String> tagMap = tagReader.getNamesByIdsMap(tagIdList);

        return posts.stream()
                .map(post -> {
                    List<String> tagNameList = post.getPostTagList().stream()
                            .map(pt -> tagMap.getOrDefault(pt.getTagId(), ""))
                            .toList();

                    return PostResponse.from(post, tagNameList);
                })
                .toList();
    }
}
