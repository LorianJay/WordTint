package com.github.lorenj.wordtint.utils;

import android.content.res.Resources;

public class DPUtils {
    /**
     * dp转px的方法
     *
     * @param i 输入i的单位为dp
     * @return 输出对应的px
     */
    public static int dp2px(int i) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * i + 0.5f);
    }
}
