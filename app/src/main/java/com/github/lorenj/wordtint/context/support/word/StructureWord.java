package com.github.lorenj.wordtint.context.support.word;

import com.github.lorenj.wordtint.entity.dto.WordDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 结构单词;只是单个单词不是列表形式!
 *
 * @author cnsukidayo
 * @date 2024/2/19 9:59
 */
public class StructureWord {

    private Map<Long, List<WordDTO>> structureWordMap;

    private StructureWord() {
    }

    /**
     * 将单词信息元素转换为结构单词信息
     *
     * @param wordDTOList 元数据列表
     * @return 返回单词结构信息
     */
    public static Map<Long, List<WordDTO>> convert(List<WordDTO> wordDTOList) {
        return wordDTOList.stream()
                .collect(Collectors.groupingBy(WordDTO::getWordStructureId, Collectors.toList()));
    }
}
