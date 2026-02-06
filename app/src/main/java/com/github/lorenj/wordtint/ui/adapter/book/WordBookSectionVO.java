package com.github.lorenj.wordtint.ui.adapter.book;

import com.github.lorenj.wordtint.database.entity.WordBookSectionEntity;
import com.github.lorenj.wordtint.enums.MarkColor;

import java.util.Objects;

/**
 * @author cnsukidayo
 * @date 2026/1/29 16:24
 */
public class WordBookSectionVO {

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

    public WordBookSectionVO(WordBookSectionEntity wordBookSectionEntity) {
        this.wordBookSectionEntity = wordBookSectionEntity;
    }

    public static WordBookSectionVO clone(WordBookSectionVO wordBookSectionVO) {
        WordBookSectionVO result = new WordBookSectionVO(wordBookSectionVO.wordBookSectionEntity);
        result.selection = wordBookSectionVO.selection;
        result.elementCount = wordBookSectionVO.elementCount;
        result.tagColor = wordBookSectionVO.tagColor;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WordBookSectionVO that = (WordBookSectionVO) o;

        if (selection != that.selection) return false;
        if (elementCount != that.elementCount) return false;
        if (!Objects.equals(wordBookSectionEntity, that.wordBookSectionEntity))
            return false;
        return tagColor == that.tagColor;
    }

    @Override
    public int hashCode() {
        int result = wordBookSectionEntity != null ? wordBookSectionEntity.hashCode() : 0;
        result = 31 * result + (selection ? 1 : 0);
        result = 31 * result + elementCount;
        result = 31 * result + (tagColor != null ? tagColor.hashCode() : 0);
        return result;
    }
}
