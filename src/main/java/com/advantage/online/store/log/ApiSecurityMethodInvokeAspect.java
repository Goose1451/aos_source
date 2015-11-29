package com.advantage.online.store.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static com.advantage.online.store.user.util.ValidationHelper.isAuthorized;

@Aspect
public class ApiSecurityMethodInvokeAspect {
    @Around("execution(* *(..)) && @annotation(AppUserAuthorize)")
    public ResponseEntity authorize(ProceedingJoinPoint  joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = null;
        String token = "";
        for (Object arg : args) {
            if(arg instanceof HttpServletRequest) {
                request = (HttpServletRequest) arg;
            }
            if(arg instanceof String) {
                token = (String)arg;
            }
        }

        assert request != null;
        if(!isAuthorized(request.getSession(), token)) return unAuthorized();

        joinPoint.proceed();
        return null;
    }

    private static ResponseEntity unAuthorized(){
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }
}
