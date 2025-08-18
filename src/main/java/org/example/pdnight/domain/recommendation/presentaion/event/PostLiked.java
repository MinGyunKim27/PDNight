package org.example.pdnight.domain.recommendation.presentaion.event;

import java.util.Collection;

public record PostLiked(
        Long userId,
        Collection<String> postTags) {}