package org.example.pdnight.domain.post.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.post.domain.tag.Tag;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TagResponse {
    private Long id;
    private String tagName;

    public static TagResponse from(Tag tag){
        return new TagResponse(tag.getId(), tag.getName());
    }
}
