package com.github.lorenj.wordtint.enums;

/**
 * @author cnsukidayo
 * @date 2026/1/30 13:17
 */
public enum UserSettingKeyEnums {
    /**
     * 用户协议是否同意
     */
    AGREE_USER_POLICY(Boolean.class, false),
    /**
     * 是否跳过偏好设置
     */
    SKIP_PREFERENCE(Boolean.class, false),
    /**
     * 背诵模式
     */
    RECITE_MODE(ReciteMode.class, ReciteMode.ENGLISH_TRANSLATION_CHINESE_HEARING),
    /**
     * 背诵顺序
     */
    RECITE_ORDER(ReciteOrder.class, ReciteOrder.ORDERLY),
    /**
     * 背诵过滤
     */
    RECITE_FILTER(ReciteFilter.class, ReciteFilter.NO_FILER),
    /**
     * 背诵的风格
     */
    RECITE_STYLE(ReciteStyle.class, ReciteStyle.CLASSIC),
    /**
     * 背诵去重
     */
    RECITE_DISTINCT(Boolean.class, true),
    /**
     * 介词显示模式
     */
    Recite_PREPOSITION(RecitePreposition.class, RecitePreposition.VISIBLE);

    public final Class<?> type;
    public final Object defaultValue;

    UserSettingKeyEnums(Class<?> type, Object defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }
}
