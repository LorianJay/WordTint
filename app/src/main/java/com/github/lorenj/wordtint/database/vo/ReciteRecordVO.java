package com.github.lorenj.wordtint.database.vo;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.entity.ReciteRecordEntity;
import com.github.lorenj.wordtint.enums.ReciteFilter;
import com.github.lorenj.wordtint.enums.ReciteMode;
import com.github.lorenj.wordtint.enums.ReciteOrder;

/**
 * @author cnsukidayo
 * @date 2026/2/4 21:30
 */
public class ReciteRecordVO {
    /**
     * 原本的背诵记录对象
     */
    private ReciteRecordEntity reciteRecordEntity;

    private ReciteMode reciteMode = ReciteMode.ENGLISH_TRANSLATION_CHINESE_HEARING;

    private ReciteOrder reciteOrder = ReciteOrder.ORDERLY;

    private ReciteFilter reciteFilter = ReciteFilter.NO_FILER;

    private int hidePreposition = R.string.visible;

    public ReciteRecordVO() {
    }

    public ReciteRecordVO(ReciteRecordEntity reciteRecordEntity,
                          ReciteMode reciteMode,
                          ReciteOrder reciteOrder,
                          ReciteFilter reciteFilter,
                          int hidePreposition) {
        this.reciteRecordEntity = reciteRecordEntity;
        this.reciteMode = reciteMode;
        this.reciteOrder = reciteOrder;
        this.reciteFilter = reciteFilter;
        this.hidePreposition = hidePreposition;
    }

    public ReciteRecordEntity getReciteRecordEntity() {
        return reciteRecordEntity;
    }

    public void setReciteRecordEntity(ReciteRecordEntity reciteRecordEntity) {
        this.reciteRecordEntity = reciteRecordEntity;
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

    public int getHidePreposition() {
        return hidePreposition;
    }

    public void setHidePreposition(int hidePreposition) {
        this.hidePreposition = hidePreposition;
    }
}
