package com.example.hellouisccb;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.IntervalFunction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;

@SpringBootApplication
public class HelloUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloUiApplication.class, args);
		// Constant Backoff
		CircuitBreakerConfig config1 = CircuitBreakerConfig.custom()
				.permittedNumberOfCallsInHalfOpenState(5)
				.waitDurationInOpenState(Duration.ofSeconds(3))
				.build();
		// Randomized Backoff
		CircuitBreakerConfig config2 = CircuitBreakerConfig.custom()
				.permittedNumberOfCallsInHalfOpenState(5)
				.waitIntervalFunctionInOpenState(IntervalFunction.ofRandomized())
				.build();
		// Exponential Backoff
		CircuitBreakerConfig config3 = CircuitBreakerConfig.custom()
				.permittedNumberOfCallsInHalfOpenState(5)
				.waitIntervalFunctionInOpenState(
						IntervalFunction.ofExponentialBackoff(Duration.ofSeconds(1), 2))
				.build();
		// Exponential Random Backoff
		CircuitBreakerConfig config4 = CircuitBreakerConfig.custom()
				.permittedNumberOfCallsInHalfOpenState(5)
				.waitIntervalFunctionInOpenState(
						IntervalFunction.ofExponentialRandomBackoff(Duration.ofSeconds(1), 2))
				.build();
	}
}
