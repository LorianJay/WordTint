package com.github.lorenj.wordtint.enums;

/**
 * @author sukidayo
 * @date 2023/7/28 16:13
 */
public enum DivideType {

    OFFICIAL("官方"),
    PERSONAL("个人"),
    BASE("总库");
    private final String value;

    DivideType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
