package com.kyle.spring.rest.starter.swagger;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.kyle.spring.rest.starter.config.SwaggerConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
@ConditionalOnProperty(prefix = "swagger", value = {"enable"}, havingValue = "true", matchIfMissing = true)
@EnableSwagger2WebMvc
@Component
@EnableKnife4j
public class SwaggerIniter implements ApplicationContextAware {

    private static final String APPLICATION_VERSION_KEY = "appVersion";

    @Autowired
    private Environment env;

    @Autowired
    private SwaggerConfig swaggerConfig;

    private ApiInfo demoApiInfo() {
        String appVersion = env.getProperty(APPLICATION_VERSION_KEY);
        if (StringUtils.isEmpty(appVersion)) {
            appVersion = swaggerConfig.getVersion();
        }

        ApiInfo apiInfo = new ApiInfo(
                //大标题
                swaggerConfig.getTitle(),
                //小标题
                swaggerConfig.getDescription(),
                //版本
                appVersion,
                swaggerConfig.getTermsOfServiceUrl(),
                new Contact(swaggerConfig.getContactName(), swaggerConfig.getContactURL(), swaggerConfig.getContactEmail()),
                //链接显示文字
                swaggerConfig.getLicense(),
                //网站链接
                swaggerConfig.getLicenseUrl(),
                Arrays.asList(new StringVendorExtension(swaggerConfig.getContactName(), swaggerConfig.getContactURL()))
        );
        return apiInfo;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ConfigurableApplicationContext appContext = (ConfigurableApplicationContext) applicationContext;
        //Bean的实例工厂
        DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) appContext.getBeanFactory();
        swaggerConfig.getGroups().forEach((k, v) -> {
            //Bean构建  BeanService.class 要创建的Bean的Class对象
            BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(Docket.class);
            bdb.addConstructorArgValue(DocumentationType.SWAGGER_2);
            dbf.registerBeanDefinition("swagger" + k, bdb.getBeanDefinition());
            Docket docket = (Docket) appContext.getBean("swagger" + k);
            List<Parameter> pars = new ArrayList<Parameter>();
            if (!CollectionUtils.isEmpty(swaggerConfig.getHeaders())) {
                swaggerConfig.getHeaders().forEach((hk, hv) -> {
                    ParameterBuilder parBuild = new ParameterBuilder();
                    parBuild.name(hk).description(hv)
                            .modelRef(new ModelRef("string")).parameterType("header")
                            .required(false).build();
                    pars.add(parBuild.build());
                });
            }

            //根据每个方法名也知道当前方法在设置什么参数
            docket.groupName(k)
                    .apiInfo(demoApiInfo())
                    // 选择那些路径和api会生成document
                    .select()
                    // 对所有路径进行监控
                    .paths(PathSelectors.any())
                    .apis(RequestHandlerSelectors.basePackage(v))
                    .build().globalOperationParameters(pars);

        });
    }
}

