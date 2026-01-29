package com.github.lorenj.wordtint.ui.adapter.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.entity.local.HistoryDTOLocal;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.github.lorenj.wordtint.utils.DPUtils;
import com.github.lorenj.wordtint.R;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.RecyclerViewHolder>
        implements RecyclerViewAdapterItemChange<HistoryDTOLocal> {

    private final Context context;
    private final List<HistoryDTOLocal> allDivideDTOList = new ArrayList<>();
    /**
     * 记录被点击的划分id
     */
    private final Set<HistoryDTOLocal> divideIdSet = new HashSet<>();

    /**
     * 设置点击子划分的回调事件
     */
    private RecycleViewItemClickCallBack<HistoryDTOLocal> recycleViewItemOnClickListener;

    public HistoryListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.credit_fragment_history_element, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.scroller.scrollTo(0, 0);
        // 首先得到子划分
        HistoryDTOLocal divideDTO = allDivideDTOList.get(position);
        holder.divideTextView.setText(divideDTO.getName());
        if (divideIdSet.contains(divideDTO)) {
            holder.childDivideButton.setImageResource(R.drawable.ic_add_plan);
        } else {
            holder.childDivideButton.setImageDrawable(null);
        }
        holder.elementCount.setText(String.valueOf(divideDTO.getSerializeWordList().size()));
    }

    @Override
    public int getItemCount() {
        return allDivideDTOList.size();
    }

    public void setRecycleViewItemOnClickListener(RecycleViewItemClickCallBack<HistoryDTOLocal> recycleViewItemOnClickListener) {
        this.recycleViewItemOnClickListener = recycleViewItemOnClickListener;
    }

    @Override
    public void addItem(HistoryDTOLocal item) {
        allDivideDTOList.add(item);
        notifyItemInserted(allDivideDTOList.size() - 1);
    }

    @Override
    public void removeItem(HistoryDTOLocal item) {

    }

    @Override
    public void replaceAll(Collection<HistoryDTOLocal> divideDTOS) {
        allDivideDTOList.clear();
        allDivideDTOList.addAll(divideDTOS);
        notifyItemRangeChanged(0, divideDTOS.size());
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView divideTextView;
        private TextView elementCount;
        private ImageButton childDivideButton;
        private RelativeLayout historyRelativeLayout;
        private TextView historyDelete;
        private View scroller;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.itemView.getLayoutParams().height = DPUtils.dp2px(70);
            this.divideTextView = itemView.findViewById(R.id.tv_item_book_section);
            this.childDivideButton = itemView.findViewById(R.id.iv_item_book_section_selection);
            this.elementCount = itemView.findViewById(R.id.tv_item_book_section_count);
            this.historyRelativeLayout = itemView.findViewById(R.id.credit_fragment_history_divide_element);
            this.historyDelete = itemView.findViewById(R.id.fragment_word_history_delete);
            this.scroller = itemView.findViewById(R.id.fragment_word_credit_start_single_category_word_scroller);

            this.historyRelativeLayout.setOnClickListener(this);
            this.historyDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickId = v.getId();
            int position = getAdapterPosition();
            HistoryDTOLocal divideDTO = allDivideDTOList.get(position);
            divideIdSet.clear();
            if (clickId == R.id.credit_fragment_history_divide_element) {
                divideIdSet.add(divideDTO);
                childDivideButton.setImageResource(R.drawable.ic_add_plan);
                recycleViewItemOnClickListener.viewClickCallBack(divideDTO);
            } else if (clickId == R.id.fragment_word_history_delete) {
                allDivideDTOList.remove(position);
                // 持久化
                try {
                    Files.delete(Paths.get(divideDTO.getPath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            notifyItemRangeChanged(0, allDivideDTOList.size());
        }
    }


}
