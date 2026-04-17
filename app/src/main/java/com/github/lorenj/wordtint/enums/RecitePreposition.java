package com.github.lorenj.wordtint.enums;

import com.github.lorenj.wordtint.R;

/**
 * 介词的可见性
 *
 * @author cnsukidayo
 * @date 2026/4/17 10:47
 */
public enum RecitePreposition {
    /**
     * 可见
     */
    VISIBLE(R.string.visible),
    /**
     * 不可见
     */
    INVISIBLE(R.string.invisible);

    private int stringId;

    RecitePreposition(int stringId) {
        this.stringId = stringId;
    }

    public int getStringId() {
        return stringId;
    }

}
