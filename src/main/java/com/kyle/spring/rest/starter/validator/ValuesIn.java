package com.kyle.spring.rest.starter.validator;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValuesIn.Validator.class)
public @interface ValuesIn {
    Logger logger = LoggerFactory.getLogger(ValuesIn.class);

    String message() default "invalid value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();

    String[] exclusions() default "";

    class Validator implements ConstraintValidator<ValuesIn, Object> {
        private Class<? extends Enum<?>> enumClass;
        private List<String> exclusions;

        @Override
        public void initialize(ValuesIn enumValue) {
            enumClass = enumValue.enumClass();
            exclusions = Arrays.stream(enumValue.exclusions()).collect(Collectors.toList());
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
            if (value == null) {
                return true;
            }

            if (enumClass == null) {
                return true;
            }

            try {
                Set<String> enumConstants = Arrays.stream(Class.forName(((ConstraintValidatorContextImpl) constraintValidatorContext).
                        getConstraintDescriptor().
                        getAttributes().
                        get("enumClass").
                        toString().
                        substring(6)).getEnumConstants()).map(element -> ((Enum) element).name()).collect(Collectors.toSet());

                enumConstants.removeAll(exclusions);

                return enumConstants.contains(value.toString());
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage());
                return false;
            }
        }
    }
}
