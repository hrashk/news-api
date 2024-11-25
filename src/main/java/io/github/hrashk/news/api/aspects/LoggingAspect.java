package io.github.hrashk.news.api.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    @Pointcut("within(@org.springframework.stereotype.Service *) && execution(public * *(..))")
    public void springServicePointcut() {
        throw new UnsupportedOperationException();
    }

    @Pointcut("within(io.github.hrashk.news.api..*)")
    public void applicationPackagePointcut() {
        throw new UnsupportedOperationException();
    }

    @AfterThrowing(pointcut = "applicationPackagePointcut() && springServicePointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint jp, Throwable e) {
        Logger logger = LoggerFactory.getLogger(jp.getTarget().getClass());

        logger.debug("Exception " + jp.getSignature().toShortString(), e);
    }

    @Around("applicationPackagePointcut() && springServicePointcut()")
    public Object invoke(ProceedingJoinPoint pjp) throws Throwable {
        Logger logger = LoggerFactory.getLogger(pjp.getTarget().getClass());
        final String args = Arrays.toString(pjp.getArgs());

        logger.debug("Enter: {} with argument[s] = {}", pjp.getSignature().toShortString(), args);

        final Object result = pjp.proceed();

        logger.debug("Exit: {} with result = {}", pjp.getSignature().toShortString(), result);

        return result;
    }
}
