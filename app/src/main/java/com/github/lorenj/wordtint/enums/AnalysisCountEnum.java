package com.github.lorenj.wordtint.enums;

import com.github.lorenj.wordtint.R;

/**
 * @author lorianjay
 * @date 2026/7/27 16:53
 */
public enum AnalysisCountEnum {

    TEN(10, R.string.number_ten),
    TWENTY_FIVE(25, R.string.number_twenty_five),
    FIFTY(50, R.string.number_filter),
    ALL(Integer.MAX_VALUE, R.string.all),
    CUSTOM(-1, R.string.custom);
    public final int count;
    public final int textId;

    AnalysisCountEnum(int count, int textId) {
        this.count = count;
        this.textId = textId;
    }

    public int getCount() {
        return count;
    }

    public int getTextId() {
        return textId;
    }
}
