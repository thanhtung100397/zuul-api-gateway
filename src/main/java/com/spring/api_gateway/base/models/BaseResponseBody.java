package com.spring.api_gateway.base.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.api_gateway.constants.ResponseValue;
import com.spring.api_gateway.constants.StringConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class BaseResponseBody<T> {
    @ApiModelProperty(notes = "[http status code][special code]")
    @JsonProperty(StringConstants.CODE)
    private int code;
    @ApiModelProperty(notes = "thông điệp của response", position = 1)
    @JsonProperty(StringConstants.MESSAGE)
    private String msg;
    @ApiModelProperty(notes = "dữ liệu trả về của response", position = 2)
    @JsonProperty(StringConstants.DATA)
    private T data;

    public BaseResponseBody() {
    }

    public BaseResponseBody(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BaseResponseBody(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResponseBody(ResponseValue responseValue, T data) {
        this.code = responseValue.specialCode();
        this.msg = responseValue.message();
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
