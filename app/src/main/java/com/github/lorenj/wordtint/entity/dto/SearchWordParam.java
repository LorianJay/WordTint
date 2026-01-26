package com.github.lorenj.wordtint.entity.dto;


/**
 * @author sukidayo
 * @date 2023/9/11 19:37
 */
public class SearchWordParam extends PageQueryParam {

    private String word;

    private Long languageId;

    public SearchWordParam() {
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }
}
