package com.github.lorenj.wordtint.enums;


import com.github.lorenj.wordtint.R;

/**
 * @author cnsukidayo
 * @date 2023/2/9 20:14
 */
public enum CreditOrder {
    ORDERLY(R.string.orderly), DISORDER(R.string.disorder),LEXICOGRAPHIC(R.string.lexicographic_order);
    private int modeID;

    CreditOrder(int modeID) {
        this.modeID = modeID;
    }

    public int getModeID() {
        return modeID;
    }
}
