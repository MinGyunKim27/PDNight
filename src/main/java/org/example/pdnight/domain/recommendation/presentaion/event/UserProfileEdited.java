package org.example.pdnight.domain.recommendation.presentaion.event;

import java.util.List;

public record UserProfileEdited(
        Long userId,
        List<String> hobbies,
        List<String> skills) {}