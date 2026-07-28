package com.github.lorenj.wordtint.ui.adapter.analysis;

/**
 * @author lorianjay
 * @date 2026/7/27 22:56
 */
public class ChartPoint {
    private String label;
    private int count;

    public ChartPoint(String label, int count) {
        this.label = label;
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
