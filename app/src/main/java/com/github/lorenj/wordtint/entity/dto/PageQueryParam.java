package com.github.lorenj.wordtint.entity.dto;


/**
 * @author sukidayo
 * @date 2023/8/27 21:57
 */

public class PageQueryParam {
    private Integer current;

    private Integer size;

    public PageQueryParam() {
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
