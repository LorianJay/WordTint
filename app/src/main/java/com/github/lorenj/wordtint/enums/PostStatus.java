package com.github.lorenj.wordtint.enums;


/**
 * @author sukidayo
 * @date 2023/9/14 11:38
 */
public enum PostStatus {

    TO_CHECK("待审核"),
    PUBLISH_FAIL("发布失败"),
    SUCCESS("发布成功"),
    PRIVATE("私密"),
    FREEZE("冻结");

    private final String value;

    PostStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
