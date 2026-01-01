package com.phiny.labs.apigateway.config;

import com.phiny.labs.apigateway.filter.JwtAuthenticationGatewayFilter;
import com.phiny.labs.apigateway.filter.RateLimitGatewayFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${app.discovery.server.uri}")
    private String discoveryServerUri;

    @Autowired
    private JwtAuthenticationGatewayFilter jwtFilter;
    
    @Autowired
    private RateLimitGatewayFilter rateLimitFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                        .route("discovery-server", r -> r
                                .path("/eureka/web")
                                .filters(f -> f.setPath("/"))
                                .uri(discoveryServerUri)
                        ).route("discovery-server-static", r -> r
                                .path("/eureka/**")
                                .uri(discoveryServerUri)
                        ).route("user-management-public", r -> r
                                .path("/users/create", "/users/login", "/users/password-reset/**")
                                .filters(f -> f.rewritePath("/users/(?<a>.*)","/api/users/${a}"))
                                .uri("lb://user-management")
                        ).route("user-management", r -> r
                                .path("/users/**")
                                .filters(f -> f.rewritePath("/users/(?<a>.*)","/api/users/${a}")
                                        .filter(jwtFilter.apply(new JwtAuthenticationGatewayFilter.Config()))
                                        .filter(rateLimitFilter.apply(new RateLimitGatewayFilter.Config())))
                                .uri("lb://user-management")
                        ).route("course-service", r -> r
                                .path("/api/categories/**", "/api/courses/**", "/api/enrollments/**",
                                        "/api/ratings/**", "/api/tags/**", "/api/topics/**")
                                .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationGatewayFilter.Config()))
                                        .filter(rateLimitFilter.apply(new RateLimitGatewayFilter.Config())))
                                .uri("lb://course-service")
                        )                        .route("tenant-service", r -> r
                                .path("/api/tenants/**")
                                .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationGatewayFilter.Config()))
                                        .filter(rateLimitFilter.apply(new RateLimitGatewayFilter.Config())))
                                .uri("lb://tenant-service")
                        ).route("content-management", r -> r
                                .path("/api/multimedia/**")
                                .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationGatewayFilter.Config()))
                                        .filter(rateLimitFilter.apply(new RateLimitGatewayFilter.Config())))
                                .uri("lb://content-management"))
                        .route("notification-service", r -> r
                                .path("/api/notifications/**")
                                .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationGatewayFilter.Config()))
                                        .filter(rateLimitFilter.apply(new RateLimitGatewayFilter.Config())))
                                .uri("lb://notification-service"))
                        .route("progress-service", r -> r
                                .path("/api/progress/**")
                                .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationGatewayFilter.Config()))
                                        .filter(rateLimitFilter.apply(new RateLimitGatewayFilter.Config())))
                                .uri("lb://progress-service"))
                        .build();
    }

}
