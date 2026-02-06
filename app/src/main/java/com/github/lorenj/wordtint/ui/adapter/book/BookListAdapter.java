package com.github.lorenj.wordtint.ui.adapter.book;

import android.content.Context;

import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.ui.viewmodel.BookViewModel;

import java.util.ArrayList;
import java.util.List;

public class BookListAdapter {

    private final Context context;
    private final ConcatAdapter globalAdapter;
    private final List<WordBookWithSectionVO> wordBookWithSectionVOList = new ArrayList<>();
    private final BookViewModel bookViewModel;
    /**
     * 标签共享的缓存
     */
    private final RecyclerView.RecycledViewPool tagSelectionPool = new RecyclerView.RecycledViewPool();
    public static final int BOOK = 1;
    public static final int BOOK_SECTION = 2;

    public BookListAdapter(Context context,
                           BookViewModel bookViewModel) {
        this.context = context;
        this.bookViewModel = bookViewModel;
        ConcatAdapter.Config config = new ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(false)
                .build();
        this.globalAdapter = new ConcatAdapter(config);
    }

    /**
     * 替换所有的书本章节信息
     */
    public ConcatAdapter replaceAll(List<WordBookWithSectionVO> wordBookWithSectionVOList) {
        this.wordBookWithSectionVOList.clear();
        this.wordBookWithSectionVOList.addAll(wordBookWithSectionVOList);
        for (WordBookWithSectionVO wordBookWithSectionVO : wordBookWithSectionVOList) {
            BookHeaderAdapter headerAdapter = new BookHeaderAdapter(context,
                    globalAdapter,
                    bookViewModel,
                    tagSelectionPool);
            headerAdapter.addItem(wordBookWithSectionVO);
            globalAdapter.addAdapter(headerAdapter);
        }
        return globalAdapter;
    }

}
