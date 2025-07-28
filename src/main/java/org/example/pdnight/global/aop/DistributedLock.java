package org.example.pdnight.global.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String key();
    long timeoutMs() default 3000L;
    long leaseTimeMs() default 3000;
    long intervalMs() default 50L;
}