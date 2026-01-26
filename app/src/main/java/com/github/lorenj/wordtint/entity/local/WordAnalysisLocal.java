package com.github.lorenj.wordtint.entity.local;

import com.github.lorenj.wordtint.enums.FlagColor;

import java.util.Map;

/**
 * @author cnsukidayo
 * @date 2024/12/20 18:35
 */
public class WordAnalysisLocal {
    private Map<FlagColor, FlagColorMapInfo> mapMessage;

    public WordAnalysisLocal() {
    }

    public Map<FlagColor, FlagColorMapInfo> getMapMessage() {
        return mapMessage;
    }

    public void setMapMessage(Map<FlagColor, FlagColorMapInfo> mapMessage) {
        this.mapMessage = mapMessage;
    }

    public static class FlagColorMapInfo {
        /**
         * 当前单词背标记过的总次数
         */
        private int total;

        /**
         * 最近的一次标记时间戳
         */
        private long recentCreateTimestamp;

        public FlagColorMapInfo() {
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public long getRecentCreateTimestamp() {
            return recentCreateTimestamp;
        }

        public void setRecentCreateTimestamp(long recentCreateTimestamp) {
            this.recentCreateTimestamp = recentCreateTimestamp;
        }
    }

}
