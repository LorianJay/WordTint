package com.github.lorenj.wordtint.entity;

/**
 * @author cnsukidayo
 * @date 2023/2/2 10:24
 */
public class PostCover {

    private String imagePath;
    private String title;
    private String userFacePath;
    private long userID;
    private String userName;
    private int praiseCount;

    public PostCover() {
    }

    public PostCover(String imagePath, String title, String userFacePath, long userID, String userName, int praiseCount) {
        this.imagePath = imagePath;
        this.title = title;
        this.userFacePath = userFacePath;
        this.userID = userID;
        this.userName = userName;
        this.praiseCount = praiseCount;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserFacePath() {
        return userFacePath;
    }

    public void setUserFacePath(String userFacePath) {
        this.userFacePath = userFacePath;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }
}
