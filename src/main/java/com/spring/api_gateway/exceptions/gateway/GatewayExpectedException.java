package com.spring.api_gateway.exceptions.gateway;

import com.netflix.zuul.exception.ZuulException;
import com.spring.api_gateway.modules.gateway.models.dtos.ResponseContent;

public class GatewayExpectedException extends ZuulException {
    private ResponseContent responseContent = ResponseContent.MESSAGE_ONLY;
    private int responseCode;

    public GatewayExpectedException(ResponseContent responseContent, int nStatusCode, String data) {
        super(data, nStatusCode, "");
        this.responseContent = responseContent;
    }

    public GatewayExpectedException(ResponseContent responseContent, int nStatusCode, int responseCode, String data) {
        super(data, nStatusCode, "");
        this.responseCode = responseCode;
        this.responseContent = responseContent;
    }

    public ResponseContent getResponseContent() {
        return responseContent;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
