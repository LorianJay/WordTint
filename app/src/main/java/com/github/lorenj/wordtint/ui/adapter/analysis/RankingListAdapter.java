package com.github.lorenj.wordtint.ui.adapter.analysis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.vo.WordMarkCountVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 标记分析排行榜 Adapter
 *
 * @author cnsukidayo
 * @date 2026/7/26
 */
public class RankingListAdapter extends RecyclerView.Adapter<RankingListAdapter.ViewHolder> {

    private final Context context;
    private final List<WordMarkCountVO> dataList = new ArrayList<>();
    private final Map<Integer, String> wordTextMap;
    private int maxCount = 1;
    private OnItemClickListener onItemClickListener;

    public RankingListAdapter(Context context, Map<Integer, String> wordTextMap) {
        this.context = context;
        this.wordTextMap = wordTextMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_mark_analysis, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WordMarkCountVO item = dataList.get(position);
        String wordText = wordTextMap.getOrDefault(item.markWordId, String.valueOf(item.markWordId));
        holder.tvWordOrigin.setText(wordText);
        holder.tvMarkCount.setText(item.count + "次");

        // 平均停留时间格式化
        double avgStaySec = item.avgStayTime / 1000.0;
        holder.tvAvgStay.setText(String.format("平均:%.1fs", avgStaySec));

        // 柱状图宽度按比例计算
        int barWeight = (int) (item.count * 1000L / maxCount);
        ViewGroup.LayoutParams params = holder.vBar.getLayoutParams();
        if (params instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) params).weight = barWeight;
        }
        holder.vBar.setLayoutParams(params);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setData(List<WordMarkCountVO> newData) {
        dataList.clear();
        if (newData != null) {
            dataList.addAll(newData);
            maxCount = 1;
            for (WordMarkCountVO vo : dataList) {
                if (vo.count > maxCount) maxCount = vo.count;
            }
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(WordMarkCountVO item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvWordOrigin;
        final TextView tvMarkCount;
        final TextView tvAvgStay;
        final View vBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWordOrigin = itemView.findViewById(R.id.tv_word_origin);
            tvMarkCount = itemView.findViewById(R.id.tv_mark_count);
            tvAvgStay = itemView.findViewById(R.id.tv_avg_stay);
            vBar = itemView.findViewById(R.id.v_bar);
        }
    }
}
