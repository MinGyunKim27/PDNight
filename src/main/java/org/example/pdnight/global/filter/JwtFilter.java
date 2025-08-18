package org.example.pdnight.global.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.global.common.enums.JobCategory;
import org.example.pdnight.global.common.enums.UserRole;
import org.example.pdnight.global.constant.CacheName;
import org.example.pdnight.global.utils.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String url = httpRequest.getRequestURI();


        if (url.startsWith("/webjars/") || url.startsWith("/css/") || url.startsWith("/js/") || url.startsWith("/images/")) {
                 chain.doFilter(request, response);
            return;
        }

        if (url.startsWith("/api-docs") || url.startsWith("/swagger-ui")) {
            chain.doFilter(request, response);
            return;
        }

        if (url.startsWith("/health")) {

            chain.doFilter(request, response);
            return;
        }

        if (url.startsWith("/api/auth/signup") || url.startsWith("/api/auth/login") || url.startsWith("/login")
                || url.startsWith("/ws-stomp") || url.startsWith("/chat/view") || url.startsWith("/chat/view/enter/")
        ) {

            chain.doFilter(request, response);
            return;
        }

        String bearerJwt = httpRequest.getHeader("Authorization");

        if (bearerJwt == null || !bearerJwt.startsWith("Bearer ")) {
            log.error("JWT 토큰이 필요합니다.");
            sendError(httpResponse, 400, "JWT 토큰이 필요합니다.");
            return;
        }

        String jwt = jwtUtil.substringToken(bearerJwt);

        try {
            Boolean isBlacklisted = redisTemplate.opsForSet().isMember(CacheName.BLACKLIST_TOKEN, jwt);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                log.error("블랙리스트에 추가된 토큰입니다.");
                sendError(httpResponse, 400,"블랙리스트에 추가된 토큰입니다.");
                return;
            }

            Claims claims = jwtUtil.extractClaims(jwt);
            if (claims == null) {
                log.error("잘못된 JWT 토큰입니다.");
                sendError(httpResponse, 400, "잘못된 JWT 토큰입니다.");
                return;
            }

            String roleStr = claims.get("userRole", String.class); // 문자열로 받기
            UserRole userRole = UserRole.valueOf(roleStr); // 문자열 → enum 변환
            Object userNickname = claims.get("userNickname");
            Long userAge = Long.valueOf((Integer) claims.get("age"));
            String genderStr  = claims.get("gender", String.class);
            Gender userGender = Gender.valueOf(genderStr);
            String jobCategoryStr = claims.get("jobCategory", String.class);
            JobCategory userJobCategory = JobCategory.valueOf(jobCategoryStr);


            Long userId = Long.parseLong(claims.getSubject());

            CustomUserDetails userDetails = new CustomUserDetails(
                    userId,
                    (String) userNickname,
                    "",
                    userRole,
                    userAge,
                    userGender,
                    userJobCategory);

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
            );

            chain.doFilter(request, response);
        } catch (SecurityException | MalformedJwtException e) {
            log.error("유효하지 않는 JWT 서명 입니다.", e);
            sendError(httpResponse, 401, "유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT token 입니다.", e);
            sendError(httpResponse, 401, "만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰 입니다.", e);
            sendError(httpResponse, 400, "지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("Internal server error", e);
            sendError(httpResponse, 500, "Internal server error.");
        }
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8"); // 한 번만 설정
        response.setCharacterEncoding("UTF-8"); // 명시적 인코딩
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
