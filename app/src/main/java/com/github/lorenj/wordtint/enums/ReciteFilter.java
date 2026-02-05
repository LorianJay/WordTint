package com.github.lorenj.wordtint.enums;

import com.github.lorenj.wordtint.R;

/**
 * 背诵内容
 *
 * @author cnsukidayo
 * @date 2023/2/9 20:16
 */
public enum ReciteFilter {
    /**
     * 过滤出所有单词
     */
    NO_FILER(R.string.no_filer),
    /**
     * 过滤出所有短语
     */
    PHRASE(R.string.concise_phrase);

    private int stringId;

    ReciteFilter(int stringId) {
        this.stringId = stringId;
    }

    public int getStringId() {
        return stringId;
    }
}
