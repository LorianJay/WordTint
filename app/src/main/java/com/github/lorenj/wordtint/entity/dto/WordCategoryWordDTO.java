package com.github.lorenj.wordtint.entity.dto;


/**
 * @author sukidayo
 * @date 2023/7/27 10:10
 */
public class WordCategoryWordDTO {

    private Long wordCategoryId;

    private Long wordId;

    private Integer wordOrder;

    public WordCategoryWordDTO() {
    }

    public Long getWordCategoryId() {
        return wordCategoryId;
    }

    public void setWordCategoryId(Long wordCategoryId) {
        this.wordCategoryId = wordCategoryId;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public Integer getWordOrder() {
        return wordOrder;
    }

    public void setWordOrder(Integer wordOrder) {
        this.wordOrder = wordOrder;
    }
}
