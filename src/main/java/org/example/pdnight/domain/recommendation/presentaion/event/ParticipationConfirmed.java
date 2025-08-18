package org.example.pdnight.domain.recommendation.presentaion.event;

import java.util.Collection;
import java.util.List;

public record ParticipationConfirmed(
        List<Long> userIds,
        Collection<String> postTags) {}