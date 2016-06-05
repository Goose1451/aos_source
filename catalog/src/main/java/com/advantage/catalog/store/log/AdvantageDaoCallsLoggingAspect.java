package com.advantage.catalog.store.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.advantage.catalog.util.ArgumentValidationHelper;

/**
 * An aspect class for logging data access object methods calls.
 */
@Aspect
public class AdvantageDaoCallsLoggingAspect {

    @Before("execution(* com.advantage.catalog.store.dao.*.*(..))")
    public void logBeforeDaoCall(final JoinPoint joinPoint) {
        ArgumentValidationHelper.validateArgumentIsNotNull(joinPoint, "join point");
        logDaoCall(joinPoint, true);
    }

    @After("execution(* com.advantage.catalog.store.dao.*.*(..))")
    public void logAfterDaoCall(final JoinPoint joinPoint) {
        ArgumentValidationHelper.validateArgumentIsNotNull(joinPoint, "join point");
        logDaoCall(joinPoint, false);
    }

    private void logDaoCall(final JoinPoint joinPoint, final boolean before) {
        assert joinPoint != null;

        final Signature signature = joinPoint.getSignature();
        final Class<?> cls = signature.getDeclaringType();
        //final Logger logger = LoggerFactory.getLogger(cls);
        final Logger logger = LoggerFactory.getLogger(AdvantageDaoCallsLoggingAspect.class);
        final String daoMethodName = signature.getName();
        final StringBuilder info;

        if (before) {
            info = new StringBuilder("Before");
        } else {
            info = new StringBuilder("After");
        }

        info.append(daoMethodName);
        final String infoString = info.toString();
        //TODO-EVG Test logger
        logger.info("EVG AOP LOG:" + infoString);
    }
}