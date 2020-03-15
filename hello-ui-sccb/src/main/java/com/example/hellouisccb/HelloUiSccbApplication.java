package com.example.hellouisccb;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@SpringBootApplication
public class HelloUiSccbApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloUiSccbApplication.class, args);
	}

	// 全CircuitBreaker共通の設定を書く
	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
		TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
				// 何かカスタマイズするコードを書く
				.build();
		CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
				// 何かカスタマイズするコードを書く
				.build();
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.timeLimiterConfig(timeLimiterConfig)
				.circuitBreakerConfig(circuitBreakerConfig)
				.build());
	}

	// 個別のCircuitBreakerの設定を書く
	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> slowCustomizer() {
		CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.ofDefaults();
		TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
				.timeoutDuration(Duration.ofSeconds(3))
				.build();
		return factory -> factory.configure(builder -> builder
				.circuitBreakerConfig(circuitBreakerConfig)
				.timeLimiterConfig(timeLimiterConfig)
				, "hello-api");
	}
}
