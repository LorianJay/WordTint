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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.pathsystem.document.UserInfoPath;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.vo.ReciteRecordVO;
import com.github.lorenj.wordtint.database.vo.UserRecitePreference;
import com.github.lorenj.wordtint.entity.local.HistoryDTOLocal;
import com.github.lorenj.wordtint.enums.ReciteFilter;
import com.github.lorenj.wordtint.enums.ReciteMode;
import com.github.lorenj.wordtint.enums.ReciteOrder;
import com.github.lorenj.wordtint.ui.adapter.LoadMoreAdapter;
import com.github.lorenj.wordtint.ui.adapter.history.RecordListAdapter;
import com.github.lorenj.wordtint.ui.adapter.listener.NavigationItemSelectListener;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.github.lorenj.wordtint.utils.JsonUtils;

import java.io.IOException;
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
    private LinearLayoutManager historyLayoutManager;

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
                try {
                    userRecitePreference = JsonUtils.readJson(UserInfoPath.USER_CREDIT_STYLE.getPath(), UserRecitePreference.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 拷贝Bean
                Bundle bundle = new Bundle();
                //bundle.putParcelable(WordReciteLaunchActivity.USER_RECITE_PREFERENCE, userCreditStyleWrapper);
                // 首先将id转为String类型的List
                bundle.putSerializable(BookListFragment.HISTORY_WORD_SET, historyDTOSet);
                // 统计当前的选词量
                int selectWordCount = 0;
                for (HistoryDTOLocal historyDTOLocal : historyDTOSet) {
                    selectWordCount += historyDTOLocal.getSerializeWordList().size();
                }
                bundle.putInt(BookListFragment.SELECT_WORD_COUNT, selectWordCount);
                updateUIHandler.post(() -> {
                    if (userRecitePreference.isIgnore()) {
                        Navigation.findNavController(getView()).navigate(R.id.action_navigation_main_to_word_credit, bundle,
                                StaticFactory.getSimpleNavOptions());
                    } else {
                        Navigation.findNavController(getView()).navigate(R.id.action_main_navigation_to_navigation_word_credit_launch, bundle,
                                StaticFactory.getSimpleNavOptions());
                    }
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
                            .findWithLimit(page * PAGE_SIZE, PAGE_SIZE)
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
                    });
                });
            }
        };
    }


    private void initView() {
        this.historyLayoutManager = new LinearLayoutManager(getContext());
        this.recordList.setLayoutManager(historyLayoutManager);
        this.recordListAdapter = new RecordListAdapter(getContext());
        this.loadMoreAdapter = new LoadMoreAdapter(getContext());
        ConcatAdapter concatAdapter = new ConcatAdapter(recordListAdapter, loadMoreAdapter);
        this.recordList.setAdapter(concatAdapter);

        this.startLearn.setOnClickListener(this);

        this.refreshLayout.setSize(CircularProgressDrawable.LARGE);
        this.refreshLayout.setColorSchemeResources(R.color.theme_color);
        this.refreshLayout.setOnRefreshListener(this);

        appDatabase = APPDatabase.getInstance(getContext());

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