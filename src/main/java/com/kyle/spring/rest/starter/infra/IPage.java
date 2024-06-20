package com.kyle.spring.rest.starter.infra;

/**
 * @author carroll
 * @Date 2017-07-25 18:06
 */
public interface IPage {
    Integer getCurPage();

    void setCurPage(Integer curPage);

    Integer getPageSize();

    void setPageSize(Integer pageSize);
}
