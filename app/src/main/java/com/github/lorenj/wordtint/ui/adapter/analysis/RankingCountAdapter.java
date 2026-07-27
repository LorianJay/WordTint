package com.github.lorenj.wordtint.ui.adapter.analysis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.enums.AnalysisCountEnum;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author lorianjay
 * @date 2026/7/27 16:50
 */
public class RankingCountAdapter implements View.OnClickListener {

    public void initCountList(ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        Arrays.stream(AnalysisCountEnum.values())
                .forEach(analysisCountEnum -> {
                    TextView textView = (TextView) inflater.inflate(R.layout.item_analysis_select, viewGroup, false);
                    textView.setText(viewGroup.getContext().getString(analysisCountEnum.getTextId()));
                    textView.setTag(analysisCountEnum);
                    textView.setOnClickListener(RankingCountAdapter.this);
                    viewGroup.addView(textView);
                });
    }

    @Override
    public void onClick(View v) {
        AnalysisCountEnum analysisCountEnum = (AnalysisCountEnum) v.getTag();

    }
}
