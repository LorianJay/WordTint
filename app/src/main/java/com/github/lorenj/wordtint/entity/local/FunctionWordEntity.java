package com.github.lorenj.wordtint.entity.local;

import com.github.lorenj.wordtint.enums.MarkColor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sukidayo
 * @date 2023/7/28 16:30
 */
public class FunctionWordEntity extends WordDTOLocal implements Serializable {

    /**
     * 当前单词对应的标记颜色列表
     */
    private Set<MarkColor> markColorList = new HashSet<>(2);

    public FunctionWordEntity() {
        markColorList.add(MarkColor.GREEN);
        markColorList.add(MarkColor.BROWN);
    }

    public Set<MarkColor> getMarkColorList() {
        return markColorList;
    }

    public void setMarkColorList(Set<MarkColor> markColorList) {
        this.markColorList = markColorList;
    }
}
