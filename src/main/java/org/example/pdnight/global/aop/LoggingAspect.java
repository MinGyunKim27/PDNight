package org.example.pdnight.global.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    // 모든 Service 클래스의 메서드에 적용
    @Pointcut("execution(* org.example.pdnight.domain.auth.application.authUseCase.*.*(..))")
    public void authServiceLayer() {
    }

    @Pointcut("execution(* org.example.pdnight.domain.chat.application.chatMessageUseCase.*.*(..))")
    public void chatServiceLayer() {
    }

    @Pointcut("execution(* org.example.pdnight.domain.notification.application.notificationUseCase.*.*(..))")
    public void notificationServiceLayer() {
    }

    @Pointcut("execution(* org.example.pdnight.domain.post.application.PostUseCase.*.*(..))")
    public void postServiceLayer() {
    }

    @Pointcut("execution(* org.example.pdnight.domain.post.application.commentUseCase.*.*(..))")
    public void commentServiceLayer() {
    }

    @Pointcut("execution(* org.example.pdnight.domain.promotion.application.promotionUseCase.*.*(..))")
    public void promotionServiceLayer() {
    }

    @Pointcut("execution(* org.example.pdnight.domain.user.application.userUseCase.*.*(..))")
    public void userServiceLayer() {
    }

    @Pointcut("execution(* org.example.pdnight.domain.user.application.couponUseCase.*.*(..))")
    public void couponServiceLayer() {
    }

    @Pointcut("execution(* org.example.pdnight.domain.user.application.hobbyUseCase.*.*(..))")
    public void hobbyServiceLayer() {
    }

    @Pointcut("execution(* org.example.pdnight.domain.user.application.reviewUserCase.*.*(..))")
    public void reviewServiceLayer() {
    }

    @Pointcut("execution(* org.example.pdnight.domain.user.application.techStackUseCase.*.*(..))")
    public void techServiceLayer() {
    }

    @Pointcut("authServiceLayer() || chatServiceLayer() || notificationServiceLayer() || postServiceLayer() || commentServiceLayer() || promotionServiceLayer() || userServiceLayer() || couponServiceLayer() || hobbyServiceLayer() || reviewServiceLayer() || techServiceLayer()")
    public void applicationLayer() {
    }

    // 성공/실패 모두 로깅하는 메서드
    @Around("@annotation(SaveLog)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String requestInfo;

        // 현재 스레드에 웹 요청 컨텍스트가 있는지 확인
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            requestInfo = String.format("%s %s", request.getMethod(), request.getRequestURI());
        } else {
            // 웹 요청 컨텍스트가 없는 경우 - 카프카 리스너
            requestInfo = joinPoint.getSignature().toShortString();
        }
        log.debug("1");

        Object[] args = joinPoint.getArgs(); // 요청 파라미터 또는 바디

        log.debug("[REQUEST] {} | Args: {}", requestInfo, Arrays.toString(args));

        try {
            // 대상 메서드 실행
            log.debug("2");
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("[RESPONSE] {} | Return Value: {} | Time taken: {}ms", requestInfo, result, executionTime);

            return result;
        } catch (Throwable e) {
            log.debug("3");
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("[ERROR] {} | Exception: {} | Time taken: {}ms", requestInfo, e.getMessage(), executionTime);
            throw e;
        }
    }

    // 실패 시에만 로깅하는 메서드
    @Around("applicationLayer() && !@annotation(SaveLog)")
    public Object logOnFailure(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String requestInfo;
        log.debug("4");
        // 현재 스레드에 웹 요청 컨텍스트가 있는지 확인
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            log.debug("5");
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            requestInfo = String.format("%s %s", request.getMethod(), request.getRequestURI());
        } else {
            // 웹 요청 컨텍스트가 없는 경우 - 카프카 리스너
            log.debug("6");
            requestInfo = joinPoint.getSignature().toShortString();
        }

        try {
            // 성공한 경우, 로깅하지 않고 결과 반환
            log.debug("7");
            return joinPoint.proceed();

        } catch (Throwable e) {
            log.debug("8");
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("[ERROR] {} | Exception: {} | Time taken: {}ms", requestInfo, e.getMessage(), executionTime);
            throw e;
        }
    }
}
