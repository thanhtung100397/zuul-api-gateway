package com.spring.api_gateway.configs.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Value("${ribbon.ConnectTimeout}")
    private int connectTimeout;
    @Value("${ribbon.ReadTimeout}")
    private int readTimeout;

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build(RestTemplate.class);
    }

    @Bean
    public RestTemplate httpRequest(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }
}
