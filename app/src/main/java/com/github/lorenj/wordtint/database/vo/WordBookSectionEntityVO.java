package com.github.lorenj.wordtint.database.vo;

import com.github.lorenj.wordtint.database.entity.WordBookSectionEntity;

/**
 * @author cnsukidayo
 * @date 2026/1/29 16:24
 */
public class WordBookSectionEntityVO {

    public WordBookSectionEntity wordBookSectionEntity;
    public boolean selection = false;

    public WordBookSectionEntityVO(WordBookSectionEntity wordBookSectionEntity) {
        this.wordBookSectionEntity = wordBookSectionEntity;
    }
}
