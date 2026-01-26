package com.github.lorenj.wordtint.enums.structure;

/**
 * 语种基础结构
 *
 * @author cnsukidayo
 * @date 2023/11/4 12:59
 */
public interface BaseStructure {

    /**
     * 得到当前单词结构对应的id
     *
     * @return 单词结构的id
     */
    Long getWordStructureId();

    /**
     * 得到当前单词结构对应的提示
     *
     * @return 返回提示信息在String资源中的编号
     */
    int getTitleHint();

    /**
     * 当前结构在展示界面的顺序
     *
     * @return 返回order值
     */
    int getOrder();
}
