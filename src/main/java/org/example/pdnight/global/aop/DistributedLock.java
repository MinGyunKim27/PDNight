package org.example.pdnight.global.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String key();                    // "postId" 또는 "eventId" 등 파라미터명
    long timeoutMs() default 3000L;
    int maxAttempts() default 5;
    long intervalMs() default 50L;
}