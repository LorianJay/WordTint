package com.github.lorenj.wordtint.enums;

import com.github.lorenj.wordtint.R;

/**
 * @author lorianjay
 * @date 2026/7/26 21:05
 */
public enum ChartTime {
    TODAY(1, R.string.today),
    WEEK(7, R.string.recent_week),
    MONTH(30, R.string.recent_month),
    ALL(Integer.MAX_VALUE, R.string.all),
    CUSTOM(-1, R.string.custom);

    private int range;
    private int textId;

    ChartTime(int range, int textId) {
        this.range = range;
        this.textId = textId;
    }

    public int getRange() {
        return range;
    }

    public int getTextId() {
        return textId;
    }
}
