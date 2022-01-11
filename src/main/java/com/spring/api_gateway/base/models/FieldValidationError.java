package com.spring.api_gateway.base.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class FieldValidationError {
    @ApiModelProperty(notes = "tên trường bị lỗi")
    private String target;
    @ApiModelProperty(notes = "điều kiện yêu cầu", position = 1)
    private String required;

    public FieldValidationError() {
    }

    public FieldValidationError(String target, String required) {
        this.target = target;
        this.required = required;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }
}
