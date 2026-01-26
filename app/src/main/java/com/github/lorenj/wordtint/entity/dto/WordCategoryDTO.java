package com.github.lorenj.wordtint.entity.dto;

/**
 * @author sukidayo
 * @date 2023/7/27 10:10
 */
public class WordCategoryDTO {

    private Long id;

    private String title;

    private String describeInfo;

    private Integer categoryOrder;

    public WordCategoryDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribeInfo() {
        return describeInfo;
    }

    public void setDescribeInfo(String describeInfo) {
        this.describeInfo = describeInfo;
    }

    public Integer getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(Integer categoryOrder) {
        this.categoryOrder = categoryOrder;
    }
}
