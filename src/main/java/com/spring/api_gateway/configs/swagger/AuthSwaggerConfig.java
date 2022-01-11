package com.spring.api_gateway.configs.swagger;

import com.spring.api_gateway.components.swagger.CustomSwaggerOperationAuthReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger.readers.operation.OperationAuthReader;

@Configuration
public class AuthSwaggerConfig {
    @Bean
    public OperationAuthReader operationAuthReader() {
        return new CustomSwaggerOperationAuthReader();
    }
}
