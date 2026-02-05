package com.github.lorenj.wordtint.ui.adapter.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.vo.WordBookSectionEntityVO;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.viewmodel.BookViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BookSectionListAdapter extends RecyclerView.Adapter<BookSectionListAdapter.BookSecionViewHolder>
        implements RecyclerViewAdapterItemChange<WordBookSectionEntityVO> {

    private final Context context;
    private final List<WordBookSectionEntityVO> wordBookSectionEntityList = new ArrayList<>();
    private final BookViewModel bookViewModel;

    public BookSectionListAdapter(Context context, BookViewModel bookViewModel) {
        this.context = context;
        this.bookViewModel = bookViewModel;
    }

    @NonNull
    @Override
    public BookSecionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookSecionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_section, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookSecionViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WordBookSectionEntityVO wordBookSectionEntity = wordBookSectionEntityList.get(position);
        holder.sectionTextView.setText(wordBookSectionEntity.wordBookSectionEntity.name);
        holder.elementCount.setText(String.valueOf(wordBookSectionEntity.elementCount));
        if (wordBookSectionEntity.selection) {
            holder.bookSectionSelection.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_plan));
        } else {
            holder.bookSectionSelection.setImageDrawable(null);
        }
        // tag标签颜色
        if (wordBookSectionEntity.tagColor != null) {
            Drawable drawable = holder.bookTag.getDrawable();
            drawable = drawable.mutate();
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, wordBookSectionEntity.tagColor.getMapColorID()));
        }
        Log.d("position:", String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return wordBookSectionEntityList.size();
    }

    @Override
    public void replaceAll(Collection<WordBookSectionEntityVO> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BookSectionCallback(this.wordBookSectionEntityList, new ArrayList<>(newData)));
        this.wordBookSectionEntityList.clear();
        this.wordBookSectionEntityList.addAll(newData);
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * 批量选中某个标签
     *
     * @param selectMarkColor 选中的标签
     */
    public void batchSelectSection(MarkColor selectMarkColor) {
        for (WordBookSectionEntityVO wordBookSectionEntityVO : wordBookSectionEntityList) {
            if (wordBookSectionEntityVO.tagColor == selectMarkColor) {
                bookViewModel.selectSection(wordBookSectionEntityVO.wordBookSectionEntity);
                wordBookSectionEntityVO.selection = !wordBookSectionEntityVO.selection;
            }
        }
        notifyItemRangeChanged(0, wordBookSectionEntityList.size());
    }

    public static class BookSecionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final View itemView;
        private final TextView sectionTextView;
        private final TextView elementCount;
        private final ImageView bookSectionSelection, bookTag;

        public BookSecionViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.sectionTextView = itemView.findViewById(R.id.tv_item_book_section);
            this.bookSectionSelection = itemView.findViewById(R.id.iv_item_book_section_selection);
            this.elementCount = itemView.findViewById(R.id.tv_item_book_section_count);
            this.bookTag = itemView.findViewById(R.id.iv_item_book_tag);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;
            BookSectionListAdapter bindingAdapter = (BookSectionListAdapter) getBindingAdapter();
            if (bindingAdapter == null) return;
            // 传递选中的id数据
            WordBookSectionEntityVO selectWordBookSectionEntityVO = bindingAdapter.wordBookSectionEntityList.get(position);
            bindingAdapter.bookViewModel.selectSection(selectWordBookSectionEntityVO.wordBookSectionEntity);
            selectWordBookSectionEntityVO.selection = !selectWordBookSectionEntityVO.selection;
            bindingAdapter.notifyItemChanged(position);
        }
    }

}
