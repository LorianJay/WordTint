package com.github.lorenj.wordtint.ui.adapter.analysis;

import com.github.lorenj.wordtint.enums.ChartTime;

/**
 * @author lorianjay
 * @date 2026/7/27 20:09
 */
public class ChartTimeRange {

    private long startTime;
    private long endTime;
    private ChartTime chartTime;

    public ChartTimeRange() {
    }

    public ChartTimeRange(long startTime, long endTime, ChartTime chartTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.chartTime = chartTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public ChartTime getChartTime() {
        return chartTime;
    }

    public void setChartTime(ChartTime chartTime) {
        this.chartTime = chartTime;
    }
}
