package com.spring.api_gateway.constants;

import org.springframework.http.HttpStatus;

public enum ResponseValue {
    //200x
    SUCCESS(HttpStatus.OK, "thành công"),

    //400x
    INVALID_FIELDS(HttpStatus.BAD_REQUEST, 4001, "trường không hợp lệ"),
    INVALID_OR_MISSING_REQUEST_BODY(HttpStatus.BAD_REQUEST, 4002, "request body không hợp lệ"),
    INVALID_OR_MISSING_REQUEST_PARAMETERS(HttpStatus.BAD_REQUEST, 4003, "request param thiếu hoặc không hợp lệ"),

    // 401x
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, 4011, "truy cập yêu cầu xác thực người dùng"),
    USER_BANNED(HttpStatus.UNAUTHORIZED, 4016, "tài khoản đã bị vô hiệu hóa"),

    //403x

    //404x
    ROUTE_ID_NOT_FOUND(HttpStatus.NOT_FOUND, 4042, "không tìm thấy đường dẫn"),
    PATH_NOT_FOUND(HttpStatus.NOT_FOUND, 4042, "không tìm thấy đường dẫn của request"),

    //408x
    BUSINESS_SERVICE_CONNECTION_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, 4082, "kết nối dịch vụ timeout"),

    //500x
    UNEXPECTED_ERROR_OCCURRED(HttpStatus.INTERNAL_SERVER_ERROR, 500, "lỗi hệ thống");

    private HttpStatus httpStatus;
    private int specialCode;
    private String message;

    ResponseValue(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.specialCode = httpStatus.value();
        this.message = message;
    }

    ResponseValue(HttpStatus httpStatus, int specialCode, String message) {
        this.httpStatus = httpStatus;
        this.specialCode = specialCode;
        this.message = message;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public int specialCode() {
        return specialCode;
    }

    public String message() {
        return message;
    }
}
