package org.example.pdnight.domain.recommendation.infra;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(indexName = "user_profiles")
@Setter
@Getter
public class UserProfileDocument {
    @Id
    Long userId;

    @Field(type = FieldType.Keyword)
    List<String> hobbys;
    @Field(type = FieldType.Keyword)
    List<String> skills;

    @Field(type = FieldType.Object)
    Map<String,Integer> likedTagCounts;

    @Field(type = FieldType.Object)
    Map<String,Integer> joinedTagCounts;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss||strict_date_time")
    LocalDateTime updatedAt;

    private UserProfileDocument(Long userId) {
        this.userId = userId;
    }

    public static UserProfileDocument of(Long userId) {
        return new UserProfileDocument(userId);
    }
}

