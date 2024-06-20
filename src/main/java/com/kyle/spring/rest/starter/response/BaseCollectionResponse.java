package com.kyle.spring.rest.starter.response;

import com.kyle.spring.rest.starter.BaseResponse;

import java.util.List;

/**
 * @param <T>
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public class BaseCollectionResponse<T> extends BaseResponse {

    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
