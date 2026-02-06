package com.github.lorenj.wordtint.ui.adapter.book;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class BookSectionDiffCallback extends DiffUtil.Callback {
    private final List<WordBookSectionVO> oldList;
    private final List<WordBookSectionVO> newList;

    public BookSectionDiffCallback(List<WordBookSectionVO> oldList, List<WordBookSectionVO> newList) {
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
        return oldList.get(oldPos).wordBookSectionEntity.id == newList.get(newPos).wordBookSectionEntity.id;
    }

    @Override
    public boolean areContentsTheSame(int oldPos, int newPos) {
        return oldList.get(oldPos).equals(newList.get(newPos));
    }
}