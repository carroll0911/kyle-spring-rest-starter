package com.kyle.spring.rest.starter;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public class BaseException extends RuntimeException {
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
     * @param errCode
     * @param errMsg
     */
    public BaseException(String errCode, String errMsg) {
        super("errCode: ".concat(errCode).concat(",errMsg: ").concat(errMsg));
        returnErrCode = errCode;
        returnErrMsg = errMsg;
    }

    /**
     * 构造函数
     *
     * @param baseEnum
     */
    public BaseException(BaseEnum baseEnum) {
        super("errCode: ".concat(baseEnum.getCode()).concat(",errMsg: ").concat(baseEnum.getMsg()));
        returnErrCode = baseEnum.getCode();
        returnErrMsg = baseEnum.getMsg();
    }

    @Deprecated
    public BaseException(){}

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
