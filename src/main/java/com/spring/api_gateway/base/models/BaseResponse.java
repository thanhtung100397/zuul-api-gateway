package com.spring.api_gateway.base.models;

import com.spring.api_gateway.constants.ResponseValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseResponse<T> extends ResponseEntity<BaseResponseBody<T>> {
    public BaseResponse(ResponseValue response) {
        super(new BaseResponseBody<>(response.specialCode(), response.message(), null), response.httpStatus());
    }

    public BaseResponse(ResponseValue response, T data) {
        super(new BaseResponseBody<>(response.specialCode(), response.message(), data), response.httpStatus());
    }

    public BaseResponse(HttpStatus status, BaseResponseBody<T> body) {
        super(body, status);
    }
}
