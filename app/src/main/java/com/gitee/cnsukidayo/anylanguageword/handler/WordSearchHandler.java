package com.gitee.cnsukidayo.anylanguageword.handler;

import com.gitee.cnsukidayo.anylanguageword.entity.local.WordDTOLocal;

import io.github.cnsukidayo.wword.model.dto.support.DataPage;
import io.github.cnsukidayo.wword.model.params.SearchWordParam;

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
    DataPage<WordDTOLocal> searchWord(SearchWordParam  searchWordParam);

}
