package com.github.lorenj.wordtint.handler.impl;

import android.content.Context;
import android.media.MediaPlayer;

import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.WordAudioHandler;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * @author cnsukidayo
 * @date 2026/4/17 11:57
 */
public class WordAudioHandlerImpl implements WordAudioHandler {
    /**
     * 单词音频播放器
     */
    private final MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public void playWordAudio(FunctionWordVO functionWordVO, Context context) {
        String wordOrigin = Optional.ofNullable(functionWordVO.getValue().get(WordStructure.WORD_ORIGIN))
                .orElse("");
        File audioFile = new File(context.getFilesDir() + "/audio", wordOrigin + ".mp3");
        if (!audioFile.exists()) {
            return;
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
