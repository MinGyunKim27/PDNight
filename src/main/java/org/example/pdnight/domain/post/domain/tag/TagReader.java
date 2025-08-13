package org.example.pdnight.domain.post.domain.tag;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TagReader {

    List<String> getNamesByIds(List<Long> tagIdList);

    Map<Long, String> getNamesByIdsMap(Set<Long> tagIdList);

    List<Tag> searchTags(String tagName);
}
