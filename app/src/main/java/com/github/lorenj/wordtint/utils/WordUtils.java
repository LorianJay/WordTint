package com.github.lorenj.wordtint.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author lorianjay
 * @date 2026/8/4 20:04
 */
public class WordUtils {

    /**
     * 计算单词的id
     *
     * @param wordOrigin 单词原英文
     * @return 单词的id
     */
    public static long calculateWordId(String wordOrigin) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(wordOrigin.getBytes());
            byte[] digest = md.digest();
            // 取前4个字节生成一个int
            int id = ((digest[0] & 0xFF) << 24) |
                    ((digest[1] & 0xFF) << 16) |
                    ((digest[2] & 0xFF) << 8) |
                    (digest[3] & 0xFF);
            return id & 0x7FFFFFFFL;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


}
