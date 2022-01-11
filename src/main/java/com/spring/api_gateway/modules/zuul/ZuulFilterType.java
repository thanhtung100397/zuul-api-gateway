package com.spring.api_gateway.modules.zuul;

public enum ZuulFilterType {
    PRE("pre"),
    ROUTE("route"),
    POST("post"),
    ERROR("error"),
    CUSTOM("custom");

    private String value;

    ZuulFilterType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
