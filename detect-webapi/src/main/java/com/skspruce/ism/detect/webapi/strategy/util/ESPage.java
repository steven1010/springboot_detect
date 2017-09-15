package com.skspruce.ism.detect.webapi.strategy.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;

public class ESPage<T>{
    public int getTotalPages() {
        return 0;
    }

    public long getTotalElements() {
        return 0;
    }

    public int getNumber() {
        return 0;
    }

    public int getSize() {
        return 0;
    }

    public int getNumberOfElements() {
        return 0;
    }

    public List<T> getContent() {
        return null;
    }

    public boolean hasContent() {
        return false;
    }

    public Sort getSort() {
        return null;
    }

    public boolean isFirst() {
        return false;
    }

    public boolean isLast() {
        return false;
    }

    public boolean hasNext() {
        return false;
    }

    public boolean hasPrevious() {
        return false;
    }

    public Pageable nextPageable() {
        return null;
    }

    public Pageable previousPageable() {
        return null;
    }


    public Iterator<T> iterator() {
        return null;
    }
}
