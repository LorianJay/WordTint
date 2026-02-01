package com.github.lorenj.wordtint.ui.adapter.markarea;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author cnsukidayo
 * @date 2023/1/7 17:35
 */
public class ReciteMarkToastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerViewAdapterItemChange<MarkColor> {

    private final Context context;
    private final List<MarkColor> markColorList = new ArrayList<>(2);

    public ReciteMarkToastAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ToastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ToastViewHolder(LayoutInflater.from(context).inflate(R.layout.item_toast_mark, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof ToastViewHolder) {
            ToastViewHolder toastViewHolder = (ToastViewHolder) holder;
            toastViewHolder.toastMark.getDrawable().setTint(context.getResources().getColor(MarkColor.values()[position].getMapColorID(), null));
            toastViewHolder.viewMark.setBackgroundColor(context.getResources().getColor(MarkColor.values()[position].getMapColorID(), null));
        }
    }

    @Override
    public int getItemCount() {
        return markColorList.size();
    }

    @Override
    public void addItem(MarkColor item) {

    }

    @Override
    public void removeItem(MarkColor item) {

    }

    @Override
    public void replaceAll(Collection<MarkColor> markColorList) {
        this.markColorList.clear();
        this.markColorList.addAll(markColorList);
        notifyItemRangeChanged(0, markColorList.size());
    }

    protected static class ToastViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public ImageView toastMark;
        public View viewMark;

        public ToastViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.toastMark = itemView.findViewById(R.id.iv_toast_mark);
            this.viewMark = itemView.findViewById(R.id.iv_view_mark);
        }
    }

}
