package com.github.lorenj.wordtint.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.dao.ReciteRecordDao;
import com.github.lorenj.wordtint.database.dao.ReciteRecordMarkDao;
import com.github.lorenj.wordtint.database.dao.ReciteRecordWordDao;
import com.github.lorenj.wordtint.database.entity.ReciteRecordEntity;
import com.github.lorenj.wordtint.database.vo.ReciteRecordVO;
import com.github.lorenj.wordtint.database.vo.UserRecitePreference;
import com.github.lorenj.wordtint.entity.local.HistoryDTOLocal;
import com.github.lorenj.wordtint.enums.ReciteFilter;
import com.github.lorenj.wordtint.enums.ReciteMode;
import com.github.lorenj.wordtint.enums.ReciteOrder;
import com.github.lorenj.wordtint.ui.adapter.LoadMoreAdapter;
import com.github.lorenj.wordtint.ui.adapter.history.RecordListAdapter;
import com.github.lorenj.wordtint.ui.adapter.history.RecordViewModel;
import com.github.lorenj.wordtint.ui.adapter.listener.NavigationItemSelectListener;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;

import java.util.HashSet;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author sukidayo
 * @date Wednesday, February 01, 2023
 */
public class RecordListFragment extends Fragment implements NavigationItemSelectListener,
        View.OnClickListener,
        RecycleViewItemClickCallBack<HistoryDTOLocal>, SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private RecyclerView recordList;

    private final Handler updateUIHandler = new Handler();
    private TextView startLearn;
    private ProgressBar loadingBar;
    private UserRecitePreference userRecitePreference;
    private final HashSet<HistoryDTOLocal> historyDTOSet = new HashSet<>();
    /**
     * 下拉刷新
     */
    private SwipeRefreshLayout refreshLayout;

    /**
     * 数据库
     */
    private APPDatabase appDatabase;
    /**
     * 执行器
     */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> currentTask = null;
    /**
     * 当前查询的页面参数
     */
    private int page = 0;
    private final int PAGE_SIZE = 10;
    private boolean lastPage = false;
    private final long searchDelay = 500;
    /**
     * 适配器
     */
    private LoadMoreAdapter loadMoreAdapter;
    private RecordListAdapter recordListAdapter;
    private ReciteRecordEntity currentSelectRecord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_record_list, container, false);
        bindView();
        initView();
        onRefresh();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();
        if (clickId == R.id.tv_record_list_start) {
            loadingBar.setVisibility(View.VISIBLE);
            StaticFactory.getExecutorService().submit(() -> {
                updateUIHandler.post(() -> {
                    loadingBar.setVisibility(View.INVISIBLE);
                });
            });
        }
    }


    /**
     * 用户选择一个历史记录后的回调事件
     *
     * @param recycleViewOnClick 回调对象
     */
    @Override
    public void viewClickCallBack(HistoryDTOLocal recycleViewOnClick) {
        historyDTOSet.clear();
        historyDTOSet.add(recycleViewOnClick);
    }

    @Override
    public void onRefresh() {
        page = 0;
        this.currentTask = this.scheduler.schedule(getQueryTask(), searchDelay, TimeUnit.MILLISECONDS);
    }


    /**
     * 获得具体的查询函数
     */
    private TimerTask getQueryTask() {
        return new TimerTask() {
            @Override
            public void run() {
                StaticFactory.getExecutorService().submit(() -> {
                    int countWord = appDatabase.reciteRecordDao().countReciteRecord();
                    lastPage = (page + 1) * PAGE_SIZE >= countWord;
                    List<ReciteRecordVO> reciteRecordVOList = appDatabase.reciteRecordDao()
                            .findReciteRecordWithLimit(page * PAGE_SIZE, PAGE_SIZE)
                            .stream()
                            .map(reciteRecordEntity -> {
                                ReciteRecordVO reciteRecordVO = new ReciteRecordVO(
                                        reciteRecordEntity,
                                        ReciteMode.valueOf(reciteRecordEntity.reciteMode),
                                        ReciteOrder.valueOf(reciteRecordEntity.reciteOrder),
                                        ReciteFilter.valueOf(reciteRecordEntity.reciteFiler),
                                        reciteRecordEntity.hidePreposition ? R.string.gone : R.string.visible
                                );
                                return reciteRecordVO;
                            })
                            .collect(Collectors.toList());
                    updateUIHandler.post(() -> {
                        loadMoreAdapter.setVisible(lastPage ? View.GONE : View.VISIBLE);
                        // 第一次是全量更新,第二次是增量更新
                        if (page == 0) {
                            recordListAdapter.replaceAll(reciteRecordVOList);
                        } else {
                            recordListAdapter.addAll(reciteRecordVOList);
                        }
                        refreshLayout.setRefreshing(false);
                    });
                });
            }
        };
    }

    private void initView() {
        LinearLayoutManager recordListLayoutManager = new LinearLayoutManager(getContext());
        this.recordList.setLayoutManager(recordListLayoutManager);
        RecordViewModel recordViewModel = new RecordViewModel();
        this.recordListAdapter = new RecordListAdapter(getContext(), recordViewModel);
        this.loadMoreAdapter = new LoadMoreAdapter(getContext());
        ConcatAdapter concatAdapter = new ConcatAdapter(recordListAdapter, loadMoreAdapter);
        this.recordList.setAdapter(concatAdapter);

        this.startLearn.setOnClickListener(this);

        this.refreshLayout.setSize(CircularProgressDrawable.LARGE);
        this.refreshLayout.setColorSchemeResources(R.color.theme_color);
        this.refreshLayout.setOnRefreshListener(this);

        appDatabase = APPDatabase.getInstance(getContext());
        recordList.setItemAnimator(null);
        recordList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 滑动到底部
                if (!recyclerView.canScrollVertically(1) && !lastPage) {
                    // 优先执行搜索事件或者已有的分页查询事件
                    if (currentTask != null && !currentTask.isDone()) currentTask.cancel(true);
                    currentTask = scheduler.schedule(getQueryTask(), searchDelay, TimeUnit.MILLISECONDS);
                }
            }
        });
        recordViewModel.getSelectedRecord().observe(getViewLifecycleOwner(), reciteRecordEntity -> currentSelectRecord = reciteRecordEntity);
        recordViewModel.getRemoveRecord().observe(getViewLifecycleOwner(), reciteRecordEntity -> StaticFactory.getExecutorService().execute(() -> {
            ReciteRecordDao reciteRecordDao = appDatabase.reciteRecordDao();
            ReciteRecordWordDao reciteRecordWordDao = appDatabase.reciteRecordWordDao();
            ReciteRecordMarkDao reciteRecordMarkDao = appDatabase.reciteRecordMarkDao();
            appDatabase.runInTransaction(() -> {
                List<Integer> recordWordIdList = reciteRecordWordDao.findByRecordId(reciteRecordEntity.id)
                        .stream()
                        .map(reciteRecordWordEntity -> reciteRecordWordEntity.id)
                        .collect(Collectors.toList());
                // 删除所有标记
                reciteRecordMarkDao.deleteByRecordWordId(recordWordIdList);
                // 删除所有单词列表
                reciteRecordWordDao.deleteByRecordId(reciteRecordEntity.id);
                // 删除背诵记录
                reciteRecordDao.delete(reciteRecordEntity);
            });
        }));
    }


    private void bindView() {
        this.loadingBar = rootView.findViewById(R.id.pb_record_list);
        this.startLearn = rootView.findViewById(R.id.tv_record_list_start);
        this.refreshLayout = rootView.findViewById(R.id.sRl_record_list);
        this.recordList = rootView.findViewById(R.id.rv_record_list);
    }

    @Override
    public void onClickCurrentPage(@NonNull MenuItem item) {

    }
}