package com.spring.api_gateway.exceptions.zuul;

import org.springframework.web.client.RestClientException;

public class AllRequestTimeoutException extends RestClientException {
    public AllRequestTimeoutException(String msg) {
        super(msg);
    }
}
