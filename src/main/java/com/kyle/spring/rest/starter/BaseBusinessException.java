package com.kyle.spring.rest.starter;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public class BaseBusinessException extends Exception {
    /**
     * 错误code
     */
    private String returnErrCode;

    /**
     * 错误message
     */
    private String returnErrMsg;

    /**
     * 构造函数
     *
     * @param errCode
     * @param errMsg
     */
    public BaseBusinessException(String errCode, String errMsg) {
        super("errCode: ".concat(errCode).concat(",errMsg: ").concat(errMsg));
        returnErrCode = errCode;
        returnErrMsg = errMsg;
    }

    @Deprecated
    public BaseBusinessException() {
    }

    public String getReturnErrCode() {
        return returnErrCode;
    }

    public void setReturnErrCode(String returnErrCode) {
        this.returnErrCode = returnErrCode;
    }

    public String getReturnErrMsg() {
        return returnErrMsg;
    }

    public void setReturnErrMsg(String returnErrMsg) {
        this.returnErrMsg = returnErrMsg;
    }
}
