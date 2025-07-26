package org.example.pdnight.global.aop;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {

    private final RedissonClient redissonClient;

    @PostConstruct
    public void init() {
        log.info("🔧 DistributedLockAspect Bean 등록됨");
    }

    @Around("@annotation(distributedLock)")
    public Object executeLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String key = generateKey(joinPoint, distributedLock.key());

        if(key.isEmpty() || distributedLock.intervalMs() < 0 || distributedLock.timeoutMs() < 0){
            throw new BaseException(ErrorCode.LOCK_GET_FAILED);
        }

        log.info("🔐 Distributed Lock Key = {}", key);
        RLock rLock = redissonClient.getLock(key);

        boolean lockAcquired = false;
        try {
            // 락 획득 시도
            lockAcquired = rLock.tryLock(
                    distributedLock.timeoutMs(),  // wait time
                    distributedLock.leaseTimeMs(),  // lock lease time
                    TimeUnit.MILLISECONDS
            );

            if (!lockAcquired) {
                log.warn("락 획득에 실패했습니다. key = {}", key);
                throw new BaseException(ErrorCode.LOCK_GET_FAILED);
            }

            // 메소드 실행
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 인터럽트 발생", e);
        } finally {
            if (lockAcquired && rLock.isHeldByCurrentThread()) {
                try {
                    rLock.unlock();
                    log.info("락 해제 완료: key = {}", key);
                } catch (IllegalMonitorStateException e) {
                    log.error("락 해제 중 오류: key = {}", key, e);
                }

            }
        }
    }

    // 기존 코드 유지
    private String generateKey(ProceedingJoinPoint joinPoint, String keyTemplate) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        EvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        ExpressionParser parser = new SpelExpressionParser();
        String resolvedKey = parser.parseExpression(keyTemplate).getValue(context, String.class);

        return String.format("lock:%s:%s", methodName, resolvedKey);
    }
}