package com.github.lorenj.wordtint.utils;

/**
 * @author cnsukidayo
 * @date 2026/2/1 14:11
 */
public class MathUtils {
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
