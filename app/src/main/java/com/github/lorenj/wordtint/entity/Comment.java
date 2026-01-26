package com.github.lorenj.wordtint.entity;

/**
 * @author cnsukidayo
 * @date 2023/2/13 18:57
 */
public class Comment {
    private int face;
    private String name;
    private int level;
    private String date;
    private String commentContext;
    private int praise;

    public Comment() {
    }

    public Comment(int face, String name, int level, String date, String commentContext, int praise) {
        this.face = face;
        this.name = name;
        this.level = level;
        this.date = date;
        this.commentContext = commentContext;
        this.praise = praise;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommentContext() {
        return commentContext;
    }

    public void setCommentContext(String commentContext) {
        this.commentContext = commentContext;
    }

    public int getPraise() {
        return praise;
    }

    public void setPraise(int praise) {
        this.praise = praise;
    }
}
