package com.phiny.labs.progressservice.config;

import com.phiny.labs.common.feign.FeignAuthInterceptor;
import com.phiny.labs.common.feign.ServiceTokenGenerator;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
public class FeignConfig {

    @Value("${spring.application.name:progress-service}")
    private String serviceName;

    @Bean
    public ServiceTokenGenerator serviceTokenGenerator() {
        ServiceTokenGenerator generator = new ServiceTokenGenerator();
        generator.setServiceName(serviceName);
        return generator;
    }
    
    @Bean
    public RequestInterceptor feignAuthInterceptor() {
        return new FeignAuthInterceptor(serviceTokenGenerator());
    }
}

