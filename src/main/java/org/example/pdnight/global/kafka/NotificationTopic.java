package org.example.pdnight.global.kafka;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum NotificationTopic {

    PARTICIPATE_EVENT("participate.event", 6),
    SOCIAL_EVENT("social.event", 7),
    INVITE_EVENT("invite.event", 3),
    REVIEW_EVENT("review.event", 5),
    CHAT_EVENT("chat.event", 2),
    COUPON_EVENT("coupon.event", 1),
    AUTH_SIGNED_UP("auth.signedup", 1),
    AUTH_DELETE_TOPIC("auth.deleted", 1),
    POST_CONFIRMED("post.confirmed", 1),
    POST_DELETE_EVENT("post.deleted", 1),
    ;

    private final String topic;
    private final Integer partition;

    NotificationTopic(String topic, Integer partition) {
        this.topic = topic;
        this.partition = partition;
    }

    public String getTopic() {
        return topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public static List<TopicInfo> getAllTopics() {
        return Arrays.stream(NotificationTopic.values())
                .map(e -> new TopicInfo(e.getTopic(), e.getPartition()))
                .collect(Collectors.toList());
    }
}
