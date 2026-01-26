package com.github.lorenj.wordtint.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.entity.local.WordAnalysisLocal;
import com.github.lorenj.wordtint.enums.FlagColor;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WordAnalysisProgressRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerViewAdapterItemChange<WordAnalysisLocal> {

    private final Context context;
    private WordAnalysisLocal wordAnalysisLocal;
    private final List<FlagColor> flagColorList = new ArrayList<>(5);
    private Integer maxValue = Integer.MIN_VALUE;

    public WordAnalysisProgressRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WordAnalysisProgressViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_analysis_progress_element, parent, false));
    }

    @Override
    @SuppressWarnings("all")
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WordAnalysisProgressViewHolder wordAnalysisProgressViewHolder = null;
        if (holder instanceof WordAnalysisProgressViewHolder) {
            wordAnalysisProgressViewHolder = (WordAnalysisProgressViewHolder) holder;
        }
        // 设置总记录次数
        FlagColor flagColor = flagColorList.get(position);
        WordAnalysisLocal.FlagColorMapInfo flagColorMapInfo = wordAnalysisLocal.getMapMessage().get(flagColor);
        wordAnalysisProgressViewHolder.percentage.setText(String.valueOf(flagColorMapInfo.getTotal()));
        // 计算比例
        int progressValue = BigDecimal
                .valueOf(flagColorMapInfo.getTotal())
                .divide(BigDecimal.valueOf(maxValue), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .intValue();
        wordAnalysisProgressViewHolder.progressBar.setProgress(progressValue);
        // 设置旗帜颜色
        wordAnalysisProgressViewHolder.flag.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(flagColor.getMapColorID(), null)));
        // 设置上次标记的时间
        wordAnalysisProgressViewHolder.last.setText(String.format(
                context.getString(R.string.word_analysis_last_flag),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        .format(ZonedDateTime
                                .ofInstant(Instant
                                        .ofEpochMilli(flagColorMapInfo.getRecentCreateTimestamp()), ZoneId.systemDefault()))
        ));
        // 设置距离当前已经过去的时间
        Duration between = Duration.between(Instant.ofEpochMilli(flagColorMapInfo.getRecentCreateTimestamp()), LocalDateTime.now().atZone(ZoneId.systemDefault()));
        wordAnalysisProgressViewHolder.previous.setText(String.format(
                context.getString(R.string.word_analysis_previous),
                between.toHours()));
    }

    @Override
    public int getItemCount() {
        return wordAnalysisLocal.getMapMessage().size();
    }

    @Override
    public void addItem(WordAnalysisLocal item) {
        this.wordAnalysisLocal = item;
        // 找出最大值
        for (Map.Entry<FlagColor, WordAnalysisLocal.FlagColorMapInfo> entry : wordAnalysisLocal.getMapMessage().entrySet()) {
            flagColorList.add(entry.getKey());
            maxValue = Math.max(entry.getValue().getTotal(), maxValue);
        }
        flagColorList.sort((o1, o2) -> o1.ordinal() - o2.ordinal());
    }

    @Override
    public void removeItem(WordAnalysisLocal item) {

    }


    public static class WordAnalysisProgressViewHolder extends RecyclerView.ViewHolder {
        private TextView percentage;
        private ProgressBar progressBar;
        private ImageView flag;
        private TextView last;
        private TextView previous;

        public WordAnalysisProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            this.percentage = itemView.findViewById(R.id.analysis_word_progress_bar_percentage);
            this.progressBar = itemView.findViewById(R.id.analysis_word_progress_bar);
            this.flag = itemView.findViewById(R.id.analysis_word_flag);
            this.last = itemView.findViewById(R.id.analysis_word_last_flag);
            this.previous = itemView.findViewById(R.id.analysis_word_previous);
        }
    }
}

