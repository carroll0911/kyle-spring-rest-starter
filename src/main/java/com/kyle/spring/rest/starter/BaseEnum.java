package com.kyle.spring.rest.starter;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public interface BaseEnum {
    /**
     * 获取错误码
     *
     * @return
     */
    String getCode();

    /**
     * 获取错误信息
     *
     * @return
     */
    String getMsg();
}
