package com.github.lorenj.wordtint.ui.adapter.analysis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.vo.WordMarkCountVO;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * 标记分析排行榜 Adapter
 *
 * @author cnsukidayo
 * @date 2026/7/26
 */
public class RankingListAdapter extends RecyclerView.Adapter<RankingListAdapter.ViewHolder> implements RecyclerViewAdapterItemChange<WordMarkCountVO> {

    private final Context context;
    private final List<WordMarkCountVO> allWordMarkCountList = new ArrayList<>();
    private int maxCount = 1;
    private RecycleViewItemClickCallBack<WordMarkCountVO> onItemClickListener;

    public RankingListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_mark_analysis, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WordMarkCountVO wordMarkCountVO = allWordMarkCountList.get(position);
        String wordText = wordMarkCountVO.getWordText();
        holder.tvWordOrigin.setText(wordText);
        holder.tvMarkCount.setText(context.getString(R.string.mark_count, String.valueOf(wordMarkCountVO.getCount())));
        double avgStaySec = wordMarkCountVO.getAvgStayTime() / 1000.0;
        holder.tvAvgStay.setText(String.format(context.getString(R.string.avg_time_format), avgStaySec));

        // 柱状图宽度按比例计算
        int barProgress = (int) ( wordMarkCountVO.getCount() / (float) maxCount * 100);
        holder.progressBar.setProgress(barProgress);
        holder.itemView.setOnClickListener(v -> onItemClickListener.viewClickCallBack(wordMarkCountVO));
    }

    @Override
    public int getItemCount() {
        return allWordMarkCountList.size();
    }

    @Override
    public void replaceAll(Collection<WordMarkCountVO> replaceAll) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new RankWordDiffCallback(this.allWordMarkCountList, new ArrayList<>(replaceAll)));
        allWordMarkCountList.clear();
        allWordMarkCountList.addAll(replaceAll);
        maxCount = allWordMarkCountList.stream()
                .max(Comparator.comparingInt(WordMarkCountVO::getCount))
                .map(WordMarkCountVO::getCount)
                .orElse(1);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void setRecycleViewItemClickCallBack(RecycleViewItemClickCallBack<WordMarkCountVO> recycleViewItemClickCallBack) {
        this.onItemClickListener = recycleViewItemClickCallBack;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvWordOrigin;
        final TextView tvMarkCount;
        final TextView tvAvgStay;
        final LinearProgressIndicator progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWordOrigin = itemView.findViewById(R.id.tv_word_origin);
            tvMarkCount = itemView.findViewById(R.id.tv_mark_count);
            tvAvgStay = itemView.findViewById(R.id.tv_avg_stay);
            progressBar = itemView.findViewById(R.id.pb_analysis_bar);
        }
    }
}
