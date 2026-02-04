package com.github.lorenj.wordtint.ui.adapter.wordsearch;

import androidx.recyclerview.widget.DiffUtil;

import com.github.lorenj.wordtint.database.entity.WordSearchEntity;

import java.util.List;

public class WordDiffCallback extends DiffUtil.Callback {
    private final List<WordSearchEntity> oldList;
    private final List<WordSearchEntity> newList;

    public WordDiffCallback(List<WordSearchEntity> oldList, List<WordSearchEntity> newList) {
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
        return oldList.get(oldPos).wordId == newList.get(newPos).wordId;
    }

    @Override
    public boolean areContentsTheSame(int oldPos, int newPos) {
        return oldList.get(oldPos).wordOrigin.equals(newList.get(newPos).wordOrigin);
    }
}