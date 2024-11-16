package com.gitee.cnsukidayo.anylanguageword.entity.local;

import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;

import java.util.Set;

/**
 * 功能性单词
 *
 * @author cnsukidayo
 * @date 2024/7/21 15:28
 */
public class FunctionWordDTOLocal {

    /**
     * 单词的id
     */
    private Long id;
    /**
     * 当前单词的标记
     */
    private Set<FlagColor> wordsFlagList;

    public FunctionWordDTOLocal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<FlagColor> getWordsFlagList() {
        return wordsFlagList;
    }

    public void setWordsFlagList(Set<FlagColor> wordsFlagList) {
        this.wordsFlagList = wordsFlagList;
    }
}
