package org.example.pdnight.global.aop;

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
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(distributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String keyName = distributedLock.key(); // 파라미터 이름

        String key = generateKeyFromParam(joinPoint, keyName);
        RLock rLock = redissonClient.getLock("lock:" + key);

        boolean isLocked = rLock.tryLock(distributedLock.timeoutMs(), TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new BaseException(ErrorCode.LOCK_TIMEOUT);
        }

        try {
            return joinPoint.proceed();
        } finally {
            rLock.unlock();
        }
    }

    private String generateKeyFromParam(ProceedingJoinPoint joinPoint, String keyParamName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();  // 파라미터 이름들
        Object[] args = joinPoint.getArgs();                  // 전달된 인자 값들

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(keyParamName)) {
                return keyParamName + ":" + args[i]; // 예: "postId:1"
            }
        }

        throw new IllegalArgumentException("지정된 key 파라미터가 존재하지 않습니다: " + keyParamName);
    }
}
