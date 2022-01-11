package com.spring.api_gateway.base.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.api_gateway.base.models.BaseResponse;
import com.spring.api_gateway.base.models.FieldValidationError;
import com.spring.api_gateway.constants.ResponseValue;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRESTController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public BaseResponse onDtoValidationError(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        List<ObjectError> errors = result.getAllErrors();
        return handleFieldErrors(errors);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public BaseResponse onBindValueDtoError(BindException e) {
        BindingResult result = e.getBindingResult();
        List<ObjectError> errors = result.getAllErrors();
        return handleFieldErrors(errors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public BaseResponse onRequestParamMissingError(MissingServletRequestParameterException e) {
        return new BaseResponse<>(ResponseValue.INVALID_OR_MISSING_REQUEST_PARAMETERS);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public BaseResponse onRequestBodyError(HttpMessageNotReadableException e) {
        return new BaseResponse<>(ResponseValue.INVALID_OR_MISSING_REQUEST_BODY);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public BaseResponse onInternalServerError(Exception e) {
        e.printStackTrace();
        return new BaseResponse(ResponseValue.UNEXPECTED_ERROR_OCCURRED);
    }

    private BaseResponse handleFieldErrors(List<ObjectError> fieldErrors) {
        List<FieldValidationError> invalidFieldDtos = new ArrayList<>();
        for (ObjectError objectError : fieldErrors) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                String fieldName = fieldError.getField();
                ConstraintViolationImpl constraintViolation = fieldError.unwrap(ConstraintViolationImpl.class);
                try {
                    Field invalidField = constraintViolation.getRootBeanClass().getDeclaredField(fieldError.getField());
                    JsonProperty jsonProperty = invalidField.getAnnotation(JsonProperty.class);
                    if (jsonProperty != null) {
                        fieldName = jsonProperty.value();
                    }
                } catch (NoSuchFieldException e) {
                    continue;
                }
                String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
                invalidFieldDtos.add(new FieldValidationError(fieldName, localizedErrorMessage));
            } else {
                invalidFieldDtos.add(new FieldValidationError(objectError.getObjectName(), objectError.getDefaultMessage()));
            }
        }
        return new BaseResponse<>(ResponseValue.INVALID_FIELDS, invalidFieldDtos);
    }

    private String resolveLocalizedErrorMessage(FieldError fieldError) {
        return fieldError.getDefaultMessage();
    }
}
