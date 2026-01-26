package com.github.lorenj.wordtint.context.support.category;

import com.github.lorenj.wordtint.enums.structure.BaseStructure;

import java.util.Map;

/**
 * 简化单词展示Map;<br>
 * 因为不同的语种的单词哪些信息要展示在前端是不确定的<br>
 * 因为单词的流转是全部的元数据流转,所以通过当前接口你就可以根据语种获取当前需要过滤哪些单词元信息
 *
 * @author cnsukidayo
 * @date 2024/2/19 15:23
 */
public interface WordMetaInfoFilter {

    /**
     * 得到元数据过滤Map
     *
     * @param languageId 语种的Id
     * @return Map的key为单词的结构id, map的value为结构信息
     */
    Map<Integer, BaseStructure> getMetaInfoFilterMap(Long languageId);

}
