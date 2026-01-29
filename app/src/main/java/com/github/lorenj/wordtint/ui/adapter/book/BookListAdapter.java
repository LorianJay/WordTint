package com.github.lorenj.wordtint.ui.adapter.book;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.RecyclerViewHolder>
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
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_parent, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WordBookWithSectionVO wordBookWithSectionVO = wordBookEntityList.get(position);
        holder.bookName.setText(wordBookWithSectionVO.wordBookEntity.name);
        holder.count.setText(String.valueOf(wordBookWithSectionVO.wordBookSectionEntityVOList.size()));
        // 章节初始化
        LinearLayoutManager bookSectionLm = new LinearLayoutManager(context);
        holder.bookSection.setLayoutManager(bookSectionLm);
        holder.bookSectionListAdapter = new BookSectionListAdapter(context, bookViewModel);
        holder.bookSection.setAdapter(holder.bookSectionListAdapter);
        holder.bookSection.setRecycledViewPool(bookSectionPool);
        holder.bookSectionListAdapter.replaceAll(wordBookWithSectionVO.wordBookSectionEntityVOList);
        holder.bookSection.setVisibility(wordBookWithSectionVO.folded ? View.GONE : View.VISIBLE);
        // 标签选择初始化-保证顺序的一致性
        Set<MarkColor> distinct = new HashSet<>();
        List<MarkColor> allSectionTag = wordBookWithSectionVO.wordBookSectionEntityVOList.stream()
                .filter(wordBookSectionEntityVO -> {
                    if (wordBookSectionEntityVO.tagColor != null && distinct.contains(wordBookSectionEntityVO.tagColor)) {
                        return false;
                    } else if (wordBookSectionEntityVO.tagColor != null) {
                        distinct.add(wordBookSectionEntityVO.tagColor);
                        return true;
                    }
                    return false;
                })
                .map(wordBookSectionEntityVO -> wordBookSectionEntityVO.tagColor)
                .collect(Collectors.toList());
        LinearLayoutManager tagSelectionLm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.bookTagSelection.setLayoutManager(tagSelectionLm);
        // 当标签选择列表点击了一个标签后会回调,这里用回调而不是viewModel
        RecycleViewItemClickCallBack<MarkColor> tagSelectionCallBak = selectMarkColor -> holder.bookSectionListAdapter.batchSelectSection(selectMarkColor);
        holder.tagSelectionListAdapter = new TagSelectionListAdapter(context, tagSelectionCallBak);
        holder.bookTagSelection.setAdapter(holder.tagSelectionListAdapter);
        holder.bookTagSelection.setRecycledViewPool(tagSelectionPool);
        holder.tagSelectionListAdapter.replaceAll(allSectionTag);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position, @NonNull List<Object> payloads) {
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
        notifyItemRangeChanged(0, wordBookEntityCollection.size());
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RelativeLayout bookItem;
        private final TextView bookName, count;
        private final RecyclerView bookSection, bookTagSelection;
        private BookSectionListAdapter bookSectionListAdapter;
        private TagSelectionListAdapter tagSelectionListAdapter;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.bookName = itemView.findViewById(R.id.tv_item_book);
            this.bookSection = itemView.findViewById(R.id.rv_book_section_child);
            this.count = itemView.findViewById(R.id.tv_item_book_count);
            this.bookItem = itemView.findViewById(R.id.rl_item_book);
            this.bookTagSelection = itemView.findViewById(R.id.rv_book_tag_selection);

            bookItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int vId = v.getId();
            if (vId == R.id.rl_item_book) {
                int position = getAdapterPosition();
                WordBookWithSectionVO wordBookWithSectionVO = wordBookEntityList.get(position);
                wordBookWithSectionVO.folded = !wordBookWithSectionVO.folded;
                notifyItemChanged(position, Item.CLICK_BOOK);
            }
        }
    }

    private enum Item {
        CLICK_BOOK
    }

}
