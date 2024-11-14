package com.gitee.cnsukidayo.anylanguageword.context.pathsystem.document;

import android.os.Environment;

import java.io.File;

/**
 * @author cnsukidayo
 * @date 2023/2/6 11:39
 */
public enum WordContextPath {
    WORD_CONTEXT_ROOT_PATH(null),
    /**
     * document/wordContext/wordList/
     */
    WORD_LIST("wordList" + File.separator),
    /**
     * document/wordContext/wordDict/
     */
    WORD_DICT("wordDict" + File.separator),
    /**
     * document/wordContext/wordHistory/
     */
    WORD_HISTORY("wordHistory" + File.separator),
    /**
     * document/wordContext/word_audio/
     */
    WORD_AUDIO("word_audio" + File.separator),
    /**
     * document/wordContext/wordStar/wordStar.json
     */
    WORD_STAR("wordStar" + File.separator + "wordStar.json");

    private final String userInfoRootPath = Environment.DIRECTORY_DOCUMENTS + File.separator + "wordContext" + File.separator;
    private final String path;

    WordContextPath(String path) {
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
