package com.github.lorenj.wordtint.enums;


import com.github.lorenj.wordtint.R;

/**
 * 英语单词结构
 *
 * @author cnsukidayo
 * @date 2023/11/4 12:58
 */
public enum WordStructure {

    /**
     * 单词原文
     */
    WORD_ORIGIN(0, R.string.default_txt),
    /**
     * 形容词
     */
    ADJ(1, R.string.adjective),
    /**
     * 副词
     */
    ADV(2, R.string.adverb),
    /**
     * 动词
     */
    V(3, R.string.verb),
    /**
     * 不及物动词
     */
    VI(4, R.string.intransitive_verb),
    /**
     * 及物动词
     */
    VT(5, R.string.transitive_verb),
    /**
     * 名词
     */
    N(6, R.string.noun),
    /**
     * 连词
     */
    CONJ(7, R.string.conjunction),
    /**
     * 代词
     */
    PRON(8, R.string.pronoun),
    /**
     * 数次
     */
    NUM(9, R.string.number),
    /**
     * 冠词
     */
    ART(10, R.string.article),
    /**
     * 介词
     */
    PREP(11, R.string.preposition),
    /**
     * 感叹词
     */
    INT(12, R.string.int_word),
    /**
     * 助动词
     */
    AUX(13, R.string.auxiliary),
    /**
     * 介词短语
     */
    PHRASE(14, R.string.phrase),
    /**
     * 例句
     */
    SENTENCE(15, R.string.sentence_translation);

    private final int order;
    private final int keyHint;

    WordStructure(int order, int keyHint) {
        this.order = order;
        this.keyHint = keyHint;
    }

    public int getOrder() {
        return order;
    }

    public int getKeyHint() {
        return keyHint;
    }
}
