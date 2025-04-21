package com.projectdata.transaction.service;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class PrometheusService {
    private final MeterRegistry meterRegistry;
    private final Counter reqCounter;
    private final Timer reqTimer;

    public PrometheusService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        /*
         * Counter for tracking total requests
         */
        this.reqCounter = Counter.builder("app_requests_total")
                .description("Total number of requests")
                .register(meterRegistry);
        /*
         * Timer for measuring request latency
         */
        this.reqTimer = Timer.builder("app_request_latency")
                .description("Request latency in milliseconds")
                .register(meterRegistry);
    }

    public void incrementRequestCount(String endpoint) {
        Counter.builder("app_endpoint_requests_total")
                .tag("endpoint", endpoint)
                .description("Total requests per endpoint")
                .register(meterRegistry)
                .increment();
        reqCounter.increment();
    }

    public void recordLatency(long duration, TimeUnit timeUnit) {
        reqTimer.record(duration, timeUnit);
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(reqTimer);
    }

}
