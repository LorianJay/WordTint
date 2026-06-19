package com.github.lorenj.wordtint.database.vo;

import com.github.lorenj.wordtint.enums.ReciteFilter;
import com.github.lorenj.wordtint.enums.ReciteMode;
import com.github.lorenj.wordtint.enums.ReciteOrder;
import com.github.lorenj.wordtint.enums.ReciteOrigin;
import com.github.lorenj.wordtint.enums.RecitePreposition;
import com.github.lorenj.wordtint.enums.ReciteStyle;

import java.io.Serializable;
import java.util.List;

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
    private ReciteFilter reciteFilter = ReciteFilter.NO_FILER;
    // 背诵格式
    private ReciteStyle reciteStyle = ReciteStyle.CLASSIC;
    // 片段来源
    private ReciteOrigin reciteOrigin = ReciteOrigin.RECITE_LIST;
    // 介词模式
    private RecitePreposition recitePreposition = RecitePreposition.VISIBLE;
    // 所有选中的章节
    private List<Integer> allSectionIdList;
    // 是否跳过
    private boolean ignore = false;
    // 背诵去重
    private boolean reciteDistinct = true;

    public UserRecitePreference() {
    }

    public UserRecitePreference(ReciteMode reciteMode,
                                ReciteOrder reciteOrder,
                                ReciteFilter reciteFilter,
                                ReciteStyle reciteStyle,
                                ReciteOrigin reciteOrigin,
                                RecitePreposition recitePreposition,
                                boolean ignore,
                                List<Integer> allSectionIdList,
                                boolean reciteDistinct) {
        this.reciteMode = reciteMode;
        this.reciteOrder = reciteOrder;
        this.reciteFilter = reciteFilter;
        this.reciteStyle = reciteStyle;
        this.reciteOrigin = reciteOrigin;
        this.recitePreposition = recitePreposition;
        this.ignore = ignore;
        this.allSectionIdList = allSectionIdList;
        this.reciteDistinct = reciteDistinct;
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

    public ReciteOrigin getReciteOrigin() {
        return reciteOrigin;
    }

    public void setReciteOrigin(ReciteOrigin reciteOrigin) {
        this.reciteOrigin = reciteOrigin;
    }

    public List<Integer> getAllSectionIdList() {
        return allSectionIdList;
    }

    public void setAllSectionIdList(List<Integer> allSectionIdList) {
        this.allSectionIdList = allSectionIdList;
    }

    public RecitePreposition getRecitePreposition() {
        return recitePreposition;
    }

    public void setRecitePreposition(RecitePreposition recitePreposition) {
        this.recitePreposition = recitePreposition;
    }

    public boolean isReciteDistinct() {
        return reciteDistinct;
    }

    public void setReciteDistinct(boolean reciteDistinct) {
        this.reciteDistinct = reciteDistinct;
    }
}
