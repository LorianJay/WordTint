package com.github.lorenj.wordtint.ui.adapter.analysis;

import androidx.recyclerview.widget.DiffUtil;

import com.github.lorenj.wordtint.database.vo.WordMarkCountVO;
import com.github.lorenj.wordtint.ui.adapter.book.WordBookSectionVO;

import java.util.List;

public class RankWordDiffCallback extends DiffUtil.Callback {
    private final List<WordMarkCountVO> oldList;
    private final List<WordMarkCountVO> newList;

    public RankWordDiffCallback(List<WordMarkCountVO> oldList, List<WordMarkCountVO> newList) {
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
        return oldList.get(oldPos).getMarkWordId() == newList.get(newPos).getMarkWordId();
    }

    @Override
    public boolean areContentsTheSame(int oldPos, int newPos) {
        return oldList.get(oldPos).equals(newList.get(newPos));
    }
}