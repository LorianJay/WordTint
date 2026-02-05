package com.github.lorenj.wordtint.ui.adapter.history;

import androidx.recyclerview.widget.DiffUtil;

import com.github.lorenj.wordtint.database.vo.ReciteRecordVO;

import java.util.List;

public class RecordDiffCallback extends DiffUtil.Callback {
    private final List<ReciteRecordVO> oldList;
    private final List<ReciteRecordVO> newList;

    public RecordDiffCallback(List<ReciteRecordVO> oldList, List<ReciteRecordVO> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldPos, int newPos) {
        return oldList.get(oldPos).getReciteRecordEntity().id == newList.get(newPos).getReciteRecordEntity().id;
    }

    @Override
    public boolean areContentsTheSame(int oldPos, int newPos) {
        return oldList.get(oldPos).getReciteRecordEntity().equals(newList.get(newPos).getReciteRecordEntity());
    }
}