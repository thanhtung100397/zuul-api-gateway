package com.spring.api_gateway.exceptions.oauth2;

import com.spring.api_gateway.constants.ResponseValue;

public class AuthorizationException extends Exception {
    private ResponseValue responseValue;

    public AuthorizationException(ResponseValue responseValue) {
        super(responseValue.message());
        this.responseValue = responseValue;
    }

    public ResponseValue getResponseValue() {
        return responseValue;
    }
}
