package com.github.lorenj.wordtint.database.vo;

import com.github.lorenj.wordtint.database.entity.WordBookSectionEntity;

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

    public WordBookSectionEntityVO(WordBookSectionEntity wordBookSectionEntity) {
        this.wordBookSectionEntity = wordBookSectionEntity;
    }
}
