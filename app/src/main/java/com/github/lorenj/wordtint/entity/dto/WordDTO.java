package com.github.lorenj.wordtint.entity.dto;

public class WordDTO {
    private Long groupFlag;

    private Long wordId;

    private Long wordStructureId;

    private String value;

    private Long groupId;

    public WordDTO() {
    }

    public Long getGroupFlag() {
        return groupFlag;
    }

    public void setGroupFlag(Long groupFlag) {
        this.groupFlag = groupFlag;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public Long getWordStructureId() {
        return wordStructureId;
    }

    public void setWordStructureId(Long wordStructureId) {
        this.wordStructureId = wordStructureId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
