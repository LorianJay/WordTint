package com.github.lorenj.wordtint.ui.adapter.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.entity.WordBookWithSection;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.RecyclerViewHolder>
        implements RecyclerViewAdapterItemChange<WordBookWithSection> {

    private final Context context;
    private final List<WordBookWithSection> wordBookEntityList = new ArrayList<>();
    /**
     * 二级列表贡献缓存
     */
    private final RecyclerView.RecycledViewPool sharedPool;

    public BookListAdapter(Context context) {
        this.context = context;
        sharedPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_parent, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WordBookWithSection wordBook = wordBookEntityList.get(position);
        holder.bookName.setText(wordBook.wordBookEntity.name);
        holder.count.setText(String.valueOf(wordBook.wordBookSectionEntityList.size()));
        LinearLayoutManager lm = new LinearLayoutManager(holder.itemView.getContext(), RecyclerView.VERTICAL, false);
        holder.bookSection.setLayoutManager(lm);
        BookSectionListAdapter bookSectionListAdapter = new BookSectionListAdapter(holder.itemView.getContext());
        holder.bookSection.setAdapter(bookSectionListAdapter);
        holder.bookSection.setRecycledViewPool(sharedPool);
        bookSectionListAdapter.replaceAll(wordBook.wordBookSectionEntityList);
    }

    @Override
    public int getItemCount() {
        return wordBookEntityList.size();
    }

    @Override
    public void addItem(WordBookWithSection item) {
    }

    @Override
    public void removeItem(WordBookWithSection item) {

    }

    @Override
    public void replaceAll(Collection<WordBookWithSection> wordBookEntityCollection) {
        wordBookEntityList.clear();
        wordBookEntityList.addAll(wordBookEntityCollection);
        notifyItemRangeChanged(0, wordBookEntityCollection.size());
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView bookName, count;
        private RecyclerView bookSection;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.bookName = itemView.findViewById(R.id.tv_item_book);
            this.bookSection = itemView.findViewById(R.id.rv_book_section_child);
            this.count = itemView.findViewById(R.id.tv_item_book_count);
        }

    }


}
