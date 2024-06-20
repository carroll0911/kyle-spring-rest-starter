package com.kyle.spring.rest.starter.infra;

import javax.servlet.http.HttpServletRequest;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public interface ProcessException {
    void process(HttpServletRequest request, Object handler, Throwable e);
}
