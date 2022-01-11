package com.spring.api_gateway.swagger.gateway;

import com.spring.api_gateway.base.models.BaseResponseBody;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import java.util.Map;

public class ZuulRoutesSwagger extends BaseResponseBody<Map<String, ZuulProperties.ZuulRoute>> {
}
