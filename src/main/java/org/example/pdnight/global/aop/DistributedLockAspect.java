package org.example.pdnight.global.aop;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Order(1)
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

        log.info("AOP 시작");

        try {
            String key = generateKey(joinPoint, distributedLock.key());
            log.info("Distributed Lock Key = {}", key);

            // 🚨 여기서 예외가 발생하는지 확인
            log.info("락 획득 시도 전 체크");

            if(key.isEmpty() || distributedLock.timeoutMs() < 0){
                log.error("락 파라미터 검증 실패: key={}, intervalMs={}, timeoutMs={}",
                        key, distributedLock.intervalMs(), distributedLock.timeoutMs());
                throw new BaseException(ErrorCode.LOCK_GET_FAILED);
            }

            log.info("🔒 RLock 객체 생성 시도");
            RLock rLock = redissonClient.getLock(key);
            log.info("RLock 객체 생성 성공: {}", rLock.getName());

            boolean lockAcquired = false;
            try {
                log.info("🎯 락 획득 시도 시작: timeout={}ms, lease={}ms",
                        distributedLock.timeoutMs(), distributedLock.leaseTimeMs());

                // 락 획득 시도
                lockAcquired = rLock.tryLock(
                        distributedLock.timeoutMs(),
                        distributedLock.leaseTimeMs(),
                        TimeUnit.MILLISECONDS
                );

                log.info("락 획득 결과: {}", lockAcquired);

                if (!lockAcquired) {
                    log.warn("락 획득에 실패했습니다. key = {}", key);
                    throw new BaseException(ErrorCode.LOCK_GET_FAILED);
                }

                log.info("🚀 메소드 실행 시작");
                Object result = joinPoint.proceed();
                log.info("메소드 실행 완료");
                return result;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("락 대기 중 인터럽트", e);
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
        } catch (Exception e) {
            log.error("AOP 실행 중 예외 발생", e);
            throw e;
        }
    }

    private String generateKey(ProceedingJoinPoint joinPoint, String keyTemplate) {
        try {
            log.info("키 생성 시작: template = {}", keyTemplate);

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.getMethod().getName();
            Object[] args = joinPoint.getArgs();
            String[] paramNames = signature.getParameterNames();

            log.info("메소드 정보: name={}, args={}, params={}",
                    methodName, Arrays.toString(args), Arrays.toString(paramNames));

            EvaluationContext context = new StandardEvaluationContext();

            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
                log.info("변수 설정: {}={}", paramNames[i], args[i]);
            }

            ExpressionParser parser = new SpelExpressionParser();
            String resolvedKey = parser.parseExpression(keyTemplate).getValue(context, String.class);
            String finalKey = String.format("lock:%s:%s", methodName, resolvedKey);

            log.info("키 생성 완료: {}", finalKey);
            return finalKey;

        } catch (Exception e) {
            log.error("키 생성 중 예외 발생: template = {}", keyTemplate, e);
            throw new RuntimeException("키 생성 실패", e);
        }
    }
}