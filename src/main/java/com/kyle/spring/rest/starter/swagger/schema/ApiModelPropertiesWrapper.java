/*
*
*  Copyright 2015 the original author or authors.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*
*
*/

package com.kyle.spring.rest.starter.swagger.schema;

import com.kyle.spring.rest.starter.BaseEnum;
import com.kyle.spring.rest.starter.swagger.annotations.ApiModelPropertyWrapper;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public final class ApiModelPropertiesWrapper {
    private ApiModelPropertiesWrapper() {
        throw new UnsupportedOperationException();
    }

    public static Function<ApiModelPropertyWrapper, Boolean> toIsRequired() {
        return annotation -> annotation.required();
    }

    public static Function<ApiModelPropertyWrapper, Integer> toPosition() {
        return annotation -> annotation.position();
    }

    public static Function<ApiModelPropertyWrapper, Boolean> toIsReadOnly() {
        return annotation -> annotation.readOnly();
    }

    public static Function<ApiModelPropertyWrapper, String> toDescription() {
        return annotation -> {
            String description = "";
            if (!Strings.isNullOrEmpty(annotation.value())) {
                description = annotation.value();
            } else if (!Strings.isNullOrEmpty(annotation.notes())) {
                description = annotation.notes();
            }

            Class<? extends BaseEnum> enumClass = annotation.enumClass();
            if (enumClass.isEnum()) {
                try {
                    description += " " + constructEnumDescription(enumClass, annotation);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return description;
        };
    }

    private static String constructEnumDescription(Class<? extends BaseEnum> enumClass, ApiModelPropertyWrapper annotation) throws ClassNotFoundException {
        Set<String> exclusionsEnumCode = Arrays.stream(annotation.excludeEnumCode()).collect(Collectors.toSet());
        if (enumClass == BaseEnum.class) {
            return "";
        }
        return getValueFromEnum(enumClass, exclusionsEnumCode);
    }

    public static String getValueFromEnum(Class<? extends BaseEnum> enumClass, Set<String> exclusionsEnumCode) throws ClassNotFoundException {
        StringBuilder stringBuilder = new StringBuilder(" 示例：[");
        for (Object object : (Class.forName(enumClass.getName()).getEnumConstants())) {
            BaseEnum baseEnum = (BaseEnum) object;
            if (exclusionsEnumCode.contains(baseEnum.getCode())) {
                continue;
            }
            stringBuilder.append(baseEnum.getCode());
            stringBuilder.append(":");
            stringBuilder.append(baseEnum.getMsg());
            stringBuilder.append(";");
        }
        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    public static Function<ApiModelPropertyWrapper, ResolvedType> toType(final TypeResolver resolver) {
        return annotation -> {
            try {
                return resolver.resolve(Class.forName(annotation.dataType()));
            } catch (ClassNotFoundException e) {
                return resolver.resolve(Object.class);
            }
        };
    }

    public static Optional<ApiModelPropertyWrapper> findApiModePropertyAnnotation(AnnotatedElement annotated) {
        return Optional.fromNullable(AnnotationUtils.getAnnotation(annotated, ApiModelPropertyWrapper.class));
    }

    public static Function<ApiModelPropertyWrapper, Boolean> toHidden() {
        return annotation -> annotation.hidden();
    }

    public static Function<ApiModelPropertyWrapper, String> toExample() {
        return annotation -> {
            String example = "";
            if (!Strings.isNullOrEmpty(annotation.example())) {
                example = annotation.example();
            }
            return example;
        };
    }
}
