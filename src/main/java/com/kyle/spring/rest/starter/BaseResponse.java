package com.kyle.spring.rest.starter;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public class BaseResponse<T> {
    @ApiModelProperty(value = "是否成功", required = true)
    private Boolean success = true;

    @ApiModelProperty(value = "错误code")
    private String errCode;

    @ApiModelProperty(value = "错误描述")
    private String message;

    @ApiModelProperty("返回数据")
    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public static <T> BaseResponse<T> success(T data){
        BaseResponse<T> response = new BaseResponse<>();
        response.setData(data);
        return response;
    }

    public static BaseResponse error(BaseEnum baseEnum){
        BaseResponse response = new BaseResponse<>();
        response.setSuccess(false);
        response.setErrCode(baseEnum.getCode());
        response.setMessage(baseEnum.getMsg());
        return response;
    }

    public static BaseResponse error(String errCode, String errMsg){
        BaseResponse response = new BaseResponse<>();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setMessage(errMsg);
        return response;
    }

    public void applyError(String errCode, String errMsg){
        this.setSuccess(false);
        this.setErrCode(errCode);
        this.setMessage(errMsg);
    }

    public void applyError(BaseEnum baseEnum){
        this.setSuccess(false);
        this.setErrCode(baseEnum.getCode());
        this.setMessage(baseEnum.getMsg());
    }

    public BaseResponse() {

    }

    public BaseResponse(BaseEnum baseEnum) {
        this.success = false;
        this.errCode = baseEnum.getCode();
        this.message = baseEnum.getMsg();
    }
}
