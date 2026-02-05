package com.github.lorenj.wordtint.database.vo;

import com.github.lorenj.wordtint.database.entity.WordBookSectionEntity;
import com.github.lorenj.wordtint.enums.MarkColor;

import java.util.Objects;

/**
 * @author cnsukidayo
 * @date 2026/1/29 16:24
 */
public class WordBookSectionEntityVO {

    public WordBookSectionEntity wordBookSectionEntity;
    /**
     * 当前是否被选中
     */
    public boolean selection = false;
    /**
     * 当前片段(章节)的单词数量
     */
    public int elementCount;

    /**
     * 当前片段(章节)的标记
     */
    public MarkColor tagColor;

    public WordBookSectionEntityVO(WordBookSectionEntity wordBookSectionEntity) {
        this.wordBookSectionEntity = wordBookSectionEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordBookSectionEntityVO that = (WordBookSectionEntityVO) o;
        return selection == that.selection
                && elementCount == that.elementCount
                && Objects.equals(wordBookSectionEntity, that.wordBookSectionEntity)
                && tagColor == that.tagColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordBookSectionEntity, selection, elementCount, tagColor);
    }
}
