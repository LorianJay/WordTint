package com.github.lorenj.wordtint.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.github.lorenj.wordtint.R;

import java.util.ArrayList;
import java.util.List;

public class FlagClickRecyclerViewAdapter extends RecyclerView.Adapter<FlagClickRecyclerViewAdapter.RecyclerViewHolder> {

    private Context context;
    // 保存所有的旗帜
    private final List<RecyclerViewHolder> allFlags = new ArrayList<>(MarkColor.values().length);
    private volatile boolean open = true;
    private RecycleViewItemClickCallBack<MarkColor> recycleViewItemClickCallBack;

    public FlagClickRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.sign_flag_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (allFlags.size() != getItemCount()) {
            holder.flagImageButton.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(MarkColor.values()[position].getMapColorID(), null)));
            allFlags.add(holder);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return MarkColor.values().length;
    }

    public void setRecycleViewItemOnClickListener(RecycleViewItemClickCallBack<MarkColor> recycleViewItemClickCallBack) {
        this.recycleViewItemClickCallBack = recycleViewItemClickCallBack;
    }

    /**
     * 设置当前侧边旗帜区域打开的状态,如果是true则打开侧边栏,否则关闭.
     *
     * @param isOpen 打开的状态
     */
    public void setOpened(boolean isOpen) {
        this.open = isOpen;
    }

    public boolean isOpen() {
        return open;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private ImageButton flagImageButton;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.flagImageButton = itemView.findViewById(R.id.sign_flag_element_flag);
            this.flagImageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // 负责传递消息给上一层
            if (recycleViewItemClickCallBack != null) {
                recycleViewItemClickCallBack.viewClickCallBack(MarkColor.values()[getAdapterPosition()]);
            }
        }
    }


}
