package com.github.lorenj.wordtint.enums;


import com.github.lorenj.wordtint.R;

/**
 * @author cnsukidayo
 * @date 2023/1/6 17:10
 */
public enum MeaningCategory {
    ADJECTIVE(R.string.adjective),
    ADVERB(R.string.adverb),
    VERB(R.string.verb),
    INTRANSITIVE_VERB(R.string.intransitive_verb),
    TRANSITIVE_VERB(R.string.transitive_verb),
    NOUN(R.string.noun),
    CONJUNCTION(R.string.conjunction),
    PRONOUN(R.string.pronoun),
    NUMBER(R.string.number),
    ARTICLE(R.string.article),
    PREPOSITION(R.string.preposition),
    INT(R.string.int_word),
    AUXILIARY(R.string.auxiliary);

    private int mapValue;

    MeaningCategory(int mapValue) {
        this.mapValue = mapValue;
    }

    public int getMapValue() {
        return mapValue;
    }
}
