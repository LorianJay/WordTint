package com.github.lorenj.wordtint.ui.fragment;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
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
import com.github.lorenj.wordtint.enums.TimeDimension;
import com.github.lorenj.wordtint.ui.adapter.analysis.RankingCountAdapter;
import com.github.lorenj.wordtint.ui.adapter.book.MarkSelectionListAdapter;
import com.github.lorenj.wordtint.ui.adapter.analysis.RankingListAdapter;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.flexbox.FlexboxLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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
    private TimeDimension timeDimension = TimeDimension.MONTH;
    private int countLimit = 50;
    private boolean isSingleWordMode = false;
    private Integer selectedWordId = null;
    private int customTimeDays = 30; // 自定义时间范围（天数）

    // ========== UI ==========
    private View rootView;
    private View vSelectedColor;
    private TextView tvSelectedColorName;
    private RecyclerView colorList;
    private TextView tvChartTitle;
    private LineChart lineChart;
    private RecyclerView rcLeaderboard;
    private TextView tvResetSelect;

    // 时间筛选项
    private TextView tvTimeToday, tvTimeWeek, tvTimeMonth, tvTimeAll, tvTimeCustom;
    // 数量筛选项
    //private TextView tvCount10, tvCount25, tvCount50, tvCountAll, tvCountCustom;
    private FlexboxLayout countSelectLayout;


    /**
     * 标签选择adapter
     */
    private MarkSelectionListAdapter markSelectionListAdapter;
    private RankingListAdapter rankingListAdapter;

    // ========== 数据 ==========
    private APPDatabase appDatabase;
    private final Handler updateUIHandler = new Handler(Looper.getMainLooper());
    private Map<Integer, String> wordTextCache = new HashMap<>();
    private List<WordMarkCountVO> currentMarkCountList = new ArrayList<>();

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

    // ========== 点击处理 ==========
    @Override
    public void onClick(View v) {
        int id = v.getId();

        // 时间维度
        if (id == R.id.tv_analysis_time_today && timeDimension != TimeDimension.TODAY) {
            timeDimension = TimeDimension.TODAY;
            updateTimeSelectionUI();
            resetToAllWords();
            refreshAllData();
        } else if (id == R.id.tv_time_week && timeDimension != TimeDimension.WEEK) {
            timeDimension = TimeDimension.WEEK;
            updateTimeSelectionUI();
            resetToAllWords();
            refreshAllData();
        } else if (id == R.id.tv_analysis_time_month && timeDimension != TimeDimension.MONTH) {
            timeDimension = TimeDimension.MONTH;
            updateTimeSelectionUI();
            resetToAllWords();
            refreshAllData();
        } else if (id == R.id.tv_analysis_time_all && timeDimension != TimeDimension.ALL) {
            timeDimension = TimeDimension.ALL;
            updateTimeSelectionUI();
            resetToAllWords();
            refreshAllData();
        } else if (id == R.id.tv_analysis_time_custom) {
            showCustomTimeDialog();
        }

        // 数量
        //if (id == R.id.tv_analysis_count_10 && countLimit != 10) {
        //    countLimit = 10;
        //    updateCountSelectionUI();
        //    resetToAllWords();
        //    refreshLeaderboardOnly();
        //} else if (id == R.id.tv_analysis_count_25 && countLimit != 25) {
        //    countLimit = 25;
        //    updateCountSelectionUI();
        //    resetToAllWords();
        //    refreshLeaderboardOnly();
        //} else if (id == R.id.tv_analysis_count_50 && countLimit != 50) {
        //    countLimit = 50;
        //    updateCountSelectionUI();
        //    resetToAllWords();
        //    refreshLeaderboardOnly();
        //} else if (id == R.id.tv_analysis_count_all) {
        //    countLimit = Integer.MAX_VALUE;
        //    updateCountSelectionUI();
        //    resetToAllWords();
        //    refreshLeaderboardOnly();
        //} else if (id == R.id.tv_analysis_count_custom) {
        //    showCustomCountDialog();
        //}

        // 重置选择
        if (id == R.id.tv_reset_select) {
            resetToAllWords();
            refreshAllData();
        }
    }


    // ========== 重置 ==========
    private void resetToAllWords() {
        if (isSingleWordMode || selectedWordId != null) {
            isSingleWordMode = false;
            selectedWordId = null;
            tvChartTitle.setText(R.string.mark_trend_title);
        }
    }


    // ========== 颜色选择 UI ==========
    private void updateColorSelectionUI() {
        Drawable drawable = vSelectedColor.getBackground().mutate();
        DrawableCompat.setTint(drawable,
                ContextCompat.getColor(requireContext(), selectedColor.getMapColorID()));
        tvSelectedColorName.setText(selectedColor.name());
    }


    // ========== 时间选择 UI ==========
    private void updateTimeSelectionUI() {
        tvTimeToday.setSelected(timeDimension == TimeDimension.TODAY);
        tvTimeWeek.setSelected(timeDimension == TimeDimension.WEEK);
        tvTimeMonth.setSelected(timeDimension == TimeDimension.MONTH);
        tvTimeAll.setSelected(timeDimension == TimeDimension.ALL);
        tvTimeCustom.setSelected(timeDimension == TimeDimension.CUSTOM);
    }


    // ========== 数量选择 UI ==========
    private void updateCountSelectionUI() {
        //tvCount10.setSelected(countLimit == 10);
        //tvCount25.setSelected(countLimit == 25);
        //tvCount50.setSelected(countLimit == 50);
        //tvCountAll.setSelected(countLimit == Integer.MAX_VALUE);
        //tvCountCustom.setSelected(countLimit != 10 && countLimit != 25
        //        && countLimit != 50 && countLimit != Integer.MAX_VALUE);
    }


    // ========== 数据刷新 ==========
    private void refreshAllData() {
        StaticFactory.getExecutorService().execute(() -> {
            long[] timeRange = getTimeRange();
            WordMarkLogDao dao = appDatabase.wordMarkLogDao();
            String colorName = selectedColor.name();

            // 查询排行榜数据
            List<WordMarkCountVO> countList = dao.findWordMarkCounts(colorName,
                    timeRange[0], timeRange[1]);
            currentMarkCountList = countList;

            // 更新单词文本缓存
            if (!countList.isEmpty()) {
                List<Integer> wordIds = new ArrayList<>();
                for (WordMarkCountVO vo : countList) {
                    wordIds.add(vo.markWordId);
                }
                WordOriginDao originDao = appDatabase.wordOriginDao();
                List<WordOriginEntity> origins = originDao.findAllOriginWordByIdList(wordIds);
                Map<Integer, String> textMap = new HashMap<>();
                for (WordOriginEntity entity : origins) {
                    if ("WORD_ORIGIN".equals(entity.key)) {
                        textMap.put(entity.wordId, entity.value);
                    }
                }
                wordTextCache.putAll(textMap);
            }

            // 查询折线图数据
            List<WordMarkLogEntity> chartLogs;
            if (isSingleWordMode && selectedWordId != null) {
                chartLogs = dao.findByWordIdAndColor(selectedWordId, colorName,
                        timeRange[0], timeRange[1]);
            } else {
                chartLogs = dao.findByColorAndTimeRange(colorName, timeRange[0], timeRange[1]);
            }
            List<ChartPoint> chartData = aggregateByTime(chartLogs, timeRange[0], timeRange[1]);

            updateUIHandler.post(() -> {
                updateChart(chartData);
                updateLeaderboard(countList);
            });
        });
    }

    private void refreshLeaderboardOnly() {
        StaticFactory.getExecutorService().execute(() -> {
            long[] timeRange = getTimeRange();
            List<WordMarkCountVO> countList = appDatabase.wordMarkLogDao()
                    .findWordMarkCounts(selectedColor.name(), timeRange[0], timeRange[1]);
            currentMarkCountList = countList;
            updateUIHandler.post(() -> updateLeaderboard(countList));
        });
    }

    private void refreshChartForSingleWord() {
        StaticFactory.getExecutorService().execute(() -> {
            long[] timeRange = getTimeRange();
            List<WordMarkLogEntity> chartLogs = appDatabase.wordMarkLogDao()
                    .findByWordIdAndColor(selectedWordId, selectedColor.name(),
                            timeRange[0], timeRange[1]);
            List<ChartPoint> chartData = aggregateByTime(chartLogs, timeRange[0], timeRange[1]);
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


    // ========== 排行榜更新 ==========
    private void updateLeaderboard(List<WordMarkCountVO> countList) {
        int limit = Math.min(countLimit, countList.size());
        List<WordMarkCountVO> limited = countList.subList(0, Math.max(0, limit));
        rankingListAdapter.setData(limited);
    }


    // ========== 时间范围计算 ==========
    private long[] getTimeRange() {
        Calendar cal = Calendar.getInstance();
        long endTime = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        endTime = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        switch (timeDimension) {
            case TODAY:
                return new long[]{cal.getTimeInMillis(), endTime};
            case WEEK:
                cal.add(Calendar.DAY_OF_MONTH, -6);
                return new long[]{cal.getTimeInMillis(), endTime};
            case MONTH:
                cal.add(Calendar.DAY_OF_MONTH, -29);
                return new long[]{cal.getTimeInMillis(), endTime};
            case ALL:
                return new long[]{0, endTime};
            default:
                cal.add(Calendar.DAY_OF_MONTH, -(customTimeDays - 1));
                return new long[]{cal.getTimeInMillis(), endTime};
        }
    }


    // ========== 时间聚合 ==========
    private List<ChartPoint> aggregateByTime(List<WordMarkLogEntity> logs, long startTime, long endTime) {
        Map<String, Integer> aggregated = new LinkedHashMap<>();
        SimpleDateFormat sdf;

        switch (timeDimension) {
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
        if (timeDimension == TimeDimension.ALL) {
            sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
            // 先收集所有月份
            for (WordMarkLogEntity log : logs) {
                String key = sdf.format(new Date(log.timestamp));
                aggregated.putIfAbsent(key, 0);
            }
        }

        sdf = timeDimension == TimeDimension.TODAY
                ? new SimpleDateFormat("HH:00", Locale.getDefault())
                : timeDimension == TimeDimension.ALL
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


    // ========== 自定义对话框 ==========
    private void showCustomTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_word_recite_section, null);
        EditText minValue = view.findViewById(R.id.fragment_word_credit_dialog_section_min_value);
        EditText maxValue = view.findViewById(R.id.fragment_word_credit_dialog_section_max_value);
        minValue.setHint("天数(距今天数)");
        maxValue.setHint("天数(距今天数)");

        builder.setTitle("自定义时间范围")
                .setMessage("输入距离今天的天数范围（包含今天）")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                    try {
                        int maxDays = Integer.parseInt(maxValue.getText().toString());
                        if (maxDays < 1) return;
                        customTimeDays = maxDays;
                        timeDimension = TimeDimension.CUSTOM;
                        updateTimeSelectionUI();
                        resetToAllWords();
                        refreshAllData();
                    } catch (NumberFormatException ignored) {
                    }
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                })
                .show();
    }

    private void showCustomCountDialog() {
        final EditText inputEditText = new EditText(requireContext());
        inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(requireContext())
                .setTitle("自定义数量")
                .setView(inputEditText)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                    try {
                        countLimit = Integer.parseInt(inputEditText.getText().toString());
                        if (countLimit < 1) countLimit = 50;
                        updateCountSelectionUI();
                        resetToAllWords();
                        refreshLeaderboardOnly();
                    } catch (NumberFormatException ignored) {
                    }
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                })
                .show();
    }


    // ========== 内部类 ==========
    private static class ChartPoint {

        String label;
        int count;
    }

    private void bindView() {
        vSelectedColor = rootView.findViewById(R.id.v_analysis_selected_color);
        tvSelectedColorName = rootView.findViewById(R.id.tv_selected_color_name);
        colorList = rootView.findViewById(R.id.rc_analysis_color_list);
        tvChartTitle = rootView.findViewById(R.id.tv_chart_title);
        lineChart = rootView.findViewById(R.id.lc_mark_trend);
        rcLeaderboard = rootView.findViewById(R.id.rc_analysis_leaderboard);
        tvResetSelect = rootView.findViewById(R.id.tv_reset_select);

        tvTimeToday = rootView.findViewById(R.id.tv_analysis_time_today);
        tvTimeWeek = rootView.findViewById(R.id.tv_time_week);
        tvTimeMonth = rootView.findViewById(R.id.tv_analysis_time_month);
        tvTimeAll = rootView.findViewById(R.id.tv_analysis_time_all);
        tvTimeCustom = rootView.findViewById(R.id.tv_analysis_time_custom);

        countSelectLayout = rootView.findViewById(R.id.fbl_analysis_count_select);
        //tvCount10 = rootView.findViewById(R.id.tv_analysis_count_10);
        //tvCount25 = rootView.findViewById(R.id.tv_analysis_count_25);
        //tvCount50 = rootView.findViewById(R.id.tv_analysis_count_50);
        //tvCountAll = rootView.findViewById(R.id.tv_analysis_count_all);
        //tvCountCustom = rootView.findViewById(R.id.tv_analysis_count_custom);
        appDatabase = APPDatabase.getInstance(requireContext());

        tvTimeToday.setOnClickListener(this);
        tvTimeWeek.setOnClickListener(this);
        tvTimeMonth.setOnClickListener(this);
        tvTimeAll.setOnClickListener(this);
        tvTimeCustom.setOnClickListener(this);
        //tvCount10.setOnClickListener(this);
        //tvCount25.setOnClickListener(this);
        //tvCount50.setOnClickListener(this);
        //tvCountAll.setOnClickListener(this);
        //tvCountCustom.setOnClickListener(this);
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
        markSelectionListAdapter.setRecycleViewItemClickCallBack(new RecycleViewItemClickCallBack<MarkColor>() {
            @Override
            public void viewClickCallBack(MarkColor markColor) {
                selectedColor = markColor != null ? markColor : MarkColor.GREEN;
                updateColorSelectionUI();
                resetToAllWords();
                refreshAllData();
            }
        });

        // 排行榜
        rankingListAdapter = new RankingListAdapter(requireContext(), wordTextCache);
        rcLeaderboard.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcLeaderboard.setAdapter(rankingListAdapter);

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

        // 排行榜点击
        rankingListAdapter.setOnItemClickListener(item -> {
            isSingleWordMode = true;
            selectedWordId = item.markWordId;
            String wordText = wordTextCache.getOrDefault(item.markWordId,
                    String.valueOf(item.markWordId));
            tvChartTitle.setText(wordText);
            refreshChartForSingleWord();
        });

        RankingCountAdapter rankingCountAdapter = new RankingCountAdapter();
        rankingCountAdapter.initCountList(countSelectLayout);

        // 初始化选中状态
        updateColorSelectionUI();
        updateTimeSelectionUI();
        updateCountSelectionUI();

        // 加载数据
        refreshAllData();
    }


}
