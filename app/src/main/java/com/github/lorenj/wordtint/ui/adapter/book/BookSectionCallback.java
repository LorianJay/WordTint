package com.github.lorenj.wordtint.ui.adapter.book;

import androidx.recyclerview.widget.DiffUtil;

import com.github.lorenj.wordtint.database.vo.WordBookSectionEntityVO;

import java.util.List;

public class BookSectionCallback extends DiffUtil.Callback {
    private final List<WordBookSectionEntityVO> oldList;
    private final List<WordBookSectionEntityVO> newList;

    public BookSectionCallback(List<WordBookSectionEntityVO> oldList, List<WordBookSectionEntityVO> newList) {
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
        return oldList.get(oldPos).wordBookSectionEntity.bookId == newList.get(newPos).wordBookSectionEntity.bookId;
    }

    @Override
    public boolean areContentsTheSame(int oldPos, int newPos) {
        return oldList.get(oldPos).equals(newList.get(newPos));
    }
}