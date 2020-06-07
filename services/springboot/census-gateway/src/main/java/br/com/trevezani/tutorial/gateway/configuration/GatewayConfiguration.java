package br.com.trevezani.tutorial.gateway.configuration;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfiguration {

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
        			    .failureRateThreshold(50)
        			    .waitDurationInOpenState(Duration.ofMillis(8000))
        			    .permittedNumberOfCallsInHalfOpenState(2)
        			    .slidingWindowSize(10)
        			    .minimumNumberOfCalls(5)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofMillis(8000)).build())
                .build());
    }

    // TODO: Security in the next phase
//	@Bean
//	SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
//		return http.httpBasic().and()
//				.csrf().disable()
//				.authorizeExchange()
//				.pathMatchers("/anything/**").authenticated()
//				.anyExchange().permitAll()
//				.and()
//				.build();
//	}    
    
    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just("1");
    }    
    
}
