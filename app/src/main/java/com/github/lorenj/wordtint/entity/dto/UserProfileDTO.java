package com.github.lorenj.wordtint.entity.dto;


import com.github.lorenj.wordtint.enums.AccountCreateProgress;
import com.github.lorenj.wordtint.enums.SexType;

import java.time.LocalDate;

/**
 * @author sukidayo
 * @date 2023/7/23 15:25
 */
public class UserProfileDTO {

    private Long uuid;

    private String email;

    private String account;

    private String avatars;

    private String nick;

    private String describeInfo;

    private SexType sex;

    private LocalDate birthday;

    private String university;

    private Integer level;

    private AccountCreateProgress createProgress;

    public UserProfileDTO() {
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAvatars() {
        return avatars;
    }

    public void setAvatars(String avatars) {
        this.avatars = avatars;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getDescribeInfo() {
        return describeInfo;
    }

    public void setDescribeInfo(String describeInfo) {
        this.describeInfo = describeInfo;
    }

    public SexType getSex() {
        return sex;
    }

    public void setSex(SexType sex) {
        this.sex = sex;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public AccountCreateProgress getCreateProgress() {
        return createProgress;
    }

    public void setCreateProgress(AccountCreateProgress createProgress) {
        this.createProgress = createProgress;
    }
}
