package com.github.lorenj.wordtint.entity;

import androidx.annotation.IntRange;

/**
 * 简单的用户信息数据类
 *
 * @author cnsukidayo
 * @date 2023/2/2 20:58
 */
public class UserInfo {

    private int UserFacePathID;
    private String userName;
    private int userLevel;
    private int vipLevel;
    private int money;

    public UserInfo() {
    }

    public UserInfo(int userFacePathID, String userName, int userLevel, int vipLevel, int money) {
        UserFacePathID = userFacePathID;
        this.userName = userName;
        this.userLevel = userLevel;
        this.vipLevel = vipLevel;
        this.money = money;
    }

    public int getUserFacePathID() {
        return UserFacePathID;
    }

    public void setUserFacePathID(int userFacePathID) {
        UserFacePathID = userFacePathID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(@IntRange(from = 1, to = 6) int userLevel) {
        this.userLevel = userLevel;
    }

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(@IntRange(from = 1, to = 6) int vipLevel) {
        this.vipLevel = vipLevel;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
