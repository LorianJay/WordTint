package com.github.lorenj.wordtint.enums;


import com.github.lorenj.wordtint.R;

/**
 * @author cnsukidayo
 * @date 2023/2/2 20:59
 */
public enum VIPLevel {
    NONE_VIP(R.string.none_vip),
    VIP(R.string.vip),
    ONE_YEAR_VIP(R.string.none_vip),
    TEN_YEAR_VIP(R.string.ten_year_vip),
    ONE_THOUSAND_YEAR_VIP(R.string.one_thousand_vip),
    FOREVER_VIP(R.string.forever_vip);
    private final int vipDescribe;

    VIPLevel(int vipDescribe) {
        this.vipDescribe = vipDescribe;
    }

    public int getVipDescribe() {
        return vipDescribe;
    }
}
