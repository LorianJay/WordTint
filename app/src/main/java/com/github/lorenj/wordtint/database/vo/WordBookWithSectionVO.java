package com.github.lorenj.wordtint.database.vo;

import com.github.lorenj.wordtint.database.entity.WordBookEntity;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2026/1/29 15:23
 */
public class WordBookWithSectionVO {
    public WordBookEntity wordBookEntity;
    public List<WordBookSectionEntityVO> wordBookSectionEntityVOList;
    /**
     * 当前书本有没有被折叠
     */
    public boolean folded;

    public WordBookWithSectionVO(WordBookEntity wordBookEntity,
                                 List<WordBookSectionEntityVO> wordBookSectionEntityVOList) {
        this.wordBookEntity = wordBookEntity;
        this.wordBookSectionEntityVOList = wordBookSectionEntityVOList;
        this.folded = true;
    }
}
