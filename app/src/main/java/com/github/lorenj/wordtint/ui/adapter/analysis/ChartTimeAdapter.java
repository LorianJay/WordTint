package com.github.lorenj.wordtint.ui.adapter.analysis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.enums.ChartTime;
import com.github.lorenj.wordtint.enums.RankingCount;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lorianjay
 * @date 2026/7/27 16:50
 */
public class ChartTimeAdapter implements View.OnClickListener {

    private final ChartTimeViewModel chartTimeViewModel;
    private Context context;
    private final List<TextView> allSelectTextView = new ArrayList<>();
    /**
     * 时间
     */
    private final ZoneId zone = ZoneId.systemDefault();
    private final LocalDate today = LocalDate.now(zone);

    public ChartTimeAdapter(ChartTimeViewModel chartTimeViewModel) {
        this.chartTimeViewModel = chartTimeViewModel;
    }

    public void initTimeList(ViewGroup viewGroup) {
        this.context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        Arrays.stream(ChartTime.values())
                .forEach(chartTime -> {
                    TextView textView = (TextView) inflater.inflate(R.layout.item_analysis_select, viewGroup, false);
                    textView.setText(context.getString(chartTime.getTextId()));
                    textView.setTag(chartTime);
                    textView.setOnClickListener(ChartTimeAdapter.this);
                    viewGroup.addView(textView);
                    allSelectTextView.add(textView);
                    // 默认一个月
                    if (chartTime == ChartTime.MONTH) textView.setSelected(true);
                });
    }

    @Override
    public void onClick(View v) {
        ChartTime chartTime = (ChartTime) v.getTag();
        if (chartTime == ChartTime.CUSTOM) {
            showCustomTimeDialog(v);
            return;
        }
        // 算出当天的最晚时间
        long startTime = today.minusDays(chartTime.getRange() - 1)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli();
        long todayEndTime = ZonedDateTime.of(today, LocalTime.of(23, 59, 59, 999_000_000), zone)
                .toInstant()
                .toEpochMilli();
        ChartTimeRange chartTimeRange = new ChartTimeRange(startTime, todayEndTime, chartTime);
        chartTimeViewModel.getSelectChartTime().setValue(chartTimeRange);
        clearSelectedStatus();
        v.setSelected(true);
    }

    private void showCustomTimeDialog(View v) {
        if (context == null) return;
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("选择日期范围");
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();

        dateRangePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "DATE_RANGE_PICKER_TAG");

        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            clearSelectedStatus();
            v.setSelected(true);
            ChartTimeRange chartTimeRange = new ChartTimeRange(selection.first, selection.second, ChartTime.CUSTOM);
            chartTimeViewModel.getSelectChartTime().setValue(chartTimeRange);
        });

    }

    /**
     * 清除所有选择框的状态
     */
    private void clearSelectedStatus() {
        allSelectTextView.forEach(textView -> textView.setSelected(false));
    }

}
