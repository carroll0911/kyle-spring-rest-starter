package com.kyle.spring.rest.starter.exception;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public class ParamErrorException extends Exception {

    private String[] paramNames;

    public ParamErrorException() {
    }

    public ParamErrorException(String[] paramNames) {
        this.paramNames = paramNames;
    }

    public String[] getParamNames() {
        return paramNames;
    }

    public void setParamNames(String[] paramNames) {
        this.paramNames = paramNames;
    }
}
