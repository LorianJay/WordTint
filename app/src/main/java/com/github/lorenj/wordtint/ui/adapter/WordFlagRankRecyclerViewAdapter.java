package com.github.lorenj.wordtint.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.entity.dto.DataPage;
import com.github.lorenj.wordtint.entity.local.WordFlagRankLocal;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.utils.DPUtils;
import com.github.lorenj.wordtint.R;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class WordFlagRankRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerViewAdapterItemChange<WordFlagRankLocal> {

    private final Context context;
    // 所有单词背诵情况列表
    private DataPage<WordFlagRankLocal> flagRankPage;
    // 当前旗帜的最大值
    private int maxCount;

    public WordFlagRankRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new WordFlagRankRecyclerViewAdapter.WordFlagRankViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_flag_rank_element, parent, false));
        } else {
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.theme_color)));
            RelativeLayout loadMoreLinearLayout = new RelativeLayout(context);
            loadMoreLinearLayout.setGravity(Gravity.CENTER);
            loadMoreLinearLayout.addView(progressBar, RelativeLayout.LayoutParams.MATCH_PARENT, DPUtils.dp2px(20));
            return new WordFlagRankRecyclerViewAdapter.LoadMoreViewHolder(loadMoreLinearLayout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WordFlagRankViewHolder) {
            WordFlagRankViewHolder wordAnalysisProgressViewHolder = (WordFlagRankViewHolder) holder;
            wordAnalysisProgressViewHolder.wordOrigin.setText(flagRankPage.getContent().get(position).getWordOrigin());
            wordAnalysisProgressViewHolder.wordCount.setText(String.valueOf(flagRankPage.getContent().get(position).getCount()));
            // 计算比例
            int progressValue = BigDecimal
                    .valueOf(flagRankPage.getContent().get(position).getCount())
                    .divide(BigDecimal.valueOf(maxCount), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .intValue();
            wordAnalysisProgressViewHolder.wordpProgressBar.setProgress(progressValue);
        }
    }

    @Override
    public int getItemCount() {
        if (flagRankPage != null) {
            // 如果是最后一个元素则不添加最后的loadMore视图
            if (flagRankPage.isLast()) {
                return flagRankPage.getContent().size();
            }
            // 否则多一个元素,该元素就是最后的LoadMore
            return flagRankPage.getContent().size() + 1;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        // 如果当前的position是selectWordPage的size表示当前这个组件是LoadMore组件
        if (position == flagRankPage.getContent().size()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void addItem(WordFlagRankLocal item) {

    }

    @Override
    public void removeItem(WordFlagRankLocal item) {

    }


    @Override
    public void addAllWithDataPage(DataPage<WordFlagRankLocal> dataPage) {
        // 这里需要拷贝响应的属性
        flagRankPage.setFirst(dataPage.isFirst());
        flagRankPage.setLast(dataPage.isLast());
        flagRankPage.getContent().addAll(dataPage.getContent());
        notifyItemRangeInserted(flagRankPage.getContent().size() - 1, dataPage.getContent().size());
    }

    @Override
    public void replaceAllWithDataPage(DataPage<WordFlagRankLocal> dataPage) {
        int preCount = getItemCount();
        // 必须先移除旧数据再添加新数据,否则会造成状态不一致
        notifyItemRangeRemoved(0, preCount);
        this.flagRankPage = dataPage;
        notifyItemRangeChanged(0, getItemCount());
        // 替换的时候需要记录当前最大的次数值
        if (dataPage.getContent().size() != 0) {
            this.maxCount = dataPage.getContent().get(0).getCount();
        }
    }


    public static class WordFlagRankViewHolder extends RecyclerView.ViewHolder {
        private TextView wordOrigin;
        private ProgressBar wordpProgressBar;
        private TextView wordCount;

        public WordFlagRankViewHolder(@NonNull View itemView) {
            super(itemView);
            this.wordOrigin = itemView.findViewById(R.id.flag_rank_word_origin);
            this.wordpProgressBar = itemView.findViewById(R.id.flag_rank_word_progress_bar);
            this.wordCount = itemView.findViewById(R.id.flag_rank_word_count);
        }
    }


    /**
     * 加载更多的组件
     */
    public class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

}

