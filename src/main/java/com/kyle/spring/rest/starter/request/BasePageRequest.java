package com.kyle.spring.rest.starter.request;

import com.kyle.spring.rest.starter.BaseRequest;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public class BasePageRequest extends BaseRequest {
    @ApiModelProperty(value = "当前页")
    private Integer curPage = 1;

    @ApiModelProperty("页码")
    private Integer pageSize = 10;

    public Integer getCurPage() {
        return curPage;
    }

    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }
}
