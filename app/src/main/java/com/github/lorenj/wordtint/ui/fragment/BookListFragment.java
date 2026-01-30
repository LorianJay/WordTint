package com.github.lorenj.wordtint.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.entity.relation.WordBookWithSectionEntity;
import com.github.lorenj.wordtint.database.rep.UserSettingRepository;
import com.github.lorenj.wordtint.database.vo.WordBookSectionEntityVO;
import com.github.lorenj.wordtint.database.vo.WordBookWithSectionVO;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.enums.UserSettingKeyEnums;
import com.github.lorenj.wordtint.ui.MainActivity;
import com.github.lorenj.wordtint.ui.activity.WordReciteLaunchActivity;
import com.github.lorenj.wordtint.ui.adapter.book.BookListAdapter;
import com.github.lorenj.wordtint.ui.adapter.listener.NavigationItemSelectListener;
import com.github.lorenj.wordtint.ui.viewmodel.BookViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BookListFragment extends Fragment implements View.OnClickListener, NavigationItemSelectListener {

    private View rootView;
    private BottomNavigationView viewPageChangeNavigationView;
    /**
     * 开始学习的文本框
     */
    private TextView startLearning;
    private ProgressBar loadingBar;
    private boolean isLoading;

    private final Handler updateUIHandler = new Handler(Looper.getMainLooper());
    private APPDatabase appDatabase;
    private RecyclerView bookListRecyclerView;
    private BookListAdapter bookListAdapter;

    /**
     * 标题栏,主要用于显示当前是选词还是选语种的标题提示
     */
    private TextView title;

    /**
     * 用于监听当前用户选择了哪些章节
     */
    private BookViewModel bookViewModel;

    /**
     * 所有子划分
     */
    public static final String CHILD_DIVIDE_SET = "CHILD_DIVIDE_SET";
    /**
     * 历史单词
     */
    public static final String HISTORY_WORD_SET = "HISTORY_WORD_SET";
    /**
     * 复习单词
     */
    public static final String REVIEW_WORD_List = "REVIEW_WORD_List";
    /**
     * 选中的单词数量
     */
    public static final String SELECT_WORD_COUNT = "SELECT_WORD_COUNT";
    /**
     * 所有的书本以及其对应的信息
     */
    private List<WordBookWithSectionVO> wordBookWithSectionVOList;
    private UserSettingRepository userSettingRepository;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_book_list, container, false);
        // 初始化View
        bindView();
        initView();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.ib_book_list_start_learn) {
            if (!isLoading) {
                loadingBar.setVisibility(View.VISIBLE);
                StaticFactory.getExecutorService().submit(() -> {
                    // 拷贝Bean
                    Bundle bundle = new Bundle();
                    // 是否跳过偏好设置
                    userSettingRepository = UserSettingRepository.getInstance(APPDatabase.getInstance(getContext()).userSettingDao());
                    Boolean skipPreference = userSettingRepository.getUserSettingValue(UserSettingKeyEnums.SKIP_PREFERENCE);
                    // 首先将id转为String类型的List
                    //bundle.putSerializable(BookListFragment.CHILD_DIVIDE_SET, divideSet);
                    // 统计当前的选词量
                    int selectWordCount = 0;
                    Set<Integer> allSelectSection = Optional.ofNullable(bookViewModel.getSelectedSectionList().getValue())
                            .orElse(new HashSet<>());
                    for (WordBookWithSectionVO wordBookWithSectionVO : wordBookWithSectionVOList) {
                        for (WordBookSectionEntityVO wordBookSectionEntityVO : wordBookWithSectionVO.wordBookSectionEntityVOList) {
                            if (allSelectSection.contains(wordBookSectionEntityVO.wordBookSectionEntity.id)) {
                                selectWordCount += wordBookSectionEntityVO.elementCount;
                            }
                        }
                    }
                    bundle.putInt(BookListFragment.SELECT_WORD_COUNT, selectWordCount);
                    if (selectWordCount < 1) return;
                    // 是否进入背诵格式界面
                    updateUIHandler.post(() -> {
                        if (skipPreference) {
                            // todo 直接跳转到背诵界面
                        } else {
                            Intent intent = new Intent(requireContext(), WordReciteLaunchActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        loadingBar.setVisibility(View.INVISIBLE);
                    });
                });
            }
        }
    }

    @Override
    public void onClickCurrentPage(@NonNull MenuItem item) {
        // todo 添加滑动到顶部的方法
//        addToPlaneList.smoothScrollToPosition(RecyclerView.SCROLLBAR_POSITION_DEFAULT);
    }

    private void bindView() {
        this.viewPageChangeNavigationView = ((MainActivity) rootView.getContext()).findViewById(R.id.btn_main);
        this.startLearning = rootView.findViewById(R.id.ib_book_list_start_learn);
        this.loadingBar = rootView.findViewById(R.id.ib_book_list_loading_bar);
        this.title = rootView.findViewById(R.id.ib_book_list_title);
        this.bookListRecyclerView = rootView.findViewById(R.id.rv_book_list_parent);
        // 设置各种监听事件
        this.startLearning.setOnClickListener(this);
        // 初始化数据库
        this.appDatabase = APPDatabase.getInstance(requireContext());
    }

    private void initView() {
        this.title.setText(R.string.choose_chapter);
        bookViewModel = new ViewModelProvider(this)
                .get(BookViewModel.class);
        bookViewModel.getSelectedSectionList()
                .observe(getViewLifecycleOwner(), sectionList -> {
                    // 根据是否有选择,控制按钮状态和导航栏数字显示
                    if (sectionList.isEmpty()) {
                        startLearning.setVisibility(View.GONE);
                        viewPageChangeNavigationView.removeBadge(R.id.item_main_bottom_recite);
                    } else {
                        startLearning.setVisibility(View.VISIBLE);
                        viewPageChangeNavigationView.getOrCreateBadge(R.id.item_main_bottom_recite).setNumber(sectionList.size());
                        viewPageChangeNavigationView.getOrCreateBadge(R.id.item_main_bottom_recite).setBadgeGravity(BadgeDrawable.TOP_END);
                        viewPageChangeNavigationView.getOrCreateBadge(R.id.item_main_bottom_recite).setMaxCharacterCount(3);
                    }
                });
        this.bookListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        this.bookListAdapter = new BookListAdapter(requireContext(), bookViewModel);
        this.bookListRecyclerView.setAdapter(bookListAdapter);
        StaticFactory.getExecutorService().execute(() -> {
            // 查询所有的书籍
            List<WordBookWithSectionEntity> allBookAndSection = appDatabase.wordBookDao().findAllBookAndSections();
            allBookAndSection.forEach(wordBookWithSectionEntity -> wordBookWithSectionEntity.wordBookSectionEntityList.sort((o1, o2) -> o1.order - o2.order));
            // 将所有entity转为对应需要的VO使用
            wordBookWithSectionVOList = allBookAndSection.stream()
                    .map(wordBookWithSectionEntity -> {
                        List<WordBookSectionEntityVO> wordBookSectionEntityVOList = wordBookWithSectionEntity
                                .wordBookSectionEntityList
                                .stream()
                                .map(wordBookSectionEntity -> {
                                    WordBookSectionEntityVO result = new WordBookSectionEntityVO(wordBookSectionEntity);
                                    // 查询element_count
                                    result.elementCount = appDatabase.wordBookSectionDao().countBySectionId(wordBookSectionEntity.id);
                                    result.tagColor = MarkColor.valueOfName(result.wordBookSectionEntity.tagColor);
                                    return result;
                                })
                                .collect(Collectors.toList());
                        return new WordBookWithSectionVO(wordBookWithSectionEntity.wordBookEntity, wordBookSectionEntityVOList);
                    })
                    .collect(Collectors.toList());
            updateUIHandler.post(() -> bookListAdapter.replaceAll(wordBookWithSectionVOList));
        });
    }

}