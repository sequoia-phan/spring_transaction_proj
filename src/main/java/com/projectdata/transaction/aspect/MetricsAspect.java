package com.projectdata.transaction.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.projectdata.transaction.service.PrometheusService;

@Aspect
@Component
public class MetricsAspect {

    private final PrometheusService prometheusService;

    public MetricsAspect(PrometheusService prometheusService) {
        this.prometheusService = prometheusService;
    }

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object measureEndpointMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        prometheusService.incrementRequestCount(methodName);
        var timer = prometheusService.startTimer();
        try {
            return joinPoint.proceed();
        } finally {
            prometheusService.stopTimer(timer);
        }
    }

}