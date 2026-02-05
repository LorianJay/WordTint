package com.github.lorenj.wordtint.enums;

import com.github.lorenj.wordtint.R;

/**
 * @author cnsukidayo
 * @date 2023/1/5 15:40
 */
public enum ReciteMode {
    /**
     * 英译中(有音频)
     */
    ENGLISH_TRANSLATION_CHINESE_HEARING(R.string.english_translation_chinese_hearing),
    /**
     * 英译中无音频
     */
    ENGLISH_TRANSLATION_CHINESE_NO_HEARING(R.string.english_translation_chinese_no_hearing),
    /**
     * 中译英
     */
    CHINESE_TRANSLATION_ENGLISH(R.string.chinese_translation_english),
    /**
     * 听力模式
     */
    LISTENING(R.string.listening_write_mode),
    /**
     * 纯背诵
     */
    ONLY_RECITE(R.string.only_recite);


    /**
     * 当前枚举对应的字符串选项id
     */
    private int stringId;

    ReciteMode(int stringId) {
        this.stringId = stringId;
    }

    public int getStringId() {
        return stringId;
    }
}
