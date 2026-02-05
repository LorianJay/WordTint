package com.github.lorenj.wordtint.ui.adapter.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.vo.WordBookWithSectionVO;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.github.lorenj.wordtint.ui.viewmodel.BookViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder>
        implements RecyclerViewAdapterItemChange<WordBookWithSectionVO> {

    private final Context context;
    private final List<WordBookWithSectionVO> wordBookEntityList = new ArrayList<>();
    private final BookViewModel bookViewModel;
    /**
     * 二级列表贡献缓存
     */
    private final RecyclerView.RecycledViewPool bookSectionPool;
    private final RecyclerView.RecycledViewPool tagSelectionPool;

    public BookListAdapter(Context context, BookViewModel bookViewModel) {
        this.context = context;
        this.bookViewModel = bookViewModel;
        bookSectionPool = new RecyclerView.RecycledViewPool();
        tagSelectionPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_parent, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WordBookWithSectionVO wordBookWithSectionVO = wordBookEntityList.get(position);
        holder.bookName.setText(wordBookWithSectionVO.wordBookEntity.name);
        holder.count.setText(String.valueOf(wordBookWithSectionVO.wordBookSectionEntityVOList.size()));
        // 重新加载所有的子章节
        holder.bookSectionListAdapter.replaceAll(wordBookWithSectionVO.wordBookSectionEntityVOList);
        holder.bookSection.setVisibility(wordBookWithSectionVO.folded ? View.GONE : View.VISIBLE);
        // 标签选择初始化-保证顺序的一致性
        List<MarkColor> allSectionTag = wordBookWithSectionVO.wordBookSectionEntityVOList
                .stream()
                .filter(wordBookSectionEntityVO -> wordBookSectionEntityVO.tagColor != null)
                .map(wordBookSectionEntityVO -> wordBookSectionEntityVO.tagColor)
                .distinct()
                .collect(Collectors.toList());
        holder.tagSelectionListAdapter.replaceAll(allSectionTag);
        holder.bookListAdapter = this;
        Log.d("book-list:", String.valueOf(position));
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) super.onBindViewHolder(holder, position, payloads);
        for (Object payload : payloads) {
            if (payload == Item.CLICK_BOOK) {
                WordBookWithSectionVO wordBookWithSectionVO = wordBookEntityList.get(position);
                holder.bookSection.setVisibility(wordBookWithSectionVO.folded ? View.GONE : View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return wordBookEntityList.size();
    }


    @Override
    public void replaceAll(Collection<WordBookWithSectionVO> wordBookEntityCollection) {
        wordBookEntityList.clear();
        wordBookEntityList.addAll(wordBookEntityCollection);
        notifyItemRangeInserted(0, wordBookEntityCollection.size());
    }

    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RelativeLayout bookItem;
        private final TextView bookName, count;
        private final RecyclerView bookSection, bookTagSelection;
        private BookSectionListAdapter bookSectionListAdapter;
        private TagSelectionListAdapter tagSelectionListAdapter;
        private BookListAdapter bookListAdapter;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            this.bookName = itemView.findViewById(R.id.tv_item_book);
            this.bookSection = itemView.findViewById(R.id.rv_book_section_child);
            this.count = itemView.findViewById(R.id.tv_item_book_count);
            this.bookItem = itemView.findViewById(R.id.rl_item_book);
            this.bookTagSelection = itemView.findViewById(R.id.rv_book_tag_selection);

            bookItem.setOnClickListener(this);
            LinearLayoutManager bookSectionLM = new LinearLayoutManager(context);
            bookSection.setLayoutManager(bookSectionLM);
            bookSectionListAdapter = new BookSectionListAdapter(context, bookViewModel);
            bookSection.setAdapter(bookSectionListAdapter);
            bookSection.setRecycledViewPool(bookSectionPool);

            LinearLayoutManager tagSelectionLm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            bookTagSelection.setLayoutManager(tagSelectionLm);
            // 当标签选择列表点击了一个标签后会回调,这里用回调而不是viewModel
            RecycleViewItemClickCallBack<MarkColor> tagSelectionCallBak = selectMarkColor -> bookSectionListAdapter.batchSelectSection(selectMarkColor);
            tagSelectionListAdapter = new TagSelectionListAdapter(context, tagSelectionCallBak);
            bookTagSelection.setAdapter(tagSelectionListAdapter);
            bookTagSelection.setRecycledViewPool(tagSelectionPool);

        }

        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.rl_item_book) {
                int position = getBindingAdapterPosition();
                WordBookWithSectionVO wordBookWithSectionVO = bookListAdapter.wordBookEntityList.get(position);
                wordBookWithSectionVO.folded = !wordBookWithSectionVO.folded;
                bookListAdapter.notifyItemChanged(position, Item.CLICK_BOOK);
            }
        }
    }

    private enum Item {
        CLICK_BOOK
    }

}
