package com.github.lorenj.wordtint.database.vo;

import java.util.Objects;

/**
 * 单词标记统计值对象
 *
 * @author cnsukidayo
 * @date 2026/7/26
 */
public class WordMarkCountVO {

    /**
     * 单词的id
     */
    private int markWordId;
    /**
     * 在当前时间范围内被标记的次数
     */
    private int count;
    /**
     * 平均停留的时间
     */
    private double avgStayTime;
    /**
     * 单词原意
     */
    private String wordText;

    public WordMarkCountVO() {
    }

    public int getMarkWordId() {
        return markWordId;
    }

    public void setMarkWordId(int markWordId) {
        this.markWordId = markWordId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAvgStayTime() {
        return avgStayTime;
    }

    public void setAvgStayTime(double avgStayTime) {
        this.avgStayTime = avgStayTime;
    }

    public String getWordText() {
        return wordText;
    }

    public void setWordText(String wordText) {
        this.wordText = wordText;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        WordMarkCountVO that = (WordMarkCountVO) o;
        return markWordId == that.markWordId
                && count == that.count
                && Double.compare(avgStayTime, that.avgStayTime) == 0
                && Objects.equals(wordText, that.wordText);
    }

    @Override
    public int hashCode() {
        int result = markWordId;
        result = 31 * result + count;
        result = 31 * result + Double.hashCode(avgStayTime);
        result = 31 * result + Objects.hashCode(wordText);
        return result;
    }
}
