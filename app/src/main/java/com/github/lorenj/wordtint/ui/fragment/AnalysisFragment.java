package com.github.lorenj.wordtint.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.dao.WordMarkLogDao;
import com.github.lorenj.wordtint.database.dao.WordOriginDao;
import com.github.lorenj.wordtint.database.entity.WordMarkLogEntity;
import com.github.lorenj.wordtint.database.entity.WordOriginEntity;
import com.github.lorenj.wordtint.database.vo.WordMarkCountVO;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.enums.ChartTime;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.ui.adapter.analysis.ChartTimeAdapter;
import com.github.lorenj.wordtint.ui.adapter.analysis.ChartTimeRange;
import com.github.lorenj.wordtint.ui.adapter.analysis.ChartTimeViewModel;
import com.github.lorenj.wordtint.ui.adapter.analysis.RankingCountAdapter;
import com.github.lorenj.wordtint.ui.adapter.analysis.RankingCountViewModel;
import com.github.lorenj.wordtint.ui.adapter.book.MarkSelectionListAdapter;
import com.github.lorenj.wordtint.ui.adapter.analysis.RankingListAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.flexbox.FlexboxLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import kotlinx.serialization.descriptors.StructureKind;

/**
 * 单词标记分析页面
 *
 * @author cnsukidayo
 * @date 2026/7/26
 */
public class AnalysisFragment extends Fragment implements View.OnClickListener {

    /**
     * 状态
     */
    private MarkColor selectedColor = MarkColor.GREEN;
    private ChartTimeRange chartTimeRange;
    private int countLimit = 50;
    private boolean isSingleWordMode = false;
    private Integer selectedWordId = null;

    /**
     * UI相关
     */
    private View rootView;
    private View currentSelectColor;
    private TextView tvSelectedColorName;
    private RecyclerView colorList;
    private TextView tvChartTitle;
    private LineChart lineChart;
    private RecyclerView rcLeaderboard;
    private TextView tvResetSelect;

    /**
     * 颜色选择
     */
    private MarkSelectionListAdapter markSelectionListAdapter;
    /**
     * 标签选择adapter
     */
    private FlexboxLayout countSelectLayout;
    private RankingListAdapter rankingListAdapter;
    private RankingCountViewModel rankingCountViewModel;
    /**
     * 时间选择
     */
    private FlexboxLayout timeSelectLayout;
    private ChartTimeViewModel chartTimeViewModel;


