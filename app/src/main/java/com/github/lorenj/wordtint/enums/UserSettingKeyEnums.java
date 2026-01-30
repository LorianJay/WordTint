package com.github.lorenj.wordtint.enums;

/**
 * @author cnsukidayo
 * @date 2026/1/30 13:17
 */
public enum UserSettingKeyEnums {
    AGREE_USER_POLICY(Boolean.class, false);

    public final Class<?> type;
    public final Object defaultValue;

    UserSettingKeyEnums(Class<?> type, Object defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }
}
