package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.pathsystem.document.UserInfoPath;
import com.gitee.cnsukidayo.anylanguageword.context.support.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.entity.UserCreditStyle;
import com.gitee.cnsukidayo.anylanguageword.entity.local.DivideDTOLocal;
import com.gitee.cnsukidayo.anylanguageword.entity.waper.UserCreditStyleWrapper;
import com.gitee.cnsukidayo.anylanguageword.ui.MainActivity;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.NavigationItemSelectListener;
import com.gitee.cnsukidayo.anylanguageword.utils.JsonUtils;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

public class CreditFragment extends Fragment implements View.OnClickListener, NavigationItemSelectListener {

    private View rootView;
    private BottomNavigationView viewPageChangeNavigationView;
    /**
     * 开始学习的文本框
     */
    private TextView startLearning;
    private ProgressBar loadingBar;
    private boolean isLoading;
    private UserCreditStyle userCreditStyle;
    private final Handler updateUIHandler = new Handler();
    private FragmentManager fragmentManager;
    /**
     * backup按钮,作用是从选词界面切换到选语种界面
     */
    private ImageButton switchLanguageClass;

    /**
     * 标题栏,主要用于显示当前是选词还是选语种的标题提示
     */
    private TextView title;

    /**
     * 语种的fragment
     */
    private LanguageClassFragment languageClassFragment;

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
     * 选中的单词数量
     */
    public static final String SELECT_WORD_COUNT = "SELECT_WORD_COUNT";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_credit, container, false);
        // 初始化View
        bindView();
        initView();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_credit_start_credit:
                if (!isLoading) {
                    loadingBar.setVisibility(View.VISIBLE);
                    StaticFactory.getExecutorService().submit(() -> {
                        try {
                            userCreditStyle = JsonUtils.readJson(UserInfoPath.USER_CREDIT_STYLE.getPath(), UserCreditStyle.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // 拷贝Bean
                        UserCreditStyleWrapper userCreditStyleWrapper = new UserCreditStyleWrapper(userCreditStyle);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(CreditFragment.USER_CREDIT_STYLE_WRAPPER, userCreditStyleWrapper);
                        // 首先将id转为String类型的List
                        bundle.putSerializable(CreditFragment.CHILD_DIVIDE_SET, divideSet);
                        // 统计当前的选词量
                        int selectWordCount = 0;
                        for (DivideDTOLocal divideDTO : divideSet) {
                            selectWordCount += divideDTO.getWordIdList().size();
                        }
                        bundle.putInt(CreditFragment.SELECT_WORD_COUNT, selectWordCount);
                        // 设置当前的语种
                        updateUIHandler.post(() -> {
                            if (userCreditStyle.isIgnore()) {
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
                break;
            case R.id.fragment_credit_divide_backup:
                languageClassRecyclerView();
                break;
        }
    }

    @Override
    public void onClickCurrentPage(@NonNull MenuItem item) {
        // todo 添加滑动到顶部的方法
//        addToPlaneList.smoothScrollToPosition(RecyclerView.SCROLLBAR_POSITION_DEFAULT);
    }

    /**
     * 语种界面
     */
    private void languageClassRecyclerView() {
        // 隐藏返回按钮
        this.switchLanguageClass.setVisibility(View.GONE);
        this.startLearning.setVisibility(View.GONE);
        // 设置标题信息为选择语种
        this.title.setText(R.string.select_language_class);
        divideSet.clear();
        viewPageChangeNavigationView.removeBadge(R.id.fragment_main_bottom_recite);

        this.fragmentManager = getChildFragmentManager();
        // 开启事务，获得FragmentTransaction对象
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 向容器内添加或替换碎片,默认情况下为LanguageClassFragment
        // 点击语种后发送请求,调用divideRecyclerViewLanguageClassFragment
        languageClassFragment = Optional.ofNullable(languageClassFragment).orElse(new LanguageClassFragment());
        transaction.replace(R.id.fragment_credit_frame_layout, languageClassFragment);
        // 提交事务
        transaction.commit();
    }


    /**
     * 单词划分的recycleView
     *
     * @param languageClassDTO 展示哪个语种
     */
    private void divideRecyclerView() {
        // 显示返回按钮
        this.switchLanguageClass.setVisibility(View.GONE);
        this.startLearning.setVisibility(View.VISIBLE);
        // 设置标题信息为添加划分到列表
        this.title.setText(R.string.add_to_plan);
        this.fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        divideFragment = new DivideFragment();
        transaction.replace(R.id.fragment_credit_frame_layout, divideFragment);
        // 设置点击某个子划分后的回调事件
        divideFragment.setRecycleViewItemOnClickListener(divideDTO -> {
            if (divideSet.contains(divideDTO)) {
                divideSet.remove(divideDTO);
            } else {
                divideSet.add(divideDTO);
            }
            if (divideSet.size() < 1) {
                viewPageChangeNavigationView.removeBadge(R.id.fragment_main_bottom_recite);
            } else {
                viewPageChangeNavigationView.getOrCreateBadge(R.id.fragment_main_bottom_recite).setNumber(divideSet.size());
                viewPageChangeNavigationView.getOrCreateBadge(R.id.fragment_main_bottom_recite).setBadgeGravity(BadgeDrawable.TOP_END);
                viewPageChangeNavigationView.getOrCreateBadge(R.id.fragment_main_bottom_recite).setMaxCharacterCount(3);
            }
        });
        // 提交事务
        transaction.commit();
    }

    private void bindView() {
        this.viewPageChangeNavigationView = ((MainActivity) rootView.getContext()).findViewById(R.id.fragment_home_navigation_view);
        this.startLearning = rootView.findViewById(R.id.fragment_credit_start_credit);
        this.loadingBar = rootView.findViewById(R.id.credit_fragment_loading_bar);
        this.switchLanguageClass = rootView.findViewById(R.id.fragment_credit_divide_backup);
        this.title = rootView.findViewById(R.id.fragment_credit_title);
    }

    private void initView() {
        // 设置各种监听事件
        this.startLearning.setOnClickListener(this);
        // 设置fragment切换逻辑 语种与划分之间的切换显示
        divideRecyclerView();
        this.switchLanguageClass.setOnClickListener(this);
    }

}