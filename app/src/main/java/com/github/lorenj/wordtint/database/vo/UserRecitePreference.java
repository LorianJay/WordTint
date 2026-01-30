package com.github.lorenj.wordtint.database.vo;

import com.github.lorenj.wordtint.enums.ReciteFilter;
import com.github.lorenj.wordtint.enums.ReciteStyle;
import com.github.lorenj.wordtint.enums.ReciteOrder;
import com.github.lorenj.wordtint.enums.ReciteMode;

import java.io.Serializable;

/**
 * 背诵偏好
 *
 * @author cnsukidayo
 * @date 2023/2/9 19:59
 */
public class UserRecitePreference implements Serializable {
    // 背诵状态
    private ReciteMode reciteMode = ReciteMode.ENGLISH_TRANSLATION_CHINESE_HEARING;
    // 背诵顺序
    private ReciteOrder reciteOrder = ReciteOrder.ORDERLY;
    // 背诵过滤
    private ReciteFilter reciteFilter = ReciteFilter.WORD;
    // 背诵格式
    private ReciteStyle reciteStyle = ReciteStyle.CLASSIC;
    // 是否跳过
    private boolean ignore = false;
    // 是否是回顾复习
    private boolean review = false;

    public UserRecitePreference() {
    }

    public UserRecitePreference(ReciteMode reciteMode,
                                ReciteOrder reciteOrder,
                                ReciteFilter reciteFilter,
                                ReciteStyle reciteStyle,
                                boolean ignore,
                                boolean review) {
        this.reciteMode = reciteMode;
        this.reciteOrder = reciteOrder;
        this.reciteFilter = reciteFilter;
        this.reciteStyle = reciteStyle;
        this.ignore = ignore;
        this.review = review;
    }

    public ReciteMode getReciteMode() {
        return reciteMode;
    }

    public void setReciteMode(ReciteMode reciteMode) {
        this.reciteMode = reciteMode;
    }

    public ReciteOrder getReciteOrder() {
        return reciteOrder;
    }

    public void setReciteOrder(ReciteOrder reciteOrder) {
        this.reciteOrder = reciteOrder;
    }

    public ReciteFilter getReciteFilter() {
        return reciteFilter;
    }

    public void setReciteFilter(ReciteFilter reciteFilter) {
        this.reciteFilter = reciteFilter;
    }

    public ReciteStyle getReciteStyle() {
        return reciteStyle;
    }

    public void setReciteStyle(ReciteStyle reciteStyle) {
        this.reciteStyle = reciteStyle;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isReview() {
        return review;
    }

    public void setReview(boolean review) {
        this.review = review;
    }
}
