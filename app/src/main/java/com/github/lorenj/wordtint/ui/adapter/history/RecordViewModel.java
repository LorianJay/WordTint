package com.github.lorenj.wordtint.ui.adapter.history;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.lorenj.wordtint.database.entity.ReciteRecordEntity;
import com.github.lorenj.wordtint.database.vo.ReciteRecordVO;

public class RecordViewModel extends ViewModel {

    /**
     * 所有当前用户选择的背诵记录
     */
    private final MutableLiveData<ReciteRecordVO> selectedRecord =
            new MutableLiveData<>();


    /**
     * 当前要删除的背诵记录
     */
    private final MutableLiveData<ReciteRecordEntity> removeRecord =
            new MutableLiveData<>();

    public MutableLiveData<ReciteRecordVO> getSelectedRecord() {
        return selectedRecord;
    }

    public MutableLiveData<ReciteRecordEntity> getRemoveRecord() {
        return removeRecord;
    }
}
