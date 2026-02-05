package com.github.lorenj.wordtint.enums;


import com.github.lorenj.wordtint.R;

/**
 * @author cnsukidayo
 * @date 2023/2/9 20:14
 */
public enum ReciteOrder {
    /**
     * 有序
     */
    ORDERLY(R.string.orderly),
    /**
     * 无序
     */
    DISORDER(R.string.disorder),
    /**
     * 字典序
     */
    LEXICOGRAPHIC(R.string.lexicographic_order);

    private int stringId;

    ReciteOrder(int stringId) {
        this.stringId = stringId;
    }

    public int getStringId() {
        return stringId;
    }
}
