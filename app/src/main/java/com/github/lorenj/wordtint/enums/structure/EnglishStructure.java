package com.github.lorenj.wordtint.enums.structure;


import com.github.lorenj.wordtint.R;

/**
 * 英语单词结构
 *
 * @author cnsukidayo
 * @date 2023/11/4 12:58
 */
public enum EnglishStructure implements BaseStructure {

    DEFAULT(0L, -1, Integer.MAX_VALUE, false, false, true),
    /**
     * 单词原文
     */
    WORD_ORIGIN(27L, -1, -1, false, false, true),
    /**
     * 美式音标
     */
    US_PHONETIC(16L, -1, -1, false, false, true),
    /**
     * 英式音标
     */
    UK_PHONETIC(17L, -1, -1, false, false, true),
    /**
     * 形容词
     */
    ADJ(1L, R.string.adjective, 1, true, false, true),
    /**
     * 副词
     */
    ADV(2L, R.string.adverb, 2, true, false, true),
    /**
     * 动词
     */
    V(3L, R.string.verb, 3, true, false, true),
    /**
     * 不及物动词
     */
    VI(4L, R.string.intransitive_verb, 4, true, false, true),
    /**
     * 及物动词
     */
    VT(5L, R.string.transitive_verb, 5, true, false, true),
    /**
     * 名词
     */
    N(6L, R.string.noun, 6, true, false, true),
    /**
     * 连词
     */
    CONJ(7L, R.string.conjunction, 7, true, false, true),
    /**
     * 代词
     */
    PRON(8L, R.string.pronoun, 8, true, false, true),
    /**
     * 数次
     */
    NUM(9L, R.string.number, 9, true, false, true),
    /**
     * 冠词
     */
    ART(10L, R.string.article, 10, true, false, true),
    /**
     * 介词
     */
    PREP(11L, R.string.preposition, 11, true, false, true),
    /**
     * 感叹词
     */
    INT(12L, R.string.int_word, 12, true, false, true),
    /**
     * 助动词
     */
    AUX(13L, R.string.auxiliary, 13, true, false, true),
    /**
     * 扩展
     */
    EXPAND(14L, -1, 14, true, true, true),
    /**
     * 介词短语
     */
    PHRASE(15L, R.string.phrase, 15, true, true,true),
    /**
     * 介词短语英文翻译
     */
    PHRASE_TRANSLATION(24L, R.string.phrase_translation, 16, false, false,false),
    /**
     * 单词记忆的方法
     */
    REMEMBER_METHOD(34L, R.string.remember_method, 17, true, false,true),
    /**
     * 例句
     */
    SENTENCE(22L, R.string.example_sentence, 18, true, true,true),
    /**
     * 例句翻译英文翻译
     */
    SENTENCE_TRANSLATION(23L, R.string.sentence_translation, 19, false, false,false),
    /**
     * 例题:
     */
    QUESTION(18L, R.string.question, 20, true, true,true),
    /**
     * 问题选项的索引
     */
    CHOICE_INDEX(21L, R.string.choice_index, 21, false, false,false),
    /**
     * 问题选项的内容
     */
    CHOICE_VALUE(26L, R.string.choice_value, 22, false, false,false),
    /**
     * 正确的答案索引
     */
    RIGHT_INDEX(20L, R.string.right_index, 23, false, true,false),
    /**
     * 问题的答案解释
     */
    QUESTION_EXPLAIN(19L, R.string.question_explain, 24, false, true,true),
    /**
     * 单词的英文解释
     */
    DESCRIBE_ENGLISH(25L, R.string.describe_english, 25, false, true,true),

    /**
     * 真题例句(英文)
     */
    REAL_EXAM_SENTENCE(29L, R.string.real_exam_sentence, 26, true, true,true),
    /**
     * 真题出自哪一套
     */
    SOURCE_PAPER(30L, R.string.source_paper, 27, false, false,false),
    /**
     * 真题等级(四级还是六级)
     */
    SOURCE_LEVEL(31L, R.string.source_level, 28, false, false,false),
    /**
     * 真题的年份
     */
    SOURCE_YEAR(32L, R.string.source_year, 29, false, false,false),
    /**
     * 真题的类型
     */
    SOURCE_TYPE(33L, R.string.source_type, 30, false, false,false),

    /**
     * 联想词
     */
    SYNO_WORD(38L, R.string.syno_word, 31, true, true,true),
    /**
     * 联想词的词性
     */
    SYNO_POS(35L, R.string.syno_pos, 32, false, true,false),
    /**
     * 联想词的翻译
     */
    SYNO_TRANSLATION(36L, R.string.syno_translation, 33, false, true,false),
    /**
     * 联想词对应的详细信息的id
     */
    SYNO_WORD_ID(37L, R.string.syno_word_id, 34, false, false,false),

    /**
     * 同根词的单词
     */
    REL_WORD(40L, R.string.rel_word, 35, true, true,true),
    /**
     * 同根词的词性
     */
    REL_POS(39L, R.string.rel_pos, 36, false, true,false),
    /**
     * 同根词的翻译
     */
    REL_WORD_TRANSLATION(41L, R.string.rel_word_translation, 37, false, false,false),
    /**
     * 同根词对应的单词id
     */
    REL_WORD_ID(42L, R.string.rel_word_id, 38, false, false,false),

    /**
     * 单词的图片
     */
    PICTURE(28L, R.string.picture, 39, true, true,true);

    /**
     * 单词对应的结构id
     */
    private final Long wordStructureId;
    /**
     * 标题
     */
    private final int titleHint;
    /**
     * 单词项在界面中展示的顺序
     */
    private final int order;

    /**
     * 是否添加title
     */
    private final boolean appendTitle;

    private final boolean valueBreak;

    private final boolean titleBreak;

    EnglishStructure(Long wordStructureId,
                     int titleHint,
                     int order,
                     boolean appendTitle,
                     boolean valueBreak,
                     boolean titleBreak) {
        this.wordStructureId = wordStructureId;
        this.titleHint = titleHint;
        this.order = order;
        this.appendTitle = appendTitle;
        this.valueBreak = valueBreak;
        this.titleBreak = titleBreak;
    }

    @Override
    public Long getWordStructureId() {
        return wordStructureId;
    }

    @Override
    public int getTitleHint() {
        return titleHint;
    }

    public int getOrder() {
        return order;
    }

    public boolean isAppendTitle() {
        return appendTitle;
    }

    public boolean isValueBreak() {
        return valueBreak;
    }

    public boolean isTitleBreak() {
        return titleBreak;
    }
}
