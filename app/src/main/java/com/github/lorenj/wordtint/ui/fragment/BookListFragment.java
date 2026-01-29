package com.github.lorenj.wordtint.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.pathsystem.document.UserInfoPath;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.entity.WordBookWithSection;
import com.github.lorenj.wordtint.entity.UserCreditStyle;
import com.github.lorenj.wordtint.entity.local.DivideDTOLocal;
import com.github.lorenj.wordtint.ui.MainActivity;
import com.github.lorenj.wordtint.ui.activity.WordReciteLaunchActivity;
import com.github.lorenj.wordtint.ui.adapter.book.BookListAdapter;
import com.github.lorenj.wordtint.ui.adapter.book.BookSectionListAdapter;
import com.github.lorenj.wordtint.ui.adapter.listener.NavigationItemSelectListener;
import com.github.lorenj.wordtint.utils.JsonUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class BookListFragment extends Fragment implements View.OnClickListener, NavigationItemSelectListener {

    private View rootView;
    private BottomNavigationView viewPageChangeNavigationView;
    /**
     * 开始学习的文本框
     */
    private TextView startLearning;
    private ProgressBar loadingBar;
    private boolean isLoading;
    private UserCreditStyle userCreditStyle;
    private final Handler updateUIHandler = new Handler(Looper.getMainLooper());
    private APPDatabase appDatabase;
    /**
     * 快速选择
     */
    private ImageButton quickChoose;
    private LinearLayout quickChoosePopWindowLayout;
    private View coreChoose, basisChoose, mockExamine;
    private RecyclerView bookListRecyclerView;
    private BookListAdapter bookListAdapter;

    /**
     * 标题栏,主要用于显示当前是选词还是选语种的标题提示
     */
    private TextView title;

    /**
     * 单词划分的fragment
     */
    private DivideFragment divideFragment;

    /**
     * 记录当前选中的所有子划分
     */
    private final HashSet<DivideDTOLocal> divideSet = new HashSet<>();

    /**
     * 用户背诵风格
     */
    public static final String USER_CREDIT_STYLE_WRAPPER = "USER_CREDIT_STYLE_WRAPPER";
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
     * 快速选择弹窗
     */
    private PopupWindow changeModePopupWindow;


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
                    try {
                        userCreditStyle = JsonUtils.readJson(UserInfoPath.USER_CREDIT_STYLE.getPath(), UserCreditStyle.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 拷贝Bean
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(BookListFragment.USER_CREDIT_STYLE_WRAPPER, userCreditStyle);
                    // 首先将id转为String类型的List
                    bundle.putSerializable(BookListFragment.CHILD_DIVIDE_SET, divideSet);
                    // 统计当前的选词量
                    int selectWordCount = 0;
                    for (DivideDTOLocal divideDTO : divideSet) {
                        selectWordCount += divideDTO.getWordIdList().size();
                    }
                    bundle.putInt(BookListFragment.SELECT_WORD_COUNT, selectWordCount);
                    // 设置当前的语种
                    updateUIHandler.post(() -> {
                        if (userCreditStyle.isIgnore()) {
                            Navigation.findNavController(getView()).navigate(R.id.action_navigation_main_to_word_credit, bundle,
                                    StaticFactory.getSimpleNavOptions());
                        } else {
                            Intent intent = new Intent(requireContext(), WordReciteLaunchActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        loadingBar.setVisibility(View.INVISIBLE);
                    });
                });
            }
        } else if (itemId == R.id.ib_book_list_quick) {
            this.changeModePopupWindow = new PopupWindow(
                    quickChoosePopWindowLayout,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            changeModePopupWindow.setOutsideTouchable(true);
            changeModePopupWindow.setFocusable(true);
            changeModePopupWindow.setAnimationStyle(R.style.pop_window_anim_style);
            changeModePopupWindow.showAsDropDown(quickChoose, -100, 0);
        } else if (itemId == R.id.fragment_word_credit_pop_listening_write_mode) {
            if (bookListRecyclerView == null) {
                this.bookListRecyclerView = divideFragment.getDivideRecyclerView();
            }
            BookSectionListAdapter bookSectionListAdapter = (BookSectionListAdapter) bookListRecyclerView.getAdapter();
        } else if (itemId == R.id.fragment_word_credit_pop_english_translation_chinese_hearing) {
            if (bookListRecyclerView == null) {
                this.bookListRecyclerView = divideFragment.getDivideRecyclerView();
            }
            BookSectionListAdapter bookSectionListAdapter = (BookSectionListAdapter) bookListRecyclerView.getAdapter();
        } else if (itemId == R.id.fragment_word_credit_pop_english_mock_examine) {
            changeModePopupWindow.dismiss();
            Navigation.findNavController(getView()).navigate(R.id.action_navigation_welcome_to_navigation_mock_examine, null,
                    StaticFactory.getSimpleNavOptions());
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
        this.quickChoose = rootView.findViewById(R.id.ib_book_list_quick);
        this.title = rootView.findViewById(R.id.ib_book_list_title);
        this.bookListRecyclerView = rootView.findViewById(R.id.rv_book_list_parent);

        this.quickChoosePopWindowLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.fragment_word_credit_quick_choose, null);
        this.coreChoose = this.quickChoosePopWindowLayout.findViewById(R.id.fragment_word_credit_pop_listening_write_mode);
        this.basisChoose = this.quickChoosePopWindowLayout.findViewById(R.id.fragment_word_credit_pop_english_translation_chinese_hearing);
        this.mockExamine = this.quickChoosePopWindowLayout.findViewById(R.id.fragment_word_credit_pop_english_mock_examine);

        // 设置各种监听事件
        this.startLearning.setOnClickListener(this);
        this.quickChoose.setOnClickListener(this);
        this.coreChoose.setOnClickListener(this);
        this.basisChoose.setOnClickListener(this);
        this.mockExamine.setOnClickListener(this);

        // 初始化数据库
        this.appDatabase = APPDatabase.getInstance(requireContext());
    }

    private void initView() {
        this.title.setText(R.string.add_to_plan);
        this.bookListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        this.bookListAdapter = new BookListAdapter(requireContext());
        this.bookListRecyclerView.setAdapter(bookListAdapter);
        StaticFactory.getExecutorService().execute(() -> {
            // 查询所有的书籍
            List<WordBookWithSection> allBookAndSection = appDatabase.wordBookDao().findAllBookAndSections();
            allBookAndSection.forEach(wordBookWithSection -> wordBookWithSection.wordBookSectionEntityList.sort((o1, o2) -> o1.order - o2.order));
            updateUIHandler.post(() -> bookListAdapter.replaceAll(allBookAndSection));
        });
    }

}