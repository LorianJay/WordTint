package com.github.lorenj.wordtint.enums;

/**
 * @author sukidayo
 * @date 2023/7/24 19:27
 */
public enum SexType {

    MALE("男"),
    FEMALE("女"),
    SECRECY("保密");

    private final String value;

    SexType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
