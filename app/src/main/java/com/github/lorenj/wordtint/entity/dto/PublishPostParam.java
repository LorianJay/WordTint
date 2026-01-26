package com.github.lorenj.wordtint.entity.dto;

import java.io.File;

/**
 * @author sukidayo
 * @date 2023/9/12 18:17
 */
public class PublishPostParam {

    private String title;

    private File markDownFile;
    private File coverFile;

    public PublishPostParam() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public File getMarkDownFile() {
        return markDownFile;
    }

    public void setMarkDownFile(File markDownFile) {
        this.markDownFile = markDownFile;
    }

    public File getCoverFile() {
        return coverFile;
    }

    public void setCoverFile(File coverFile) {
        this.coverFile = coverFile;
    }
}
