package com.github.lorenj.wordtint.entity.dto;

/**
 * @author sukidayo
 * @date 2023/7/27 10:18
 */
public class UserProfileVO extends UserProfileDTO {

    private String sexString;

    public UserProfileVO() {
    }

    public String getSexString() {
        return sexString;
    }

    public void setSexString(String sexString) {
        this.sexString = sexString;
    }
}