    // ========== 数据 ==========
    private APPDatabase appDatabase;
    private final Handler updateUIHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_analysis, container, false);
        bindView();
        initView();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        // 重置选择
        if (id == R.id.tv_reset_select) {
            resetToAllWords();
            refreshAllData();
        }
    }

    /**
     * 重置单词选择,不选择任何单词
     */
    private void resetToAllWords() {
        if (isSingleWordMode || selectedWordId != null) {
            isSingleWordMode = false;
            selectedWordId = null;
            tvChartTitle.setText(R.string.mark_trend_title);
        }
    }


    /**
     * 刷新数据
     */
    private void refreshAllData() {
        StaticFactory.getExecutorService().execute(() -> {
            WordMarkLogDao dao = appDatabase.wordMarkLogDao();
            String colorName = selectedColor.name();

            // 查询折线图数据
            List<WordMarkLogEntity> chartLogs;
            if (isSingleWordMode && selectedWordId != null) {
                chartLogs = dao.findByWordIdAndColor(selectedWordId, colorName,
                        chartTimeRange.getStartTime(), chartTimeRange.getEndTime());
            } else {
                chartLogs = dao.findByColorAndTimeRange(colorName, chartTimeRange.getStartTime(), chartTimeRange.getEndTime());
            }
            List<ChartPoint> chartData = aggregateByTime(chartLogs, chartTimeRange.getStartTime(), chartTimeRange.getEndTime());

            updateUIHandler.post(() -> {
                updateChart(chartData);
                refreshLeaderboardOnly();
            });
        });
    }


    /**
     * 排行榜更新
     */
    private void refreshLeaderboardOnly() {
        StaticFactory.getExecutorService().execute(() -> {
            // 查询排行榜数据
            List<WordMarkCountVO> currentMarkCountList = appDatabase.wordMarkLogDao().findWordMarkCounts(selectedColor.name(),
                            chartTimeRange.getStartTime(), chartTimeRange.getEndTime(), countLimit)
                    .stream()
                    .peek(wordMarkCountVO -> {
                        WordOriginDao originDao = appDatabase.wordOriginDao();
                        String wordText = originDao.findAllOriginWordById(wordMarkCountVO.getMarkWordId())
                                .stream()
                                .filter(wordOriginEntity -> WordStructure.valueOf(wordOriginEntity.key) == WordStructure.WORD_ORIGIN)
                                .findFirst()
                                .map(wordOriginEntity -> wordOriginEntity.value)
                                .orElse("");
                        wordMarkCountVO.setWordText(wordText);
                    })
                    .collect(Collectors.toList());
            updateUIHandler.post(() -> rankingListAdapter.replaceAll(currentMarkCountList));
        });
    }

    private void refreshChartForSingleWord() {
        StaticFactory.getExecutorService().execute(() -> {
            List<WordMarkLogEntity> chartLogs = appDatabase.wordMarkLogDao()
                    .findByWordIdAndColor(selectedWordId, selectedColor.name(),
                            chartTimeRange.getStartTime(), chartTimeRange.getEndTime());
            List<ChartPoint> chartData = aggregateByTime(chartLogs, chartTimeRange.getStartTime(), chartTimeRange.getEndTime());
            updateUIHandler.post(() -> updateChart(chartData));
        });
    }


    // ========== 折线图更新 ==========
    private void updateChart(List<ChartPoint> chartData) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < chartData.size(); i++) {
            ChartPoint point = chartData.get(i);
            entries.add(new Entry(i, point.count));
            labels.add(point.label);
        }

        LineDataSet dataSet;
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            dataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            dataSet.setValues(entries);
        } else {
            dataSet = new LineDataSet(entries, "");
            dataSet.setColor(ContextCompat.getColor(requireContext(), selectedColor.getMapColorID()));
            dataSet.setCircleColor(ContextCompat.getColor(requireContext(), selectedColor.getMapColorID()));
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(3f);
            dataSet.setDrawValues(false);
            dataSet.setMode(LineDataSet.Mode.LINEAR);
        }

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(Math.min(labels.size(), 10), true);

        lineChart.animateX(300);
        lineChart.invalidate();
    }

    /**
     * 时间聚合
     */
    private List<ChartPoint> aggregateByTime(List<WordMarkLogEntity> logs, long startTime, long endTime) {
        Map<String, Integer> aggregated = new LinkedHashMap<>();
        SimpleDateFormat sdf;

        switch (chartTimeRange.getChartTime()) {
            case TODAY:
                // 按小时：0-23
                sdf = new SimpleDateFormat("HH:00", Locale.getDefault());
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(startTime);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                for (int h = 0; h < 24; h++) {
                    cal.set(Calendar.HOUR_OF_DAY, h);
                    String key = sdf.format(new Date(cal.getTimeInMillis()));
                    aggregated.put(key, 0);
                }
                break;
            case WEEK:
            case MONTH:
            case CUSTOM:
                // 按天：MM-dd
                sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
                Calendar cal2 = Calendar.getInstance();
                cal2.setTimeInMillis(startTime);
                cal2.set(Calendar.HOUR_OF_DAY, 0);
                for (long t = startTime; t <= endTime; t += 86400000L) {
                    String key = sdf.format(new Date(t));
                    aggregated.put(key, 0);
                }
                break;
            case ALL:
                // 按月：yyyy-MM
                sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                // 动态构建月份槽位
                break;
        }

        // 填充数据
        if (chartTimeRange.getChartTime() == ChartTime.ALL) {
            sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
            // 先收集所有月份
            for (WordMarkLogEntity log : logs) {
                String key = sdf.format(new Date(log.timestamp));
                aggregated.putIfAbsent(key, 0);
            }
        }

        sdf = chartTimeRange.getChartTime() == ChartTime.TODAY
                ? new SimpleDateFormat("HH:00", Locale.getDefault())
                : chartTimeRange.getChartTime() == ChartTime.ALL
                  ? new SimpleDateFormat("yyyy-MM", Locale.getDefault())
                  : new SimpleDateFormat("MM-dd", Locale.getDefault());

        for (WordMarkLogEntity log : logs) {
            String key = sdf.format(new Date(log.timestamp));
            aggregated.merge(key, 1, Integer::sum);
        }

        List<ChartPoint> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : aggregated.entrySet()) {
            ChartPoint point = new ChartPoint();
            point.label = entry.getKey();
            point.count = entry.getValue();
            result.add(point);
        }
        return result;
    }

    // ========== 内部类 ==========
    private static class ChartPoint {
        String label;
        int count;
    }

    private void bindView() {
        currentSelectColor = rootView.findViewById(R.id.v_analysis_selected_color);
        tvSelectedColorName = rootView.findViewById(R.id.tv_selected_color_name);
        colorList = rootView.findViewById(R.id.rc_analysis_color_list);
        tvChartTitle = rootView.findViewById(R.id.tv_chart_title);
        lineChart = rootView.findViewById(R.id.lc_mark_trend);
        rcLeaderboard = rootView.findViewById(R.id.rc_analysis_leaderboard);
        tvResetSelect = rootView.findViewById(R.id.tv_reset_select);
        countSelectLayout = rootView.findViewById(R.id.fbl_analysis_count_select);
        timeSelectLayout = rootView.findViewById(R.id.fbl_analysis_time_select);
        appDatabase = APPDatabase.getInstance(requireContext());
        tvResetSelect.setOnClickListener(this);
    }

    private void initView() {
        List<MarkColor> selectColorList = Arrays.stream(MarkColor.values())
                .filter(markColor -> markColor != MarkColor.BROWN)
                .collect(Collectors.toList());
        markSelectionListAdapter = new MarkSelectionListAdapter(this.getContext());
        colorList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        markSelectionListAdapter.replaceAll(selectColorList);
        colorList.setAdapter(markSelectionListAdapter);
        markSelectionListAdapter.setRecycleViewItemClickCallBack(markColor -> {
            selectedColor = markColor;
            Drawable drawable = currentSelectColor.getBackground().mutate();
            DrawableCompat.setTint(drawable,
                    ContextCompat.getColor(requireContext(), selectedColor.getMapColorID()));
            tvSelectedColorName.setText(selectedColor.name());

            resetToAllWords();
            refreshAllData();
        });
        // 颜色选择
        Drawable drawable = currentSelectColor.getBackground().mutate();
        DrawableCompat.setTint(drawable,
                ContextCompat.getColor(requireContext(), selectedColor.getMapColorID()));
        tvSelectedColorName.setText(selectedColor.name());
        // 折线图配置
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        // 时间选择
        chartTimeViewModel = new ViewModelProvider(this)
                .get(ChartTimeViewModel.class);
        ChartTimeAdapter chartTimeAdapter = new ChartTimeAdapter(chartTimeViewModel);
        chartTimeAdapter.initTimeList(timeSelectLayout);
        chartTimeViewModel.getSelectChartTime()
                .observe(this.getViewLifecycleOwner(), observe -> {
                    chartTimeRange = observe;
                    resetToAllWords();
                    refreshAllData();
                });
        chartTimeRange = new ChartTimeRange();
        chartTimeRange.setChartTime(ChartTime.MONTH);
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        long startTime = today.minusDays(chartTimeRange.getChartTime().getRange() - 1)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli();
        long todayEndTime = ZonedDateTime.of(today, LocalTime.of(23, 59, 59, 999_000_000), zone)
                .toInstant()
                .toEpochMilli();
        chartTimeRange.setStartTime(startTime);
        chartTimeRange.setEndTime(todayEndTime);
        // 排行榜列表
        rankingListAdapter = new RankingListAdapter(requireContext());
        rcLeaderboard.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcLeaderboard.setAdapter(rankingListAdapter);

        rankingCountViewModel = new ViewModelProvider(this)
                .get(RankingCountViewModel.class);
        RankingCountAdapter rankingCountAdapter = new RankingCountAdapter(rankingCountViewModel);
        rankingCountAdapter.initCountList(countSelectLayout);
        rankingCountViewModel.getSelectCountEnum()
                .observe(this.getViewLifecycleOwner(), rankingCount -> {
                    countLimit = rankingCount;
                    resetToAllWords();
                    refreshLeaderboardOnly();
                });
        // 排行榜点击
        rankingListAdapter.setRecycleViewItemClickCallBack(item -> {
            isSingleWordMode = true;
            selectedWordId = item.getMarkWordId();
            tvChartTitle.setText(item.getWordText());
            refreshChartForSingleWord();
        });
        refreshAllData();
    }


}
