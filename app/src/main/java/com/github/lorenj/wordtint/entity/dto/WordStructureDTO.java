package com.github.lorenj.wordtint.entity.dto;

/**
 * @author sukidayo
 * @date 2023/7/30 14:36
 */
public class WordStructureDTO {

    private Long id;

    private Long languageId;

    private String field;

    private String fieldTranslation;


    public WordStructureDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFieldTranslation() {
        return fieldTranslation;
    }

    public void setFieldTranslation(String fieldTranslation) {
        this.fieldTranslation = fieldTranslation;
    }
}
