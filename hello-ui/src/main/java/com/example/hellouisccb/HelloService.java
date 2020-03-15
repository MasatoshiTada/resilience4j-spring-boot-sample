package com.example.hellouisccb;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigurationProperties;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.function.Supplier;

@Service
public class HelloService {

    private static final String INSTANCE_NAME = "hello-api";

    private final RestTemplate restTemplate;
    private final String helloApiUrl;
    private final CircuitBreaker circuitBreaker;

    public HelloService(RestTemplateBuilder restTemplateBuilder,
                        CircuitBreakerConfigurationProperties properties,
                        @Value("${hello-api.url}") String helloApiUrl,
                        CircuitBreakerRegistry circuitBreakerRegistry) {
        Duration slowCallDurationThreshold = properties.getInstances()
                .get(INSTANCE_NAME).getSlowCallDurationThreshold();
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(slowCallDurationThreshold)
                .setReadTimeout(slowCallDurationThreshold)
                .build();
        this.helloApiUrl = helloApiUrl;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(INSTANCE_NAME);
    }

    public String hello() {
        CircuitBreaker.State beforeState = circuitBreaker.getState();
        Supplier<String> decoratedSupplier = circuitBreaker.decorateSupplier(
                () -> restTemplate.getForObject(helloApiUrl + "/hello", String.class));
        Try<String> result = Try.ofSupplier(decoratedSupplier)
                .map(message -> "Hello from " + message) // このmap()は成功時のみ呼ばれる
                .recover(throwable -> "Recovery"); // 失敗時のみ呼ばれる
        CircuitBreaker.State afterState = circuitBreaker.getState();
        if (beforeState == afterState) {
            return result.get() + " (state = " + afterState + ")";
        } else {
            return result.get() + " (state = " + beforeState + " -> " + afterState + ")";
        }
    }

    public String slow() {
        CircuitBreaker.State beforeState = circuitBreaker.getState();
        Supplier<String> decoratedSupplier = circuitBreaker.decorateSupplier(
                () -> restTemplate.getForObject(helloApiUrl + "/slow", String.class));
        Try<String> result = Try.ofSupplier(decoratedSupplier)
                .recover(throwable -> "Recovery"); // 失敗時のみ呼ばれる
        CircuitBreaker.State afterState = circuitBreaker.getState();
        if (beforeState == afterState) {
            return result.get() + " (state = " + afterState + ", metrics = " + printMetrics(circuitBreaker) + ")";
        } else {
            return result.get() + " (state = " + beforeState + " -> " + afterState + ", metrics = " + printMetrics(circuitBreaker) + ")";
        }
    }

    private String printMetrics(CircuitBreaker circuitBreaker) {
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        return String.format("Metrics(slowCalls = %d)", metrics.getNumberOfSlowCalls());
    }
}
