package com.github.lorenj.wordtint.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.github.lorenj.wordtint.database.vo.WordMarkCountVO;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.enums.ChartTime;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.ui.adapter.analysis.ChartPoint;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
    private ChartTimeRange chartTimeRange;
    private int countLimit = 50;
    private boolean isSingleWordMode = false;
    private Integer selectedWordId = null;
    ZoneId zone = ZoneId.systemDefault();
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
    /**
     * 数据库
     */
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
        if (id == R.id.tv_reset_select) {
            resetSelectedWord();
            updateChart();
        }
    }

    /**
     * 折线图更新
     */
    private void updateChart() {
        StaticFactory.getExecutorService().execute(() -> {
            WordMarkLogDao wordMarkLogDao = appDatabase.wordMarkLogDao();
            List<WordMarkLogEntity> wordMarkLogEntityList;
            // 查询折线图数据-判断是全局还是局部单词
            if (isSingleWordMode) {
                wordMarkLogEntityList = wordMarkLogDao.findByWordIdAndColor(selectedWordId, selectedColor.name(),
                        chartTimeRange.getStartTime(), chartTimeRange.getEndTime());
            } else {
                wordMarkLogEntityList = wordMarkLogDao.findByColorAndTimeRange(selectedColor.name(),
                        chartTimeRange.getStartTime(), chartTimeRange.getEndTime());
            }
            // 构建出坐标数据
            Map<String, Integer> timeCount = new LinkedHashMap<>();
            DateTimeFormatter timeSpaceFormatter = chartTimeRange.getChartTime().getDateTimeFormatter();
            LocalDateTime start = Instant.ofEpochMilli(chartTimeRange.getStartTime()).atZone(zone).toLocalDateTime();
            LocalDateTime end = Instant.ofEpochMilli(chartTimeRange.getEndTime()).atZone(zone).toLocalDateTime();
            for (LocalDateTime point = start; !point.isAfter(end); point = point.plus(chartTimeRange.getChartTime().getAddDuration())) {
                timeCount.put(timeSpaceFormatter.format(point), 0);
            }
            // 按时间分类且存放到map中保存
            wordMarkLogEntityList.stream()
                    .map(wordMarkLogEntity -> timeSpaceFormatter.format(Instant.ofEpochMilli(wordMarkLogEntity.timestamp)
                            .atZone(zone)
                            .toLocalDateTime()))
                    .forEach(time -> timeCount.merge(time, 1, Integer::sum));
            List<ChartPoint> chartPointList = timeCount.entrySet()
                    .stream()
                    .map(entry -> new ChartPoint(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
            updateUIHandler.post(() -> updateChart(chartPointList));
        });
    }

    /**
     * 排行榜更新
     */
    private void updateRankingList() {
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

    /**
     * 重置单词选择,不选择任何单词
     */
    private void resetSelectedWord() {
        isSingleWordMode = false;
        tvChartTitle.setText(R.string.mark_trend_title);
    }

    /**
     * 更新折线图
     */
    private void updateChart(List<ChartPoint> chartPointList) {
        List<Entry> entryList = new ArrayList<>();
        List<String> labelList = new ArrayList<>();

        for (int i = 0; i < chartPointList.size(); i++) {
            ChartPoint point = chartPointList.get(i);
            entryList.add(new Entry(i, point.getCount()));
            labelList.add(point.getLabel());
        }

        LineDataSet dataSet = new LineDataSet(entryList, "");
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setColor(ContextCompat.getColor(requireContext(), selectedColor.getMapColorID()));
        dataSet.setCircleColor(ContextCompat.getColor(requireContext(), selectedColor.getMapColorID()));
        lineChart.setData(new LineData(dataSet));

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelList));
        xAxis.setLabelCount(Math.min(labelList.size(), 7), true);

        lineChart.animateX(300);
        lineChart.invalidate();
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
        // 颜色选择
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

            resetSelectedWord();
            updateChart();
            updateRankingList();
        });
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
                    resetSelectedWord();
                    updateChart();
                    updateRankingList();
                });
        chartTimeRange = new ChartTimeRange();
        chartTimeRange.setChartTime(ChartTime.MONTH);
        LocalDate today = LocalDate.now(zone);
        long startTime = today.minusDays(chartTimeRange.getChartTime().getRange() - 1)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli();
        long endTime = ZonedDateTime.of(today, LocalTime.of(23, 59, 59, 999_000_000), zone)
                .toInstant()
                .toEpochMilli();
        chartTimeRange.setStartTime(startTime);
        chartTimeRange.setEndTime(endTime);
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
                    resetSelectedWord();
                    updateRankingList();
                });
        // 排行榜点击
        rankingListAdapter.setRecycleViewItemClickCallBack(item -> {
            isSingleWordMode = true;
            selectedWordId = item.getMarkWordId();
            tvChartTitle.setText(item.getWordText());
            updateChart();
        });
        updateChart();
        updateRankingList();
    }


}
