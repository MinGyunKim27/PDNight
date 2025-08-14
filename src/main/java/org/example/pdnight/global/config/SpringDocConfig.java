package org.example.pdnight.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "🌃 판교의 밤 (PDNight) API Document",
                description = "PDNight Spring Doc API Document",
                version = "v4" //개발 버전
        ),
        tags = {
                @Tag(name = "Auth", description = "인증 기능"),

                @Tag(name = "AdminUser", description = "관리자/사용자 기능"),
                @Tag(name = "User", description = "사용자 기능"),
                @Tag(name = "UserTag", description = "취미, 기술스택 기능"),
                @Tag(name = "Follow", description = "팔로우 기능"),

                @Tag(name = "AdminPost", description = "관리자/게시글 기능"),
                @Tag(name = "Post", description = "게시글 기능"),
                @Tag(name = "PostTag", description = "게시글 태그 기능"),
                @Tag(name = "Participate", description = "게시글 참여 기능"),
                @Tag(name = "PostLike", description = "게시글 좋아요 기능"),
                @Tag(name = "Invite", description = "게시글 초대 기능"),

                @Tag(name = "Comment", description = "댓글 기능"),
                @Tag(name = "Review", description = "리뷰 기능"),

                @Tag(name = "AdminPromotion", description = "관리자/프로모션 기능"),
                @Tag(name = "Promotion", description = "프로모션 기능"),

                @Tag(name = "AdminCoupon", description = "관리자/쿠폰 기능"),
                @Tag(name = "Coupon", description = "쿠폰 기능"),

                @Tag(name = "Notification", description = "알림 기능"),
                @Tag(name = "Chat", description = "채팅방 기능"),
        },
        security = {
                @SecurityRequirement(name = "Bearer Authentication"),
        }
)
@SecuritySchemes({
        @SecurityScheme(name = "Bearer Authentication",
                type = SecuritySchemeType.HTTP,
                in = SecuritySchemeIn.HEADER,
                paramName = "Authorization",
                scheme = "bearer",
                description = "로그인 후 발급되는 JWT 토큰을 입력하세요."
        ),
})
@Configuration
public class SpringDocConfig {
}
