package com.github.lorenj.wordtint.handler;

import com.github.lorenj.wordtint.entity.dto.DataPage;
import com.github.lorenj.wordtint.entity.dto.SearchWordParam;
import com.github.lorenj.wordtint.entity.local.WordDTOLocal;

/**
 * @author cnsukidayo
 * @date 2024/7/21 10:38
 */
public interface WordSearchHandler {

    /**
     * 搜索单词
     *
     * @param key 关键词
     * @return 返回匹配的单词集合
     */
    DataPage<WordDTOLocal> searchWord(SearchWordParam searchWordParam);

}
