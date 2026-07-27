package com.github.lorenj.wordtint.ui.adapter.analysis;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.lorenj.wordtint.enums.ChartTime;

/**
 * @author lorianjay
 * @date 2026/7/27 18:52
 */
public class ChartTimeViewModel extends ViewModel {

    private final MutableLiveData<ChartTimeRange> selectChartTime =
            new MutableLiveData<>();

    public MutableLiveData<ChartTimeRange> getSelectChartTime() {
        return selectChartTime;
    }
}
