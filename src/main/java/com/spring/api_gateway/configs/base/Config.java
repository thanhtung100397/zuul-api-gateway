package com.spring.api_gateway.configs.base;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class Config {
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver() {
            @Override
            public boolean isMultipart(HttpServletRequest request) {
                String method = request.getMethod();
                if (!HttpMethod.POST.name().equals(method) && !HttpMethod.PUT.name().equals(method)) {
                    return false;
                }
                String contentType = request.getContentType();
                return (contentType != null &&contentType.toLowerCase().startsWith("multipart/"));
            }
        };
    }
}