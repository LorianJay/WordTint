package com.gitee.cnsukidayo.anylanguageword.entity.local;

import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;

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
    private Set<FlagColor> wordFlag;

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

    public Set<FlagColor> getWordFlag() {
        return wordFlag;
    }

    public void setWordFlag(Set<FlagColor> wordFlag) {
        this.wordFlag = wordFlag;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }
}
