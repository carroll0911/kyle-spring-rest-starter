package com.kyle.spring.rest.starter.response;

import com.kyle.spring.rest.starter.BaseResponse;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public class BasePageResponse<T> extends BaseResponse {
    @ApiModelProperty(value = "当前页，默认1")
    private int curPage;

    @ApiModelProperty(value = "每页多少行，默认1")
    private int pageSize;

    @ApiModelProperty(value = "是否存在下一页")
    private boolean hasNext;

    @ApiModelProperty(value = "是否存在上一页")
    private boolean hasPro;

    @ApiModelProperty(value = "总页数")
    private long totalPage;

    @ApiModelProperty(value = "总行数")
    private long totalRows;

    @ApiModelProperty(value = "数据")
    private List<T> data;

    public BasePageResponse(){}

    public BasePageResponse(int pageSize, int curPage, long totalRows) {
        this.totalRows = totalRows;
        this.pageSize = pageSize > 0 ? pageSize : 1;
        this.curPage = curPage > 0 ? curPage : 1;
        this.totalPage = totalRows % pageSize == 0 ? totalRows / pageSize : totalRows / pageSize + 1;
        this.hasNext = curPage + 1 <= this.totalPage ? true : false;
        this.hasPro = curPage == 1 ? false : true;
    }

    @Override
    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPro() {
        return hasPro;
    }

    public void setHasPro(boolean hasPro) {
        this.hasPro = hasPro;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(long totalRows) {
        this.totalRows = totalRows;
    }
}
