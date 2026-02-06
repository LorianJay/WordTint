package com.github.lorenj.wordtint.ui.adapter.star;

import androidx.recyclerview.widget.DiffUtil;

import com.github.lorenj.wordtint.database.entity.WordStarWordIdEntity;

import java.util.List;

public class StarSectionDiffCallback extends DiffUtil.Callback {
    private final List<WordStarWordIdEntity> oldList;
    private final List<WordStarWordIdEntity> newList;

    public StarSectionDiffCallback(List<WordStarWordIdEntity> oldList, List<WordStarWordIdEntity> newList) {
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
        return oldList.get(oldPos).equals(newList.get(newPos));
    }
}