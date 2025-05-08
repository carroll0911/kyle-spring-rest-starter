package com.kyle.spring.rest.starter.config;

import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义tomcat配置
 * @author carroll
 * @Date 2017-07-25 18:06
 */
@Configuration
@ConditionalOnClass(TomcatServletWebServerFactory.class)
public class TomcatConfig {

    @Value("${server.connection-timeout:0}")
    private int connectionTimeOut;
    @Value("${server.tomcat.max-threads:0}")
    private int maxThreads;
    @Value("${server.tomcat.max-connections:0}")
    private int maxConnections;
    @Value("${server.tomcat.keep-alive-timeout:0}")
    private int keepAliveTimeout;

    private static final Logger logger = LoggerFactory.getLogger(TomcatConfig.class);

    @Bean
    public TomcatServletWebServerFactory createEmbeddedServletContainerFactory() {
        logger.info("--------------- init tomcat config ----------------");
        TomcatServletWebServerFactory tomcatFactory = new TomcatServletWebServerFactory();
        tomcatFactory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
            //设置最大连接数
            if (maxConnections > 0) {
                protocol.setMaxConnections(maxConnections);
            }
            //设置最大线程数
            if (maxThreads > 0) {
                protocol.setMaxThreads(maxThreads);
            }
            if (maxConnections > 0) {
                protocol.setConnectionTimeout(connectionTimeOut);
            }
            if (keepAliveTimeout > 0) {
                protocol.setKeepAliveTimeout(keepAliveTimeout);
            }
        });
        return tomcatFactory;
    }
}