package com.gitee.cnsukidayo.anylanguageword.entity;

import com.gitee.cnsukidayo.anylanguageword.enums.CreditFilter;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditFormat;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditOrder;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditState;

/**
 * 背诵风格
 *
 * @author cnsukidayo
 * @date 2023/2/9 19:59
 */
public class UserCreditStyle {
    // 背诵状态
    private CreditState creditState = CreditState.ENGLISH_TRANSLATION_CHINESE_HEARING;
    // 背诵顺序
    private CreditOrder creditOrder = CreditOrder.ORDERLY;
    // 背诵过滤
    private CreditFilter creditFilter = CreditFilter.WORD;
    // 背诵格式
    private CreditFormat creditFormat = CreditFormat.CLASSIC;
    // 是否跳过
    private boolean ignore = false;


    public UserCreditStyle() {
    }

    public UserCreditStyle(CreditState creditState, CreditOrder creditOrder, CreditFilter creditFilter, CreditFormat creditFormat, boolean ignore) {
        this.creditState = creditState;
        this.creditOrder = creditOrder;
        this.creditFilter = creditFilter;
        this.creditFormat = creditFormat;
        this.ignore = ignore;
    }

    public CreditState getCreditState() {
        return creditState;
    }

    public void setCreditState(CreditState creditState) {
        this.creditState = creditState;
    }

    public CreditFormat getCreditFormat() {
        return creditFormat;
    }

    public void setCreditFormat(CreditFormat creditFormat) {
        this.creditFormat = creditFormat;
    }

    public CreditOrder getCreditOrder() {
        return creditOrder;
    }

    public void setCreditOrder(CreditOrder creditOrder) {
        this.creditOrder = creditOrder;
    }

    public CreditFilter getCreditFilter() {
        return creditFilter;
    }

    public void setCreditFilter(CreditFilter creditFilter) {
        this.creditFilter = creditFilter;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }
}
