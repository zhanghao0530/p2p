package com.bjpowernode.p2p.model.vo;/**
 * ClassName:PaginationVo
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2020/3/14 21:58
 * @author:zh
 */

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：章昊
 * 2020/3/14
 */

public class PaginationVo<T> implements Serializable {
    private Long total;
    private List<T> dataList;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
