package com.spring.api_gateway.configs.base;

import com.spring.api_gateway.customs.SaveStateRibbonLoadBalancerClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadBalancerConfig {
    @Bean
    @ConditionalOnMissingBean(LoadBalancerClient.class)
    public LoadBalancerClient loadBalancerClient(SpringClientFactory springClientFactory) {
        return new SaveStateRibbonLoadBalancerClient(springClientFactory);
    }
}
