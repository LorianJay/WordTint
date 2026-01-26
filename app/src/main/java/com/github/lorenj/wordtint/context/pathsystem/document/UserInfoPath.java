package com.github.lorenj.wordtint.context.pathsystem.document;

import android.os.Environment;

import java.io.File;

/**
 * @author cnsukidayo
 * @date 2023/2/6 11:39
 */
public enum UserInfoPath {
    USER_INFO_ROOT_PATH(null),
    USER_SETTINGS("userSettings.json"),
    USER_CREDIT_STYLE("userCreditStyle.json");

    private final String userInfoRootPath = Environment.DIRECTORY_DOCUMENTS + File.separator + "userInfo" + File.separator;
    private final String path;

    UserInfoPath(String path) {
        if (path == null) {
            this.path = userInfoRootPath;
        } else {
            this.path = userInfoRootPath + path;
        }
    }

    public String getPath() {
        return path;
    }
}
