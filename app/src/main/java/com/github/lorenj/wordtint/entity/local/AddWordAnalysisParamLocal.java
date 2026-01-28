package com.github.lorenj.wordtint.entity.local;

import com.github.lorenj.wordtint.enums.MarkColor;

import java.util.Set;

/**
 * @author cnsukidayo
 * @date 2024/12/20 17:46
 */
public class AddWordAnalysisParamLocal {
    /**
     * 单词的id
     */
    private int id;

    /**
     * 单词的标记
     */
    private Set<MarkColor> wordFlag;

    /**
     * 单词的时间戳
     */
    private long createTimestamp;

    public AddWordAnalysisParamLocal() {
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

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }
}
