package com.github.lorenj.wordtint.context.support.category;

import com.github.lorenj.wordtint.enums.structure.BaseStructure;
import com.github.lorenj.wordtint.enums.structure.EnglishStructure;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cnsukidayo
 * @date 2024/2/19 15:29
 */
public class WordMetaInfoFilterImpl implements WordMetaInfoFilter {

    /**
     * 第一个key为语种的id;
     * 第二个key为展示的顺序;也就是元数据列表要按照什么样的顺序进行展示(如果有就展示,否则不展示)
     * 第二个value为对应的结构体信息
     */
    private final Map<Long, Map<Integer, BaseStructure>> cacheMap;

    public WordMetaInfoFilterImpl() {
        Map<Long, Map<Integer, BaseStructure>> temp = new HashMap<>() {{
            put(2L, new HashMap<>() {{
                put(0, EnglishStructure.ADJ);
                put(1, EnglishStructure.ADV);
                put(2, EnglishStructure.V);
                put(3, EnglishStructure.VI);
                put(4, EnglishStructure.VT);
                put(5, EnglishStructure.N);
                put(6, EnglishStructure.CONJ);
                put(7, EnglishStructure.PRON);
                put(8, EnglishStructure.NUM);
                put(9, EnglishStructure.ART);
                put(10, EnglishStructure.PREP);
                put(11, EnglishStructure.INT);
                put(12, EnglishStructure.AUX);
            }});
        }};
        cacheMap = Collections.unmodifiableMap(temp);
    }

    @Override
    public Map<Integer, BaseStructure> getMetaInfoFilterMap(Long languageId) {
        return cacheMap.get(languageId);
    }
}
