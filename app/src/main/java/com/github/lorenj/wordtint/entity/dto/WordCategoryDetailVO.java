package com.github.lorenj.wordtint.entity.dto;


import java.util.List;

/**
 * @author sukidayo
 * @date 2023/7/27 10:10
 */
public class WordCategoryDetailVO extends WordCategoryDTO {

    private List<WordCategoryWordDTO> wordCategoryWordList;

    public WordCategoryDetailVO() {
    }

    public List<WordCategoryWordDTO> getWordCategoryWordList() {
        return wordCategoryWordList;
    }

    public void setWordCategoryWordList(List<WordCategoryWordDTO> wordCategoryWordList) {
        this.wordCategoryWordList = wordCategoryWordList;
    }
}
