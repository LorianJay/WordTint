package com.github.lorenj.wordtint.enums;


import com.github.lorenj.wordtint.R;

/**
 * @author cnsukidayo
 * @date 2023/2/3 14:58
 */
public enum UserLevel {
    LEVEL_1(R.string.level_1),
    LEVEL_2(R.string.level_2),
    LEVEL_3(R.string.level_3),
    LEVEL_4(R.string.level_4),
    LEVEL_5(R.string.level_5),
    LEVEL_6(R.string.level_6);
    private final int levelDescribe;

    UserLevel(int levelDescribe) {
        this.levelDescribe = levelDescribe;
    }

    public int getLevelDescribe() {
        return levelDescribe;
    }
}
