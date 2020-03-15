package com.example.hellouisccb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HelloService {

    private static final String INSTANCE_NAME = "hello-api";

    private final RestTemplate restTemplate;
    private final String helloApiUrl;
    private final CircuitBreaker circuitBreaker;

    public HelloService(RestTemplateBuilder restTemplateBuilder,
                        @Value("${hello-api.url}") String helloApiUrl,
                        CircuitBreakerFactory circuitBreakerFactory) {
        this.restTemplate = restTemplateBuilder
                .build();
        this.helloApiUrl = helloApiUrl;
        this.circuitBreaker = circuitBreakerFactory.create(INSTANCE_NAME);
    }

    public String hello() {
        String result = circuitBreaker.run(
                () -> restTemplate.getForObject(helloApiUrl + "/hello", String.class),
                throwable -> "Recovery");
        return result;
    }

    public String slow() {
        String result = circuitBreaker.run(
                () -> restTemplate.getForObject(helloApiUrl + "/slow", String.class),
                throwable -> "Recovery");
        return result;
    }
}
