package com.kyle.spring.rest.starter.response;

import com.kyle.spring.rest.starter.BaseEnum;
import com.kyle.spring.rest.starter.BaseResponse;
import org.springframework.beans.BeanUtils;

import java.util.function.Function;

/**
 * @author: carroll.he
 * @date 2022/4/13 
 */

public class DataResponse<T> extends BaseResponse {

    private T data;

    public DataResponse(final T data) {
        this.data = data;
    }

    public DataResponse() {
    }

    public static <T> DataResponse<T> success(T data) {
        DataResponse<T> response = new DataResponse<>();
        response.setData(data);
        return response;
    }

    public static <T> DataResponse<T> success() {
        return success(null);
    }

    public static <T> DataResponse<T> fail(T data, BaseEnum baseEnum) {
        DataResponse<T> response = new DataResponse<>();
        response.setData(data);
        response.error(baseEnum);
        return response;
    }

    public static <T> DataResponse<T> fail(BaseEnum baseEnum) {
        return fail(null, baseEnum);
    }

    public static <T> DataResponse<T> fail(String errCode, String errMsg) {
        DataResponse<T> response = new DataResponse<>();
        response.error(errCode, errMsg);
        response.setData(null);
        return response;
    }

    public <R> DataResponse<R> convert(Function<T, R> function) {
        DataResponse<R> r = ((DataResponse<R>) this);
        if (this.getData() != null) {
            r.setData(function.apply(this.getData()));
        } else {
            r.setData(null);
        }
        return r;
    }

    public <R> DataResponse<R> convert(Class<R> rClass) {
        return convert(o -> {
            R rs = BeanUtils.instantiateClass(rClass);
            BeanUtils.copyProperties(this.getData(), rs);
            return rs;
        });
    }

    @Override
    public T getData() {
        return data;
    }
}