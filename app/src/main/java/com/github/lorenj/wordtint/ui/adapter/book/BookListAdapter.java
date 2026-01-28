package com.github.lorenj.wordtint.ui.adapter.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.entity.WordBook;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.RecyclerViewHolder>
        implements RecyclerViewAdapterItemChange<WordBook> {

    private final Context context;
    private final List<WordBook> wordBookList = new ArrayList<>();

    public BookListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_parent, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WordBook wordBook = wordBookList.get(position);
        holder.bookName.setText(wordBook.name);
    }

    @Override
    public int getItemCount() {
        return wordBookList.size();
    }

    @Override
    public void addItem(WordBook item) {
    }

    @Override
    public void removeItem(WordBook item) {

    }

    @Override
    public void replaceAll(Collection<WordBook> wordBookCollection) {
        wordBookList.clear();
        wordBookList.addAll(wordBookCollection);
        notifyItemRangeChanged(0, wordBookCollection.size());
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView bookName;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.bookName = itemView.findViewById(R.id.tv_item_book);
        }

    }


}
