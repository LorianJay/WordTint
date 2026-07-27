package com.github.lorenj.wordtint.ui.adapter.analysis;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author lorianjay
 * @date 2026/7/27 18:52
 */
public class RankingCountViewModel extends ViewModel {

    private final MutableLiveData<Integer> selectCountEnum =
            new MutableLiveData<>();

    public MutableLiveData<Integer> getSelectCountEnum() {
        return selectCountEnum;
    }
}
