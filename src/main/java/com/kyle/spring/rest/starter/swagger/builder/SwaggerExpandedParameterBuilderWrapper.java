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
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.swagger.readers.parameter.Examples;
import springfox.documentation.swagger.schema.ApiModelProperties;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kyle.spring.rest.starter.swagger.schema.ApiModelPropertiesWrapper.findApiModePropertyAnnotation;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Lists.transform;
import static springfox.documentation.swagger.annotations.Annotations.findApiParamAnnotation;
import static springfox.documentation.swagger.common.SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER;

/**
 * @author carroll
 * @Date 2017-07-25 18:06 
 */
@Component
@Order(SWAGGER_PLUGIN_ORDER)
public class SwaggerExpandedParameterBuilderWrapper implements ExpandedParameterBuilderPlugin {

    private final DescriptionResolver descriptions;
    private final EnumTypeDeterminer enumTypeDeterminer;

    @Autowired
    public SwaggerExpandedParameterBuilderWrapper(
            DescriptionResolver descriptions,
            EnumTypeDeterminer enumTypeDeterminer) {
        this.descriptions = descriptions;
        this.enumTypeDeterminer = enumTypeDeterminer;
    }

    @Override
    public void apply(ParameterExpansionContext context) {
        java.util.Optional<ApiModelPropertyWrapper> apiModelPropertyOptional = context.findAnnotation(ApiModelPropertyWrapper.class);
        apiModelPropertyOptional.ifPresent(apiModelProperty -> fromApiModelProperty(context, apiModelProperty));
        java.util.Optional<ApiParam> apiParamOptional = context.findAnnotation(ApiParam.class);
        apiParamOptional.ifPresent(apiParam -> fromApiParam(context, apiParam));
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }

    private void fromApiParam(ParameterExpansionContext context, ApiParam apiParam) {
        String allowableProperty = Strings.trimToNull(apiParam.allowableValues());
        AllowableValues allowable = allowableValues(
                Optional.fromNullable(allowableProperty),
                context.getFieldType().getErasedType());

        maybeSetParameterName(context, apiParam.name())
                .description(descriptions.resolve(apiParam.value()))
                .defaultValue(apiParam.defaultValue())
                .required(apiParam.required())
                .allowMultiple(apiParam.allowMultiple())
                .allowableValues(allowable)
                .parameterAccess(apiParam.access())
                .hidden(apiParam.hidden())
                .scalarExample(apiParam.example())
                .complexExamples(Examples.examples(apiParam.examples()))
                .order(SWAGGER_PLUGIN_ORDER)
                .build();
    }

    private void fromApiModelProperty(ParameterExpansionContext context, ApiModelPropertyWrapper apiModelProperty) {
        String allowableProperty = Strings.trimToNull(apiModelProperty.allowableValues());
        AllowableValues allowable = allowableValues(
                Optional.fromNullable(allowableProperty),
                context.getFieldType().getErasedType());

        maybeSetParameterName(context, apiModelProperty.name())
                .description(descriptions.resolve(apiModelProperty.value()))
                .required(apiModelProperty.required())
                .allowableValues(allowable)
                .parameterAccess(apiModelProperty.access())
                .hidden(apiModelProperty.hidden())
                .scalarExample(apiModelProperty.example())
                .order(apiModelProperty.position()) //源码这里是: SWAGGER_PLUGIN_ORDER，需要修正
                .build();
    }

    private ParameterBuilder maybeSetParameterName(ParameterExpansionContext context, String parameterName) {
        if (!Strings.isBlank(parameterName)) {
            context.getParameterBuilder().name(parameterName);
        }
        return context.getParameterBuilder();
    }

    private AllowableValues allowableValues(final Optional<String> optionalAllowable, Class<?> fieldType) {

        AllowableValues allowable = null;
        if (enumTypeDeterminer.isEnum(fieldType)) {
            allowable = new AllowableListValues(getEnumValues(fieldType), "LIST");
        } else if (optionalAllowable.isPresent()) {
            allowable = ApiModelProperties.allowableValueFromString(optionalAllowable.get());
        }
        return allowable;
    }

    private List<String> getEnumValues(final Class<?> subject) {
        List<?> list = Arrays.asList(subject.getEnumConstants());
        List<String> res = new LinkedList<>();
        for(Object a: list){
            res.add(a.toString());
        }
        return res;
    }
}
