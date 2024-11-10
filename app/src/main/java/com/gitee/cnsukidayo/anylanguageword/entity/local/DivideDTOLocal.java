package com.gitee.cnsukidayo.anylanguageword.entity.local;

import java.io.Serializable;
import java.util.List;

/**
 * @author cnsukidayo
 * @date 2024/7/16 23:05
 */
public class DivideDTOLocal implements Serializable {
    private String name;
    private Long order;
    private List<Long> wordIdList;

    public DivideDTOLocal() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public List<Long> getWordIdList() {
        return wordIdList;
    }

    public void setWordIdList(List<Long> wordIdList) {
        this.wordIdList = wordIdList;
    }
}
