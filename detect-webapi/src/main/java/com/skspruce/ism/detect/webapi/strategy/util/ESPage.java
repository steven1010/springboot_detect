package com.skspruce.ism.detect.webapi.strategy.util;

import java.util.List;

/**
 * PAGE对象
 *
 * @param <T>
 */
public class ESPage<T> {

    private List<T> content;

    private Long totalPage;

    private Long totalElements;

    private boolean last = false;

    private Long number;

    private Integer size;

    private boolean first = false;

    private List<ESSortInfo> sort;

    private Integer numberOfElements;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public List<ESSortInfo> getSort() {
        return sort;
    }

    public void setSort(List<ESSortInfo> sort) {
        this.sort = sort;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
}

