package com.github.lorenj.wordtint.ui.adapter.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.entity.ReciteRecordEntity;
import com.github.lorenj.wordtint.database.vo.ReciteRecordVO;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.customview.ScrollerLinearLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordViewHolder>
        implements RecyclerViewAdapterItemChange<ReciteRecordVO> {

    private final Context context;
    private final List<ReciteRecordVO> allReciteRecordEntityList = new ArrayList<>();
    /**
     * 所有标记的颜色
     */
    private final RecyclerView.RecycledViewPool markPool;
    private int currentPosition = -1;
    private RecordViewModel recordViewModel;

    public RecordListAdapter(Context context, RecordViewModel recordViewModel) {
        this.context = context;
        this.markPool = new RecyclerView.RecycledViewPool();
        this.recordViewModel = recordViewModel;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recite_record, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.scroller.scrollTo(0, 0);
        ReciteRecordVO reciteRecordEntity = allReciteRecordEntityList.get(position);
        holder.createTime.setText(context.getString(
                R.string.create_time,
                DateFormat.format("yyyy-MM-dd HH:mm:ss", reciteRecordEntity.getReciteRecordEntity().createTime)));
        holder.wordCount.setText(context.getString(
                R.string.recite_word_count,
                String.valueOf(reciteRecordEntity.getReciteRecordEntity().wordCount)));
        holder.reciteMode.setText(context.getString(
                R.string.recite_word,
                context.getString(reciteRecordEntity.getReciteMode().getStringId())));
        holder.reciteOrder.setText(context.getString(
                R.string.recite_order,
                context.getString(reciteRecordEntity.getReciteOrder().getStringId())));
        holder.reciteFilter.setText(context.getString(
                R.string.recite_filer,
                context.getString(reciteRecordEntity.getReciteFilter().getStringId())));
        holder.hidePreposition.setText(context.getString(
                R.string.hide_preposition,
                context.getString(reciteRecordEntity.getHidePreposition())));
        holder.recordListAdapter = this;
        holder.recordSelection.setVisibility(position == currentPosition ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return allReciteRecordEntityList.size();
    }

    @Override
    public void replaceAll(Collection<ReciteRecordVO> newWordList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new RecordDiffCallback(this.allReciteRecordEntityList, new ArrayList<>(newWordList)));
        this.allReciteRecordEntityList.clear();
        this.allReciteRecordEntityList.addAll(newWordList);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void addAll(Collection<ReciteRecordVO> moreData) {
        int startPosition = this.allReciteRecordEntityList.size();
        this.allReciteRecordEntityList.addAll(moreData);
        notifyItemRangeInserted(startPosition, moreData.size());
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView createTime, wordCount, reciteMode, reciteOrder, reciteFilter, hidePreposition;
        private ConstraintLayout reciteRecordArea;
        private ImageView recordSelection;
        private RecyclerView recordMark;
        private TextView delete;
        private ScrollerLinearLayout scroller;
        /**
         * 必须持有真正的adapter的引用
         */
        private RecordListAdapter recordListAdapter;
        /**
         * 子recycleView的adapter
         */
        private RecordMarkAdapter recordMarkAdapter;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            reciteRecordArea = itemView.findViewById(R.id.rl_recite_record);
            scroller = itemView.findViewById(R.id.sll_recite_record);
            createTime = itemView.findViewById(R.id.tv_recite_record_create_time);
            wordCount = itemView.findViewById(R.id.tv_item_recite_word_count);
            reciteMode = itemView.findViewById(R.id.tv_item_recite_mode);
            reciteOrder = itemView.findViewById(R.id.tv_item_recite_order);
            reciteFilter = itemView.findViewById(R.id.tv_item_recite_filter);
            hidePreposition = itemView.findViewById(R.id.tv_item_recite_hide);
            //recordMark = itemView.findViewById(R.id.rv_item_record_mark);
            delete = itemView.findViewById(R.id.tx_record_delete);
            recordSelection = itemView.findViewById(R.id.iv_item_recite_record_selection);

            this.reciteRecordArea.setOnClickListener(this);
            this.delete.setOnClickListener(this);

            //recordMark.setLayoutManager(new LinearLayoutManager(context));
            //recordMark.setRecycledViewPool(markPool);
            //recordMarkAdapter = new RecordMarkAdapter(context);
            //recordMark.setAdapter(recordMarkAdapter);
        }

        @Override
        public void onClick(View v) {
            int clickId = v.getId();
            if (clickId == R.id.rl_recite_record) {
                int currentPosition = getBindingAdapterPosition();
                int previousPosition = recordListAdapter.currentPosition;
                recordListAdapter.currentPosition = currentPosition;
                recordListAdapter.notifyItemChanged(previousPosition);
                recordListAdapter.notifyItemChanged(currentPosition);
                recordListAdapter.recordViewModel.getSelectedRecord().setValue(allReciteRecordEntityList.get(currentPosition).getReciteRecordEntity());
            } else if (clickId == R.id.tx_record_delete) {
                int position = getBindingAdapterPosition();
                ReciteRecordEntity reciteRecordEntity = allReciteRecordEntityList.get(position).getReciteRecordEntity();
                recordListAdapter.allReciteRecordEntityList.remove(position);
                recordListAdapter.notifyItemRemoved(position);
                recordListAdapter.recordViewModel.getRemoveRecord().setValue(reciteRecordEntity);
            }
        }
    }

}
