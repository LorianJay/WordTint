package com.github.lorenj.wordtint.entity.local;

import com.github.lorenj.wordtint.enums.MarkColor;

import java.io.Serializable;
import java.util.Set;

/**
 * 功能性单词
 *
 * @author cnsukidayo
 * @date 2024/7/21 15:28
 */
public class FunctionWordDTOLocal implements Serializable {

    /**
     * 单词的id
     */
    private Long id;
    /**
     * 当前单词的标记
     */
    private Set<MarkColor> wordsFlagList;

    public FunctionWordDTOLocal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<MarkColor> getWordsFlagList() {
        return wordsFlagList;
    }

    public void setWordsFlagList(Set<MarkColor> wordsFlagList) {
        this.wordsFlagList = wordsFlagList;
    }
}
