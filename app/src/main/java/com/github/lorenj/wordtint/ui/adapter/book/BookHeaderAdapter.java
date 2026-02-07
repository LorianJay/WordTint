package com.github.lorenj.wordtint.ui.adapter.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;

import java.util.List;
import java.util.stream.Collectors;

public class BookHeaderAdapter extends RecyclerView.Adapter<BookHeaderAdapter.BookHeaderViewHolder>
        implements RecyclerViewAdapterItemChange<WordBookWithSectionVO> {

    private final Context context;
    private WordBookWithSectionVO wordBookWithSectionVO;
    /**
     * 标签共享缓存
     */
    private final RecyclerView.RecycledViewPool tagSelectionPool;
    private final ConcatAdapter bookListAdapter;
    private final BookSectionAdapter bookSectionAdapter;

    public BookHeaderAdapter(Context context,
                             ConcatAdapter bookListAdapter,
                             BookViewModel bookViewModel,
                             RecyclerView.RecycledViewPool tagSelectionPool) {
        this.context = context;
        this.bookListAdapter = bookListAdapter;
        this.bookSectionAdapter = new BookSectionAdapter(context, bookViewModel);
        this.tagSelectionPool = tagSelectionPool;
    }

    @NonNull
    @Override
    public BookHeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookHeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_parent, parent, false), context);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHeaderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bookName.setText(wordBookWithSectionVO.wordBookEntity.name);
        holder.count.setText(String.valueOf(wordBookWithSectionVO.wordBookSectionVOList.size()));
        holder.bookTagSelection.setRecycledViewPool(tagSelectionPool);
        // 重新加载所有的子章节
        // 标签选择初始化-保证顺序的一致性
        List<MarkColor> allSectionTag = wordBookWithSectionVO.wordBookSectionVOList
                .stream()
                .filter(wordBookSectionEntityVO -> wordBookSectionEntityVO.tagColor != null)
                .map(wordBookSectionEntityVO -> wordBookSectionEntityVO.tagColor)
                .distinct()
                .collect(Collectors.toList());
        holder.tagSelectionListAdapter.replaceAll(allSectionTag);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return BookListAdapter.BOOK;
    }

    @Override
    public void addItem(WordBookWithSectionVO item) {
        this.wordBookWithSectionVO = item;
    }

    public static class BookHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RelativeLayout bookItem;
        private final TextView bookName, count;
        private final RecyclerView bookTagSelection;
        private TagSelectionListAdapter tagSelectionListAdapter;

        public BookHeaderViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.bookName = itemView.findViewById(R.id.tv_item_book);
            this.count = itemView.findViewById(R.id.tv_item_book_count);
            this.bookItem = itemView.findViewById(R.id.rl_item_book);
            this.bookTagSelection = itemView.findViewById(R.id.rv_book_tag_selection);

            bookItem.setOnClickListener(this);
            LinearLayoutManager tagSelectionLm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            bookTagSelection.setLayoutManager(tagSelectionLm);
            // 当标签选择列表点击了一个标签后会回调,这里用回调而不是viewModel
            tagSelectionListAdapter = new TagSelectionListAdapter(context, markColor -> {
                BookHeaderAdapter bookHeaderAdapter = (BookHeaderAdapter) getBindingAdapter();
                if (bookHeaderAdapter == null) return;
                bookHeaderAdapter.bookSectionAdapter.batchSelectSection(markColor);
            });
            bookTagSelection.setAdapter(tagSelectionListAdapter);
        }

        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.rl_item_book) {
                int position = getBindingAdapterPosition();
                BookHeaderAdapter bookHeaderAdapter = (BookHeaderAdapter) getBindingAdapter();
                if (bookHeaderAdapter == null) return;
                WordBookWithSectionVO wordBookWithSection = bookHeaderAdapter.wordBookWithSectionVO;
                // 如果当前是折叠就添加,否则就删除
                if (wordBookWithSection.folded) {
                    int headerIndex = bookHeaderAdapter.bookListAdapter.getAdapters().indexOf(bookHeaderAdapter);
                    bookHeaderAdapter.bookSectionAdapter.replaceAll(bookHeaderAdapter.wordBookWithSectionVO.wordBookSectionVOList);
                    bookHeaderAdapter.bookListAdapter.addAdapter(headerIndex + 1, bookHeaderAdapter.bookSectionAdapter);
                } else {
                    bookHeaderAdapter.bookListAdapter.removeAdapter(bookHeaderAdapter.bookSectionAdapter);
                }
                wordBookWithSection.folded = !wordBookWithSection.folded;
            }
        }
    }

}
