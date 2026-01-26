package com.github.lorenj.wordtint.enums;

/**
 * @author cnsukidayo
 * @date 2023/1/4 18:42
 */
public enum WordFunctionState {
    NONE(""),
    SHUFFLE("当前处于按色打乱模式,请先点击按色打乱按钮退出当前模式."),
    RANGE("当前处于区间重背模式,请先点击区间重背按钮退出当前模式.");
    private final String information;

    WordFunctionState(String information) {
        this.information = information;
    }

    public String getInfo() {
        return this.information;
    }
}
