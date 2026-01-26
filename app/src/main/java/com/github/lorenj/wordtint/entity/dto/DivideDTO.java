package com.github.lorenj.wordtint.entity.dto;


import com.github.lorenj.wordtint.enums.DivideType;

import java.util.List;

/**
 * @author sukidayo
 * @date 2023/7/28 16:30
 */
public class DivideDTO  {

    private Long id;

    private Long languageId;

    private String name;

    private Long wordId;

    private Long elementCount;

    private DivideType divideType;

    private List<DivideDTO> childDivideDTO;

    public DivideDTO() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public Long getElementCount() {
        return elementCount;
    }

    public void setElementCount(Long elementCount) {
        this.elementCount = elementCount;
    }

    public DivideType getDivideType() {
        return divideType;
    }

    public void setDivideType(DivideType divideType) {
        this.divideType = divideType;
    }

    public List<DivideDTO> getChildDivideDTO() {
        return childDivideDTO;
    }

    public void setChildDivideDTO(List<DivideDTO> childDivideDTO) {
        this.childDivideDTO = childDivideDTO;
    }
}
