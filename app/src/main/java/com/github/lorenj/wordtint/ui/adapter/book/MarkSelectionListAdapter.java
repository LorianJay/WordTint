package com.github.lorenj.wordtint.ui.adapter.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 书籍旁边的标签选择
 */
public class MarkSelectionListAdapter extends RecyclerView.Adapter<MarkSelectionListAdapter.RecyclerViewHolder>
        implements RecyclerViewAdapterItemChange<MarkColor> {

    private final Context context;
    private final List<MarkColor> markColorList = new ArrayList<>();
    private RecycleViewItemClickCallBack<MarkColor> markColorSelectionCallBak;

    public MarkSelectionListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_tag_toast, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MarkColor markColor = markColorList.get(position);
        Drawable drawable = holder.tagToast.getDrawable();
        drawable = drawable.mutate();
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, markColor.getMapColorID()));
    }


    @Override
    public int getItemCount() {
        return markColorList.size();
    }

    @Override
    public void replaceAll(Collection<MarkColor> wordBookSectionEntityCollection) {
        markColorList.clear();
        markColorList.addAll(wordBookSectionEntityCollection);
        notifyItemRangeChanged(0, wordBookSectionEntityCollection.size());
    }

    @Override
    public void setRecycleViewItemClickCallBack(RecycleViewItemClickCallBack<MarkColor> recycleViewItemClickCallBack) {
        markColorSelectionCallBak = recycleViewItemClickCallBack;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final View itemView;
        private final ImageView tagToast;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.tagToast = itemView.findViewById(R.id.iv_tag_toast);
            tagToast.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            MarkSelectionListAdapter tagSelectionListAdapter = (MarkSelectionListAdapter) getBindingAdapter();
            if (tagSelectionListAdapter == null) return;
            tagSelectionListAdapter.markColorSelectionCallBak.viewClickCallBack(tagSelectionListAdapter.markColorList.get(position));
        }
    }

}
