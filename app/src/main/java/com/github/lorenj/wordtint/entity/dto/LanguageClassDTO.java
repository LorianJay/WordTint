package com.github.lorenj.wordtint.entity.dto;

/**
 * @author sukidayo
 * @date 2023/7/28 15:06
 */
public class LanguageClassDTO  {

    private Long id;

    private String language;

    private String iconUrl;

    private Integer orderInfo;

    public LanguageClassDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Integer getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(Integer orderInfo) {
        this.orderInfo = orderInfo;
    }
}
