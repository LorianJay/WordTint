package com.github.lorenj.wordtint.ui.adapter.book;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class BookDiffCallback extends DiffUtil.Callback {
    private final List<BaseBookItem> oldList;
    private final List<BaseBookItem> newList;

    public BookDiffCallback(List<BaseBookItem> oldList, List<BaseBookItem> newList) {
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
        BaseBookItem oldBaseBookItem = oldList.get(oldPos);
        BaseBookItem newBaseBookItem = newList.get(newPos);
        if (oldBaseBookItem.getItemType() != newBaseBookItem.getItemType()) return false;
        int currentType = oldBaseBookItem.getItemType();
        if (currentType == BaseBookItem.BOOK) {
            return ((WordBookWithSectionVO) oldBaseBookItem).wordBookEntity.id == ((WordBookWithSectionVO) newBaseBookItem).wordBookEntity.id;
        }else {
            return ((WordBookSectionVO) oldBaseBookItem).wordBookSectionEntity.id
                    == ((WordBookSectionVO) newBaseBookItem).wordBookSectionEntity.id;
        }
    }

    @Override
    public boolean areContentsTheSame(int oldPos, int newPos) {
        return oldList.get(oldPos).equals(newList.get(newPos));
    }
}