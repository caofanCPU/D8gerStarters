package com.xyz.caofancpu.mvc.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Service切面
 *
 * @author D8GER
 */
@Aspect
@Component
public class ServiceAspect {

    @Around(value = "execution(* com.xyz..*.service.imp..*ServiceImpl.*(..))")
    public Object aroundMethod(ProceedingJoinPoint pjp)
            throws Throwable {
        Object result;
        MethodSignature joinPointObject = (MethodSignature) pjp.getSignature();
        result = pjp.proceed();
        return result;
    }
}
