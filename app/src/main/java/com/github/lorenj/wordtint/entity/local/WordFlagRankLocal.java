package com.github.lorenj.wordtint.entity.local;

import java.io.Serializable;

/**
 * @author sukidayo
 * @date 2023/7/28 16:30
 */
public class WordFlagRankLocal implements Serializable {
    /**
     * 单词的id
     */
    private int wordId;
    /**
     * 单词出现的次数
     */
    private int count;

    /**
     * 单词id对应的单词原文内容
     */
    private String wordOrigin;

    public WordFlagRankLocal() {
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getWordOrigin() {
        return wordOrigin;
    }

    public void setWordOrigin(String wordOrigin) {
        this.wordOrigin = wordOrigin;
    }
}
