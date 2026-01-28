package com.github.lorenj.wordtint.entity.local;

import com.github.lorenj.wordtint.enums.MarkColor;

import java.util.Set;

/**
 * @author cnsukidayo
 * @date 2024/12/20 17:46
 */
public class AddWordReViewParamLocal {
    /**
     * 单词的id
     */
    private int id;

    /**
     * 单词的标记
     */
    private Set<MarkColor> wordFlag;

    public AddWordReViewParamLocal() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<MarkColor> getWordFlag() {
        return wordFlag;
    }

    public void setWordFlag(Set<MarkColor> wordFlag) {
        this.wordFlag = wordFlag;
    }
}
