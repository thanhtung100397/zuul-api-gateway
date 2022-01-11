package com.spring.api_gateway.modules.gateway.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class BusinessRouteDto {
    @JsonProperty("business_route")
    @NotEmpty
    private String businessRoute;

    public BusinessRouteDto(String businessRoute) {
        this.businessRoute = businessRoute;
    }

    public BusinessRouteDto() {
    }

    public String getBusinessRoute() {
        return businessRoute;
    }

    public void setBusinessRoute(String businessRoute) {
        this.businessRoute = businessRoute;
    }
}
