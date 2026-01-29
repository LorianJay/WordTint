package com.github.lorenj.wordtint.ui.adapter.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.entity.WordBookSectionEntity;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BookSectionListAdapter extends RecyclerView.Adapter<BookSectionListAdapter.RecyclerViewHolder>
        implements RecyclerViewAdapterItemChange<WordBookSectionEntity> {

    private final Context context;
    private final List<WordBookSectionEntity> wordBookSectionEntityList = new ArrayList<>();

    /**
     * 设置点击子划分的回调事件
     */
    private RecycleViewItemClickCallBack<WordBookSectionEntity> recycleViewItemOnClickListener;

    public BookSectionListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_section, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WordBookSectionEntity wordBookSectionEntity = wordBookSectionEntityList.get(position);
        holder.sectionTextView.setText(wordBookSectionEntity.name);
    }

    @Override
    public int getItemCount() {
        return wordBookSectionEntityList.size();
    }

    public void setRecycleViewItemOnClickListener(RecycleViewItemClickCallBack<WordBookSectionEntity> recycleViewItemOnClickListener) {
        this.recycleViewItemOnClickListener = recycleViewItemOnClickListener;
    }

    @Override
    public void addItem(WordBookSectionEntity item) {
    }

    @Override
    public void removeItem(WordBookSectionEntity item) {

    }

    @Override
    public void replaceAll(Collection<WordBookSectionEntity> wordBookSectionEntityCollection) {
        wordBookSectionEntityList.clear();
        wordBookSectionEntityList.addAll(wordBookSectionEntityCollection);
        notifyItemRangeChanged(0, wordBookSectionEntityCollection.size());
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView sectionTextView;
        private TextView elementCount;
        private ImageView childDivideButton;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.sectionTextView = itemView.findViewById(R.id.tv_item_book_section);
            this.childDivideButton = itemView.findViewById(R.id.ib_item_book_section);
            this.elementCount = itemView.findViewById(R.id.tv_item_book_section_count);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
        }
    }


}
