package com.kyle.spring.rest.starter;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public class BaseResponse {
    @ApiModelProperty(value = "是否成功", required = true)
    private Boolean success = true;

    @ApiModelProperty(value = "错误code")
    private String errCode;

    @ApiModelProperty(value = "错误描述")
    private String message;

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public BaseResponse error(String errCode, String errMsg) {
        setSuccess(false);
        setErrCode(errCode);
        setMessage(errMsg);
        return this;
    }

    public BaseResponse error(BaseEnum baseEnum) {
        setSuccess(false);
        setErrCode(baseEnum.getCode());
        setMessage(baseEnum.getMsg());
        return this;
    }

    public BaseResponse() {

    }

    public BaseResponse(BaseEnum baseEnum) {
        this.success = false;
        this.errCode = baseEnum.getCode();
        this.message = baseEnum.getMsg();
    }
}
