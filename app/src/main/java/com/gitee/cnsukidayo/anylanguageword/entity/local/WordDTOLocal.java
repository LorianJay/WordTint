package com.gitee.cnsukidayo.anylanguageword.entity.local;

import com.gitee.cnsukidayo.anylanguageword.enums.structure.EnglishStructure;

import java.io.Serializable;
import java.util.Map;

/**
 * @author sukidayo
 * @date 2023/7/28 16:30
 */
public class WordDTOLocal implements Serializable {

    private String origin;

    /**
     * 当前单词存储的意思
     */
    private Map<EnglishStructure, String> value;

    /**
     * 当前单词在这个划分中的顺序
     */
    private int order;

    /**
     * 当前单词的id
     */
    private Long id;

    private String from;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Map<EnglishStructure, String> getValue() {
        return value;
    }

    public void setValue(Map<EnglishStructure, String> value) {
        this.value = value;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
