package com.example.helloui;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

@Service
public class HelloService {

    private final RestTemplate restTemplate;
    private final String helloApiUrl;
    private final CircuitBreaker circuitBreaker;

    public HelloService(RestTemplate restTemplate, @Value("${hello-api.url}") String helloApiUrl,
                        CircuitBreakerRegistry registry) {
        this.restTemplate = restTemplate;
        this.helloApiUrl = helloApiUrl;
        this.circuitBreaker = registry.circuitBreaker("hello-api");
    }

    public String hello() {
        Supplier<String> decoratedSupplier = circuitBreaker.decorateSupplier(
                () -> restTemplate.getForObject(helloApiUrl + "/hello", String.class));
        Try<String> result = Try.ofSupplier(decoratedSupplier)
                .map(message -> "Hello from " + message) // このmap()は成功時のみ呼ばれる
                .recover(throwable -> "Recovery"); // 失敗時のみ呼ばれる

        return result.get() + " (state = " + circuitBreaker.getState() + ")";
    }

    public String slow() {
        Supplier<String> decoratedSupplier = circuitBreaker.decorateSupplier(
                () -> restTemplate.getForObject(helloApiUrl + "/slow", String.class));
        Try<String> result = Try.ofSupplier(decoratedSupplier)
                .recover(throwable -> "Recovery"); // 失敗時のみ呼ばれる

        return result.get() + " (state = " + circuitBreaker.getState() + ")";
    }
}
