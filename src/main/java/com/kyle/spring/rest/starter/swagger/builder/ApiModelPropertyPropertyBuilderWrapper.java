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

import com.kyle.spring.rest.starter.swagger.annotations.ApiModelPropertyWrapper;
import com.google.common.base.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.Annotations;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import static com.kyle.spring.rest.starter.swagger.properties.ApiModelPropertiesWrapper.findApiModePropertyAnnotation;
import static com.kyle.spring.rest.starter.swagger.properties.ApiModelPropertiesWrapper.toAllowableValues;
import static com.kyle.spring.rest.starter.swagger.schema.ApiModelPropertiesWrapper.*;
import static springfox.documentation.schema.Annotations.findPropertyAnnotation;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class ApiModelPropertyPropertyBuilderWrapper implements ModelPropertyBuilderPlugin {
    @Override
    public void apply(ModelPropertyContext context) {
        Optional<ApiModelPropertyWrapper> annotation = Optional.absent();

        if (context.getAnnotatedElement().isPresent()) {
            annotation = annotation.or(findApiModePropertyAnnotation(context.getAnnotatedElement().get()));
        }
        if (context.getBeanPropertyDefinition().isPresent()) {
            java.util.Optional<ApiModelPropertyWrapper> propertyAnnotation = findPropertyAnnotation(
                    context.getBeanPropertyDefinition().get(), ApiModelPropertyWrapper.class);
            if (propertyAnnotation.isPresent()) {
                annotation = Optional.of(propertyAnnotation.get());
            }
        }
        if (annotation.isPresent()) {
            context.getBuilder()
                    .allowableValues(annotation.transform(toAllowableValues()).orNull())
                    .required(annotation.transform(toIsRequired()).or(false))
                    .readOnly(annotation.transform(toIsReadOnly()).or(false))
                    .description(annotation.transform(toDescription()).orNull())
                    .isHidden(annotation.transform(toHidden()).or(false))
                    .type(annotation.transform(toType(context.getResolver())).orNull())
                    .position(annotation.transform(toPosition()).or(0))
                    .example(annotation.transform(toExample()).orNull());
        }
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }
}