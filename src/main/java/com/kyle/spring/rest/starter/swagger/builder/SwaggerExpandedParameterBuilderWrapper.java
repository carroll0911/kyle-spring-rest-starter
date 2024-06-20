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

package com.kyle.spring.rest.starter.swagger.builder;

import com.kyle.spring.rest.starter.BaseEnum;
import com.kyle.spring.rest.starter.swagger.annotations.ApiModelPropertyWrapper;
import com.kyle.spring.rest.starter.swagger.schema.ApiModelPropertiesWrapper;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import io.swagger.annotations.ApiParam;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.swagger.schema.ApiModelProperties;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kyle.spring.rest.starter.swagger.schema.ApiModelPropertiesWrapper.findApiModePropertyAnnotation;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Lists.transform;
import static springfox.documentation.swagger.annotations.Annotations.findApiParamAnnotation;

/**
 * @author carroll
 * @Date 2017-07-25 18:06 
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class SwaggerExpandedParameterBuilderWrapper implements ExpandedParameterBuilderPlugin {

    @Override
    public void apply(ParameterExpansionContext context) {
        Optional<ApiModelPropertyWrapper> apiModelPropertyWrapperOptional
                = findApiModePropertyAnnotation(context.getField().getRawMember());
        if (apiModelPropertyWrapperOptional.isPresent()) {
            try {
                fromApiModelProperty(context, apiModelPropertyWrapperOptional.get());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Optional<ApiParam> apiParamOptional = findApiParamAnnotation(context.getField().getRawMember());
        if (apiParamOptional.isPresent()) {
            fromApiParam(context, apiParamOptional.get());
        }
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }

    private void fromApiParam(ParameterExpansionContext context, ApiParam apiParam) {
        String allowableProperty = emptyToNull(apiParam.allowableValues());
        AllowableValues allowable = allowableValues(fromNullable(allowableProperty), context.getField().getRawMember(), null);
        context.getParameterBuilder()
                .description(apiParam.value())
                .defaultValue(apiParam.defaultValue())
                .required(apiParam.required())
                .allowMultiple(apiParam.allowMultiple())
                .allowableValues(allowable)
                .parameterAccess(apiParam.access())
                .hidden(apiParam.hidden())
                .build();
    }

    private void fromApiModelProperty(ParameterExpansionContext context, ApiModelPropertyWrapper apiModelPropertyWrapper) throws ClassNotFoundException {
        String allowableProperty = emptyToNull(apiModelPropertyWrapper.allowableValues());
        AllowableValues allowable = allowableValues(fromNullable(allowableProperty), context.getField().getRawMember(), apiModelPropertyWrapper);
        context.getParameterBuilder()
                .description(apiModelPropertyWrapper.value() + readFromEnumClass(apiModelPropertyWrapper))
                .required(apiModelPropertyWrapper.required())
                .allowableValues(allowable)
                .parameterAccess(apiModelPropertyWrapper.access())
                .hidden(apiModelPropertyWrapper.hidden())
                .build();
    }

    private AllowableValues allowableValues(final Optional<String> optionalAllowable, final Field field, ApiModelPropertyWrapper apiModelPropertyWrapper) {
        AllowableValues allowable = null;
        if (field.getType().isEnum()) {
            allowable = new AllowableListValues(getEnumValues(field.getType(), apiModelPropertyWrapper), "LIST");
        } else if (optionalAllowable.isPresent()) {
            allowable = ApiModelProperties.allowableValueFromString(optionalAllowable.get());
        }

        return allowable;
    }

    private List<String> getEnumValues(final Class<?> subject, ApiModelPropertyWrapper apiModelPropertyWrapper) {
        if (apiModelPropertyWrapper == null) {
            return transform(Arrays.asList(subject.getEnumConstants()), (Function<Object, String>) input -> input.toString());
        }
        Set<String> exclusions = Arrays.stream(apiModelPropertyWrapper.excludeEnumCode()).collect(Collectors.toSet());
        return Arrays.stream(subject.getEnumConstants()).
                map(element -> element.toString()).
                filter(element -> !exclusions.contains(element)).
                collect(Collectors.toList());
    }

    private String readFromEnumClass(ApiModelPropertyWrapper apiModelPropertyWrapper) throws ClassNotFoundException {
        Class<? extends BaseEnum> enumClass = apiModelPropertyWrapper.enumClass();
        Set<String> exclusionsEnumCode = Arrays.stream(apiModelPropertyWrapper.excludeEnumCode()).collect(Collectors.toSet());
        if (enumClass == BaseEnum.class) {
            return "";
        }
        return ApiModelPropertiesWrapper.getValueFromEnum(enumClass, exclusionsEnumCode);
    }
}
