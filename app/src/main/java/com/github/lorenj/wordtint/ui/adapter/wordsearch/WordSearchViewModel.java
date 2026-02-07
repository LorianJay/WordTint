package com.github.lorenj.wordtint.ui.adapter.wordsearch;

import androidx.lifecycle.MutableLiveData;

import com.github.lorenj.wordtint.database.entity.WordSearchEntity;

/**
 * @author cnsukidayo
 * @date 2026/2/4 19:42
 */
public class WordSearchViewModel {

    /**
     * 当前搜索选中的单词对象
     */
    private final MutableLiveData<WordSearchEntity> currentSelectWord =
            new MutableLiveData<>();

    public MutableLiveData<WordSearchEntity> getCurrentSelectWord() {
        return currentSelectWord;
    }
}
