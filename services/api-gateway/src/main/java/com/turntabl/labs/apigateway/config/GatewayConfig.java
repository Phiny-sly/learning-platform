package com.turntabl.labs.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${app.discovery.server.uri}")
    private String discoveryServerUri;

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
                        ).route("course-service", r -> r
                                .path("/api/categories/**", "/api/courses/**", "/api/enrollments/**",
                                        "/api/ratings/**", "/api/tags/**", "/api/topics/**")
                                .uri("lb://course-service")
                        ).route("user-management", r -> r
                                .path("/users/**").filters(f -> f.rewritePath("/users/(?<a>.*)","/api/users/${a}"))
                                .uri("lb://user-management")
                        ).route("tenant-service", r -> r
                                .path("/api/tenants/**")
                                .uri("lb://tenant-service")
                        ).route("content-management", r -> r
                                .path("/api/multimedia/**")
                                .uri("lb://content-management"))
                        .build();
    }

}
