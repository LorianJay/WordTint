package com.github.lorenj.wordtint.handler;

import com.github.lorenj.wordtint.entity.local.WordDTOLocal;

import java.util.Map;

/**
 * @author cnsukidayo
 * @date 2024/2/19 16:00
 */
public interface WordFunctionContextHandler {
    /**
     * 添加一个单词到查询缓存中
     *
     * @param structureWord 单词结构对象
     */
    void addWordQueryCache(Map<Long, WordDTOLocal> structureWord);

    /**
     * 设置本次使用的语种Id
     *
     * @param languageId 语种
     */
    void setCurrentLanguageId(Long languageId);

    /**
     * 获取当前使用的语种Id
     *
     * @return 返回值不为null
     */
    Long getCurrentLanguageId();

}
