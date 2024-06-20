package com.kyle.spring.rest.starter.swagger.properties;

import com.kyle.spring.rest.starter.swagger.annotations.ApiModelPropertyWrapper;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import org.springframework.core.annotation.AnnotationUtils;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.util.StringUtils.hasText;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public class ApiModelPropertiesWrapper {
    private static final String RANGE = "range[";
    private static final String COMMA = ",";

    private ApiModelPropertiesWrapper() {
        throw new UnsupportedOperationException();
    }

    public static Function<ApiModelPropertyWrapper, AllowableValues> toAllowableValues() {
        return annotation -> allowableValueFromString(annotation.allowableValues());
    }

    public static AllowableValues allowableValueFromString(String allowableValueString) {
        AllowableValues allowableValues = new AllowableListValues(newArrayList(), "LIST");
        String trimmed = allowableValueString.trim();
        if (trimmed.startsWith(RANGE)) {
            trimmed = trimmed.replaceAll("range\\[", "").replaceAll("]", "");
            Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(trimmed);
            List<String> ranges = newArrayList(split);
            allowableValues = new AllowableRangeValues(ranges.get(0), ranges.get(1));
        } else if (trimmed.contains(COMMA)) {
            Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(trimmed);
            allowableValues = new AllowableListValues(newArrayList(split), "LIST");
        } else if (hasText(trimmed)) {
            List<String> singleVal = Collections.singletonList(trimmed);
            allowableValues = new AllowableListValues(singleVal, "LIST");
        }
        return allowableValues;
    }

    public static Optional<ApiModelPropertyWrapper> findApiModePropertyAnnotation(AnnotatedElement annotated) {
        return Optional.fromNullable(AnnotationUtils.getAnnotation(annotated, ApiModelPropertyWrapper.class));
    }
}
