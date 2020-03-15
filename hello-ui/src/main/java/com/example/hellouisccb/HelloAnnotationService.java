package com.example.hellouisccb;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigurationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class HelloAnnotationService {

    private static final String INSTANCE_NAME = "hello-api";

    private final RestTemplate restTemplate;
    private final String helloApiUrl;

    public HelloAnnotationService(RestTemplateBuilder restTemplateBuilder,
                                  CircuitBreakerConfigurationProperties properties,
                                  @Value("${hello-api.url}") String helloApiUrl) {
        Duration slowCallDurationThreshold = properties.getInstances()
                .get(INSTANCE_NAME).getSlowCallDurationThreshold();
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(slowCallDurationThreshold)
                .setReadTimeout(slowCallDurationThreshold)
                .build();
        this.helloApiUrl = helloApiUrl;
    }

    @CircuitBreaker(name = "hello-api", fallbackMethod = "helloFallback")
    public String hello() {
        return restTemplate.getForObject(helloApiUrl + "/hello", String.class);
    }

    public String helloFallback(Throwable t) {
        t.printStackTrace();
        return "Recover";
    }
}
