package com.kyle.spring.rest.starter.validator;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.List;

/**
 * 参数校验 只允许指定的值
 *
 * @author carroll
 * @Date 2017-07-25 18:06
 **/
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueIn.Validator.class)
@SuppressWarnings("javadoc")
public @interface ValueIn {
    String message() default "invalid value";

    boolean allowBlank() default true;

    String[] allowValues() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    public class Validator implements ConstraintValidator<ValueIn, Object> {
        boolean allowBlank;
        String[] allowValues;

        @Override
        public void initialize(ValueIn valueIn) {
            allowBlank = valueIn.allowBlank();
            allowValues = valueIn.allowValues();
        }


        @Override
        public boolean isValid(Object arg0, ConstraintValidatorContext arg1) {
            if (arg0 == null) {
                return allowBlank;
            }
            boolean result = false;
            if (arg0 instanceof List || arg0.getClass().isArray()) {
                boolean rt;
                for (Object arg : (Collection) arg0) {
                    rt = false;
                    for (String allow : allowValues) {
                        if (allow.equals(String.valueOf(arg))) {
                            rt = true;
                            break;
                        }
                    }
                    if (!rt) {
                        return false;
                    }
                }
                return true;
            } else {
                for (String allow : allowValues) {
                    if (allow.equals(String.valueOf(arg0))) {
                        result = true;
                        break;
                    }
                }
            }

            return result;
        }
    }
}
