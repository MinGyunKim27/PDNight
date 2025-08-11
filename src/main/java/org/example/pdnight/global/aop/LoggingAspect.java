package org.example.pdnight.global.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

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


    @AfterReturning("applicationLayer()")
    public void logAfterMethodReturn(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName(); //클래스
        String methodName = joinPoint.getSignature().getName(); //메서드
        log.debug("[DEBUG] {},{}", className, methodName); //로그 저장
        log.info("[LOCAL] {}", joinPoint.getSignature().toShortString()); //콘솔 출력
    }
}
