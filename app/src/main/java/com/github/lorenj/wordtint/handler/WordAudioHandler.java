package com.github.lorenj.wordtint.handler;

import android.content.Context;

import com.github.lorenj.wordtint.database.vo.FunctionWordVO;

/**
 * 单词音频播放器
 *
 * @author cnsukidayo
 * @date 2026/4/17 11:49
 */
public interface WordAudioHandler {

    /**
     * 播放一个单词的音频
     *
     * @param functionWordVO 单词对象
     * @param context        上下文
     */
    void playWordAudio(FunctionWordVO functionWordVO, Context context);

}
