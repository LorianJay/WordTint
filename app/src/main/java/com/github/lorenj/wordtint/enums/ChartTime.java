package com.github.lorenj.wordtint.enums;

import com.github.lorenj.wordtint.R;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * @author lorianjay
 * @date 2026/7/26 21:05
 */
public enum ChartTime {
    TODAY(1, R.string.today, DateTimeFormatter.ofPattern("HH:00"), Duration.ofHours(1)),
    WEEK(7, R.string.recent_week, DateTimeFormatter.ofPattern("MM-dd"), Duration.ofDays(1)),
    MONTH(30, R.string.recent_month, DateTimeFormatter.ofPattern("MM-dd"), Duration.ofDays(1)),
    THREE_MONTH(30 * 3, R.string.recent_three_month, DateTimeFormatter.ofPattern("MM-dd"), Duration.ofDays(1)),
    SIX_MONTH(30 * 6, R.string.recent_six_month, DateTimeFormatter.ofPattern("yyyy-MM"), Duration.ofDays(30)),
    YEAR(365, R.string.recent_year, DateTimeFormatter.ofPattern("yyyy-MM"), Duration.ofDays(30)),
    ALL(365 * 5, R.string.recent_five_year, DateTimeFormatter.ofPattern("yyyy-MM"), Duration.ofDays(30)),
    CUSTOM(-1, R.string.custom, DateTimeFormatter.ofPattern("MM-dd"), Duration.ofDays(1));

    private final int range;
    private final int textId;
    private final DateTimeFormatter dateTimeFormatter;
    /**
     * 横坐标时间划分尺度,以多少时间为一个刻度
     */
    private final Duration addDuration;

    ChartTime(int range, int textId, DateTimeFormatter dateTimeFormatter, Duration addDuration) {
        this.range = range;
        this.textId = textId;
        this.dateTimeFormatter = dateTimeFormatter;
        this.addDuration = addDuration;
    }

    public int getRange() {
        return range;
    }

    public int getTextId() {
        return textId;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public Duration getAddDuration() {
        return addDuration;
    }
}
