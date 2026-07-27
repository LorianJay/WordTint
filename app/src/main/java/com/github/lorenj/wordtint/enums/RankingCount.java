package com.github.lorenj.wordtint.enums;

import com.github.lorenj.wordtint.R;

/**
 * @author lorianjay
 * @date 2026/7/27 16:53
 */
public enum RankingCount {

    TEN(10, R.string.number_ten),
    TWENTY_FIVE(25, R.string.number_twenty_five),
    FIFTY(50, R.string.number_fifty),
    ALL(Integer.MAX_VALUE, R.string.all),
    CUSTOM(-1, R.string.custom);
    private final int count;
    private final int textId;

    RankingCount(int count, int textId) {
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
