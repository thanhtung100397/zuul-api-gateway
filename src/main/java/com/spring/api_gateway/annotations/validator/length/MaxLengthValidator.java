package com.spring.api_gateway.annotations.validator.length;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MaxLengthValidator implements ConstraintValidator<MaxLength, String> {
    private int maxCharacter;

    @Override
    public void initialize(MaxLength constraintAnnotation) {
        this.maxCharacter = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String inputValue, ConstraintValidatorContext context) {
        return inputValue == null || inputValue.length() <= maxCharacter;
    }
}
