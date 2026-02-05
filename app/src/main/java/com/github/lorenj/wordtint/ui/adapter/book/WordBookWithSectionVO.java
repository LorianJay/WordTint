package com.github.lorenj.wordtint.ui.adapter.book;

import com.github.lorenj.wordtint.database.entity.WordBookEntity;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2026/1/29 15:23
 */
public class WordBookWithSectionVO implements BaseBookItem {
    public WordBookEntity wordBookEntity;
    public List<WordBookSectionVO> wordBookSectionVOList;
    /**
     * 当前书本有没有被折叠
     */
    public boolean folded;

    public WordBookWithSectionVO(WordBookEntity wordBookEntity,
                                 List<WordBookSectionVO> wordBookSectionVOList) {
        this.wordBookEntity = wordBookEntity;
        this.wordBookSectionVOList = wordBookSectionVOList;
        this.folded = true;
    }

    @Override
    public int getItemType() {
        return BOOK;
    }
}
