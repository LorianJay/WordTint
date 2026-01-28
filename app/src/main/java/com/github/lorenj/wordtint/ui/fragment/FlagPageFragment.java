package com.github.lorenj.wordtint.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.entity.dto.DataPage;
import com.github.lorenj.wordtint.entity.dto.PageQueryParam;
import com.github.lorenj.wordtint.entity.local.WordDTOLocal;
import com.github.lorenj.wordtint.entity.local.WordFlagRankLocal;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.handler.WordAnalysisHandler;
import com.github.lorenj.wordtint.ui.adapter.WordFlagRankRecyclerViewAdapter;
import com.github.lorenj.wordtint.R;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class FlagPageFragment extends Fragment {

    private View rootView;
    private RecyclerView pageFlagRecyclerView;
    // 当前是否是最后一页,为了防止重复请求
    private volatile boolean lastList = false;
    // 判断当前是否有任务正在执行
    private volatile boolean isRunning = false;
    // 执行器
    private volatile Timer queryTimer;
    // 分页查询参数
    private PageQueryParam pageQueryParam;
    private WordFlagRankRecyclerViewAdapter wordFlagRankRecyclerViewAdapter;
    private Handler updateUIHandler;
    // 单词分析工具
    private WordAnalysisHandler wordAnalysisHandler;
    // 当前页面对应哪个FlagColor
    private MarkColor markColor;
    // 单词字典
    private Map<Long, WordDTOLocal> allWordDict;

    public FlagPageFragment(MarkColor markColor, WordAnalysisHandler wordAnalysisHandler) {
        this.markColor = markColor;
        this.wordAnalysisHandler = wordAnalysisHandler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            initView();
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_flag_page, container, false);
        bindView();
        initView();
        return rootView;
    }


    private void showView() {
        updateUIHandler.post(() -> pageFlagRecyclerView.setAdapter(wordFlagRankRecyclerViewAdapter));
    }

    private void initView() {
        this.pageFlagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        updateUIHandler = new Handler();
        StaticFactory
                .getExecutorService()
                .submit(() -> {
                    // 异步查询单词信息(可能比较费时)
                    wordFlagRankRecyclerViewAdapter = new WordFlagRankRecyclerViewAdapter(getContext());
                    showView();
                    allWordDict = StaticFactory.getAllWordDict();
                    pageQueryParam = new PageQueryParam();
                    pageQueryParam.setSize(20);
                    pageQueryParam.setCurrent(1);
                    this.queryTimer = new Timer();
                    isRunning = true;
                    this.queryTimer.schedule(getQueryTask(), 0);
                });
        // 检测当前排行榜是否要显示更多的单词
        pageFlagRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 滑动到底部
                if (!recyclerView.canScrollVertically(1) && !lastList) {
                    // 优先执行搜索事件或者已有的分页查询事件
                    if (!isRunning) {
                        isRunning = true;
                        pageQueryParam.setCurrent(pageQueryParam.getCurrent() + 1);
                        queryTimer = new Timer();
                        queryTimer.schedule(getQueryTask(), 1000);
                    }
                }
            }
        });
    }


    /**
     * 获得具体的查询函数
     */
    private TimerTask getQueryTask() {
        return new TimerTask() {
            @Override
            public void run() {
                StaticFactory.getExecutorService().submit(() -> {
                    DataPage<WordFlagRankLocal> page = wordAnalysisHandler.pageQueryFlagRankByFlagColor(markColor, pageQueryParam);
                    // 替换为单词原文内容
                    page.getContent().forEach(wordFlagRankLocal -> wordFlagRankLocal
                            .setWordOrigin(allWordDict.get((long) wordFlagRankLocal.getWordId())
                                    .getOrigin()));
                    updateUIHandler.post(() -> {
                        lastList = page.isLast();
                        if (page.isFirst()) {
                            // 如果是第一页就替换
                            wordFlagRankRecyclerViewAdapter.replaceAllWithDataPage(page);
                        } else {
                            // 否则就添加所有数据到集合中
                            wordFlagRankRecyclerViewAdapter.addAllWithDataPage(page);
                        }
                    });
                });
                isRunning = false;
            }
        };

    }

    private void bindView() {
        this.pageFlagRecyclerView = rootView.findViewById(R.id.fragment_flag_page_recycler);
    }
}