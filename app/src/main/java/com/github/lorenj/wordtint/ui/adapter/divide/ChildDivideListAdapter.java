package com.github.lorenj.wordtint.ui.adapter.divide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.entity.local.DivideDTOLocal;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.github.lorenj.wordtint.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChildDivideListAdapter extends RecyclerView.Adapter<ChildDivideListAdapter.RecyclerViewHolder>
        implements RecyclerViewAdapterItemChange<DivideDTOLocal> {

    private final Context context;
    private final List<DivideDTOLocal> allDivideDTOList = new ArrayList<>();
    /**
     * 记录被点击的划分id
     */
    private final Set<DivideDTOLocal> divideIdSet = new HashSet<>();

    /**
     * 设置点击子划分的回调事件
     */
    private RecycleViewItemClickCallBack<DivideDTOLocal> recycleViewItemOnClickListener;

    private boolean coreChoose = false, basisChoose = false;

    public ChildDivideListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_section, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 首先得到子划分
        DivideDTOLocal divideDTO = allDivideDTOList.get(position);
        holder.divideTextView.setText(divideDTO.getName());
        if (divideIdSet.contains(divideDTO)) {
            holder.childDivideButton.setImageResource(R.drawable.ic_add_plan);
        } else {
            holder.childDivideButton.setImageDrawable(null);
        }
        holder.elementCount.setText(String.valueOf(divideDTO.getWordIdList().size()));
    }

    @Override
    public int getItemCount() {
        return allDivideDTOList.size();
    }

    public void setRecycleViewItemOnClickListener(RecycleViewItemClickCallBack<DivideDTOLocal> recycleViewItemOnClickListener) {
        this.recycleViewItemOnClickListener = recycleViewItemOnClickListener;
    }

    @Override
    public void addItem(DivideDTOLocal item) {
        allDivideDTOList.add(item);
        notifyItemInserted(allDivideDTOList.size() - 1);
    }

    @Override
    public void removeItem(DivideDTOLocal item) {

    }

    @Override
    public void replaceAll(Collection<DivideDTOLocal> divideDTOS) {
        allDivideDTOList.clear();
        allDivideDTOList.addAll(divideDTOS);
        notifyItemRangeChanged(0, divideDTOS.size());
    }

    public void coreChoose() {
        // 默认为选择
        coreChoose = !coreChoose;
        for (int position = 0; position < 26; position++) {
            DivideDTOLocal divideDTO = allDivideDTOList.get(position);
            // 没有才需要添加
            if (!divideIdSet.contains(divideDTO) && coreChoose) {
                divideIdSet.add(divideDTO);
                recycleViewItemOnClickListener.viewClickCallBack(divideDTO);
            } else if (divideIdSet.contains(divideDTO) && !coreChoose) {
                divideIdSet.remove(divideDTO);
                recycleViewItemOnClickListener.viewClickCallBack(divideDTO);
            }
        }
        notifyItemRangeChanged(0, getItemCount());
    }

    public void basisChoose() {
        // 默认为选择
        basisChoose = !basisChoose;
        for (int position = 26; position < 57; position++) {
            DivideDTOLocal divideDTO = allDivideDTOList.get(position);
            // 没有才需要添加
            if (!divideIdSet.contains(divideDTO) && basisChoose) {
                divideIdSet.add(divideDTO);
                recycleViewItemOnClickListener.viewClickCallBack(divideDTO);
            } else if (divideIdSet.contains(divideDTO) && !basisChoose) {
                divideIdSet.remove(divideDTO);
                recycleViewItemOnClickListener.viewClickCallBack(divideDTO);
            }
        }
        notifyItemRangeChanged(0, getItemCount());
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView divideTextView;
        private TextView elementCount;
        private ImageButton childDivideButton;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.divideTextView = itemView.findViewById(R.id.tv_item_book_section);
            this.childDivideButton = itemView.findViewById(R.id.ib_item_book_section);
            this.elementCount = itemView.findViewById(R.id.tv_item_book_section_count);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            DivideDTOLocal divideDTO = allDivideDTOList.get(position);
            if (divideIdSet.contains(divideDTO)) {
                divideIdSet.remove(divideDTO);
            } else {
                divideIdSet.add(divideDTO);
            }
            if (divideIdSet.contains(divideDTO)) {
                childDivideButton.setImageResource(R.drawable.ic_add_plan);
            } else {
                childDivideButton.setImageDrawable(null);
            }
            recycleViewItemOnClickListener.viewClickCallBack(divideDTO);
        }
    }


}
