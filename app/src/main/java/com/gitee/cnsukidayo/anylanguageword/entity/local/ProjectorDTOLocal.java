package com.gitee.cnsukidayo.anylanguageword.entity.local;

/**
 * @author cnsukidayo
 * @date 2025/2/20 10:57
 */
public class ProjectorDTOLocal {

    /**
     * 计时的分钟数
     */
    private int minute;

    /**
     * 单词的个数
     */
    private int wordCount;

    /**
     * 总毫秒数
     */
    private long milliseconds;

    /**
     * 剩余毫秒数
     */
    private long remainingTime;

    /**
     * 是否正在运行
     */
    private boolean running;

    /**
     * 轮次
     */
    private int round;

    public ProjectorDTOLocal() {
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
