package com.github.lorenj.wordtint.context.pathsystem.document;

import android.os.Environment;

import java.io.File;

/**
 * @author cnsukidayo
 * @date 2023/2/6 12:46
 */
public enum SystemFilePath {
    SYSTEM_FILE_ROOT_PATH(null),
    WELCOME_MESSAGE("welcomeMessage.md");

    private final String systemFilePath = Environment.DIRECTORY_DOCUMENTS + File.separator + "systemFile" + File.separator;
    private final String path;

    SystemFilePath(String path) {
        if (path == null) {
            this.path = systemFilePath;
        } else {
            this.path = systemFilePath + path;
        }
    }

    public String getPath() {
        return path;
    }
}
