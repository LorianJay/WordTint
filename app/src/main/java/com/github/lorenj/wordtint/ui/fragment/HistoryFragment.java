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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.AnyLanguageWordProperties;
import com.github.lorenj.wordtint.context.pathsystem.document.UserInfoPath;
import com.github.lorenj.wordtint.context.pathsystem.document.WordContextPath;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.vo.UserRecitePreference;
import com.github.lorenj.wordtint.entity.local.HistoryDTOLocal;
import com.github.lorenj.wordtint.ui.adapter.history.HistoryListAdapter;
import com.github.lorenj.wordtint.ui.adapter.listener.NavigationItemSelectListener;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.github.lorenj.wordtint.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author sukidayo
 * @date Wednesday, February 01, 2023
 */
public class HistoryFragment extends Fragment implements NavigationItemSelectListener,
        View.OnClickListener,
        RecycleViewItemClickCallBack<HistoryDTOLocal>, SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private RecyclerView historyRecyclerView;
    private LinearLayoutManager historyLayoutManager;
    private HistoryListAdapter historyListAdapter;
    private final Handler updateUIHandler = new Handler();
    private TextView startLearn;
    private ProgressBar loadingBar;
    private UserRecitePreference userRecitePreference;
    private final HashSet<HistoryDTOLocal> historyDTOSet = new HashSet<>();
    // 下拉刷新
    private SwipeRefreshLayout downRefreshLayout;

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
        rootView = inflater.inflate(R.layout.fragment_analysis, container, false);
        bindView();
        initView();
        requestData();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();
        if (clickId == R.id.fragment_history_start_credit) {
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

    private void bindView() {
        this.historyRecyclerView = rootView.findViewById(R.id.single_history_recycler_view);
        this.startLearn = rootView.findViewById(R.id.fragment_history_start_credit);
        this.loadingBar = rootView.findViewById(R.id.history_fragment_loading_bar);
        this.downRefreshLayout = rootView.findViewById(R.id.fragment_history_swipe_refresh );
    }

    private void initView() {
        this.historyLayoutManager = new LinearLayoutManager(getContext());
        this.historyRecyclerView.setLayoutManager(historyLayoutManager);
        this.historyListAdapter = new HistoryListAdapter(getContext());
        this.historyRecyclerView.setItemAnimator(null);
        this.historyRecyclerView.setAdapter(historyListAdapter);
        this.startLearn.setOnClickListener(this);
        this.historyListAdapter.setRecycleViewItemOnClickListener(this);

        this.downRefreshLayout.setSize(CircularProgressDrawable.LARGE);
        this.downRefreshLayout.setColorSchemeResources(R.color.theme_color);
        this.downRefreshLayout.setOnRefreshListener(this);
    }

    private void requestData() {
        // 查询当前用户的所有划分
        StaticFactory.getExecutorService().execute(() -> {
            // 读取文件列表
            File file = new File(AnyLanguageWordProperties.getExternalFilesDir(), WordContextPath.WORD_HISTORY.getPath());
            List<HistoryDTOLocal> allWordList = new ArrayList<>();
            for (File singleWordList : file.listFiles()) {
                try {
                    HistoryDTOLocal historyDTOLocal = JsonUtils.readJson(singleWordList.getAbsolutePath().replace(AnyLanguageWordProperties.getExternalFilesDir().getAbsolutePath(), ""),
                            HistoryDTOLocal.class);
                    historyDTOLocal.setPath(singleWordList.getAbsolutePath());
                    allWordList.add(historyDTOLocal);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            allWordList.sort((o1, o2) -> o2.getOrder().compareTo(o1.getOrder()));
            updateUIHandler.post(() -> {
                historyListAdapter.replaceAll(allWordList);
                downRefreshLayout.setRefreshing(false);
            });
        });
    }


    @Override
    public void onClickCurrentPage(@NonNull MenuItem item) {

    }

    @Override
    public void onRefresh() {
        this.requestData();
    }
}