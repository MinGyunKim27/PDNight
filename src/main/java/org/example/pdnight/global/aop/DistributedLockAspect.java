package org.example.pdnight.global.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {
    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;

    // Redisson 사용으로 변경
    @Around("@annotation(distributedLock)")
    public Object executeLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String key = generateKey(joinPoint, distributedLock.key());
        log.info("🔐 Distributed Lock Key = {}", key);

        RLock rLock = redissonClient.getLock(key);

        try {
            // 락 획득 시도
            boolean acquired = rLock.tryLock(
                    distributedLock.timeoutMs(),  // 최대 대기 시간
                    10000,                          // 락 유지 시간 (예: 5초)
                    TimeUnit.MILLISECONDS
            );

            if (!acquired) {
                throw new RuntimeException("락 획득 실패");
            }

            // 메소드 실행
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 인터럽트 발생", e);
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
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