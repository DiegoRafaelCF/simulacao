package com.hack.simulacao.api.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.hack.simulacao.domain.h2.Telemetria;
import com.hack.simulacao.infra.repository.h2.TelemetriaRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestTelemetryInterceptor implements HandlerInterceptor {
    
    private final TelemetriaRepository telemetriaRepository;

    public RequestTelemetryInterceptor(TelemetriaRepository telemetriaRepository) {
        this.telemetriaRepository = telemetriaRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if(request.getRequestURI().equals("/api/v1/telemetria") || request.getRequestURI().equals("/favicon.ico")) {
            return;
        }

        long start = (long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - start;

        Telemetria t = new Telemetria();

        t.setEndpoint(request.getRequestURI());
        t.setStatusCode(response.getStatus());
        t.setTempoMs(duration);
        telemetriaRepository.save(t);
    }
}
