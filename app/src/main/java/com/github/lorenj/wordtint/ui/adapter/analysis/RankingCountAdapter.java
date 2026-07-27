package com.github.lorenj.wordtint.ui.adapter.analysis;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.enums.RankingCount;
import com.github.lorenj.wordtint.utils.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lorianjay
 * @date 2026/7/27 16:50
 */
public class RankingCountAdapter implements View.OnClickListener {

    private RankingCountViewModel rankingCountViewModel;
    private Context context;
    private Toast globalToast;
    private final List<TextView> allSelectTextView = new ArrayList<>();

    public RankingCountAdapter(RankingCountViewModel rankingCountViewModel) {
        this.rankingCountViewModel = rankingCountViewModel;
    }

    public void initCountList(ViewGroup viewGroup) {
        this.context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        Arrays.stream(RankingCount.values())
                .forEach(rankingCountEnum -> {
                    TextView textView = (TextView) inflater.inflate(R.layout.item_analysis_select, viewGroup, false);
                    textView.setText(context.getString(rankingCountEnum.getTextId()));
                    textView.setTag(rankingCountEnum);
                    textView.setOnClickListener(RankingCountAdapter.this);
                    viewGroup.addView(textView);
                    allSelectTextView.add(textView);
                    // 默认50个
                    if (rankingCountEnum == RankingCount.FIFTY) textView.performClick();
                });
    }

    @Override
    public void onClick(View v) {
        RankingCount rankingCount = (RankingCount) v.getTag();
        if (rankingCount == RankingCount.CUSTOM) {
            showCustomCountDialog(v);
            return;
        }
        rankingCountViewModel.getSelectCountEnum().setValue(rankingCount.getCount());
        clearSelectedStatus();
        v.setSelected(true);
    }

    private void showCustomCountDialog(View v) {
        if (context == null) return;
        final EditText inputEditText = new EditText(context);
        inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(context)
                .setTitle("自定义数量")
                .setView(inputEditText)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                    String value = inputEditText.getText().toString();
                    int i = 0;
                    if (MathUtils.isInt(value)) {
                        i = Integer.parseInt(value);
                    }
                    if (i <= 0
                            || !MathUtils.isInt(value)) {
                        if (globalToast != null) globalToast.cancel();
                        globalToast = Toast.makeText(context,
                                context.getString(R.string.input_error),
                                Toast.LENGTH_SHORT);
                        globalToast.setGravity(Gravity.CENTER, 0, 500);
                        globalToast.show();
                        return;
                    }
                    clearSelectedStatus();
                    v.setSelected(true);
                    rankingCountViewModel.getSelectCountEnum().setValue(i);
                })
                .setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> {
                })
                .show();
    }

    /**
     * 清除所有选择框的状态
     */
    private void clearSelectedStatus() {
        allSelectTextView.forEach(textView -> textView.setSelected(false));
    }

}
