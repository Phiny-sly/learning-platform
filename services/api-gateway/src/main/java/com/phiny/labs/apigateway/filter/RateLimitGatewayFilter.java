package com.phiny.labs.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitGatewayFilter extends AbstractGatewayFilterFactory<RateLimitGatewayFilter.Config> {

    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    private static final int DEFAULT_REQUESTS_PER_MINUTE = 60;
    private static final long CLEANUP_INTERVAL_MS = 60000; // 1 minute

    public RateLimitGatewayFilter() {
        super(Config.class);
        // Cleanup expired entries periodically
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(CLEANUP_INTERVAL_MS);
                    cleanupExpiredEntries();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    @Override
    public GatewayFilter apply(Config config) {
        int requestsPerMinute = config.getRequestsPerMinute() > 0 
            ? config.getRequestsPerMinute() 
            : DEFAULT_REQUESTS_PER_MINUTE;

        return (exchange, chain) -> {
            String clientId = getClientId(exchange);
            String key = exchange.getRequest().getURI().getPath() + ":" + clientId;
            
            RateLimitInfo info = rateLimitMap.computeIfAbsent(key, 
                k -> new RateLimitInfo(System.currentTimeMillis()));
            
            long currentTime = System.currentTimeMillis();
            
            // Reset if a minute has passed
            if (currentTime - info.getStartTime() > 60000) {
                info.reset(currentTime);
            }
            
            // Check rate limit
            if (info.getCount().get() >= requestsPerMinute) {
                return onRateLimitExceeded(exchange);
            }
            
            // Increment counter
            info.getCount().incrementAndGet();
            
            return chain.filter(exchange);
        };
    }

    private String getClientId(ServerWebExchange exchange) {
        // Try to get user ID from header first, then fall back to IP
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        
        // Fall back to IP address
        String ipAddress = exchange.getRequest().getRemoteAddress() != null
            ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
            : "unknown";
        return ipAddress;
    }

    private Mono<Void> onRateLimitExceeded(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("X-RateLimit-Limit", "60");
        response.getHeaders().add("X-RateLimit-Remaining", "0");
        response.getHeaders().add("Retry-After", "60");
        
        String message = "Rate limit exceeded. Please try again later.";
        DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private void cleanupExpiredEntries() {
        long currentTime = System.currentTimeMillis();
        rateLimitMap.entrySet().removeIf(entry -> 
            currentTime - entry.getValue().getStartTime() > 120000); // Remove entries older than 2 minutes
    }

    public static class Config {
        private int requestsPerMinute = DEFAULT_REQUESTS_PER_MINUTE;

        public int getRequestsPerMinute() {
            return requestsPerMinute;
        }

        public void setRequestsPerMinute(int requestsPerMinute) {
            this.requestsPerMinute = requestsPerMinute;
        }
    }

    private static class RateLimitInfo {
        private final AtomicInteger count = new AtomicInteger(0);
        private long startTime;

        public RateLimitInfo(long startTime) {
            this.startTime = startTime;
        }

        public AtomicInteger getCount() {
            return count;
        }

        public long getStartTime() {
            return startTime;
        }

        public void reset(long newStartTime) {
            this.startTime = newStartTime;
            this.count.set(0);
        }
    }
}

