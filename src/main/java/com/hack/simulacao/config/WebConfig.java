package com.hack.simulacao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.hack.simulacao.api.interceptor.RequestTelemetryInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final RequestTelemetryInterceptor interceptor;

    public WebConfig(RequestTelemetryInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }
}
