package org.example.pdnight.global.event;

import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.global.common.enums.JobCategory;

public record AuthSignedUpEvent (
        Long authId,
        String name,
        String nickname,
        Gender gender,
        Long age,
        JobCategory jobCategory
){}