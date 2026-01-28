package com.github.lorenj.wordtint.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.github.lorenj.wordtint.context.pathsystem.document.UserInfoPath;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.entity.UserCreditStyle;
import com.github.lorenj.wordtint.entity.waper.UserCreditStyleWrapper;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.handler.WordAnalysisHandler;
import com.github.lorenj.wordtint.database.WordSupplementReviewHandler;
import com.github.lorenj.wordtint.handler.impl.WordAnalysisHandlerImpl;
import com.github.lorenj.wordtint.database.impl.WordSupplementReviewHandlerImpl;
import com.github.lorenj.wordtint.ui.adapter.StartViewAdapter;
import com.github.lorenj.wordtint.utils.JsonUtils;
import com.github.lorenj.wordtint.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class RankFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private View rootView;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;
    private List<Fragment> listFragment;
    private ArrayList<MarkColor> markColorList;
    private ImageButton creditComplete, creditSupplement;
    private TextView completeCount, supplementCount;
    private WordAnalysisHandler wordAnalysisHandler;
    private WordSupplementReviewHandler wordSupplementReviewHandler;
    private Handler updateUIHandler;
    // 当前选中的标记
    private MarkColor currentMarkColor;
    // 背词风格
    private UserCreditStyle userCreditStyle;
    private AlertDialog loadingDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_rank, container, false);
        bindView();
        initView();
        return rootView;
    }

    private void initView() {
        this.wordAnalysisHandler = new WordAnalysisHandlerImpl(getContext());
        this.wordSupplementReviewHandler = new WordSupplementReviewHandlerImpl(getContext());
        this.markColorList = new ArrayList<>();
        this.listFragment = new ArrayList<>();
        this.updateUIHandler = new Handler();
        loadingDialog = new AlertDialog.Builder(getContext()).setView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null)).setCancelable(false).create();

        for (MarkColor markColor : MarkColor.values()) {
            if (markColor == MarkColor.GREEN || markColor == MarkColor.BROWN) {
                continue;
            }
            markColorList.add(markColor);
            listFragment.add(new FlagPageFragment(markColor, wordAnalysisHandler));
        }
        String[] pageTitle = markColorList.stream()
                .map(Enum::name)
                .collect(Collectors.toList())
                .toArray(new String[]{});
        StartViewAdapter startViewAdapter = new StartViewAdapter(getChildFragmentManager(), listFragment);
        viewPager.setAdapter(startViewAdapter);
        slidingTabLayout.setViewPager(viewPager, pageTitle);
        onPageSelected(0);
    }

    private void bindView() {
        this.viewPager = rootView.findViewById(R.id.fragment_i_start_viewpage);
        this.slidingTabLayout = rootView.findViewById(R.id.slide);
        this.creditComplete = rootView.findViewById(R.id.fragment_rank_complete);
        this.creditSupplement = rootView.findViewById(R.id.fragment_rank_supplement);
        this.completeCount = rootView.findViewById(R.id.fragment_rank_complete_count);
        this.supplementCount = rootView.findViewById(R.id.fragment_rank_supplement_count);

        this.viewPager.addOnPageChangeListener(this);
        this.creditComplete.setOnClickListener(this);
        this.creditSupplement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        loadingDialog.show();
        StaticFactory.getExecutorService().submit(() -> {
            // 读取用户背诵风格
            try {
                userCreditStyle = JsonUtils.readJson(UserInfoPath.USER_CREDIT_STYLE.getPath(), UserCreditStyle.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 拷贝Bean
            UserCreditStyleWrapper userCreditStyleWrapper = new UserCreditStyleWrapper(userCreditStyle);

            ArrayList<Long> currentFlagWord = null;
            if (itemId == R.id.fragment_rank_complete) {
                currentFlagWord = wordAnalysisHandler.queryFlagRankByFlagColor(currentMarkColor);
            } else if (itemId == R.id.fragment_rank_supplement) {
                currentFlagWord = wordSupplementReviewHandler.querySupplementByFlagColor(currentMarkColor);
                userCreditStyle.setReview(true);
            }

            Bundle bundle = new Bundle();
            bundle.putParcelable(BookListFragment.USER_CREDIT_STYLE_WRAPPER, userCreditStyleWrapper);
            // 设置背诵列表
            bundle.putSerializable(BookListFragment.REVIEW_WORD_List, currentFlagWord);
            // 统计当前的选词量
            bundle.putInt(BookListFragment.SELECT_WORD_COUNT, currentFlagWord.size());
            updateUIHandler.post(() -> {
                if (userCreditStyle.isIgnore()) {
                    Navigation.findNavController(getView()).navigate(R.id.action_navigation_main_to_word_credit, bundle,
                            StaticFactory.getSimpleNavOptions());
                } else {
                    Navigation.findNavController(getView()).navigate(R.id.action_main_navigation_to_navigation_word_credit_launch, bundle,
                            StaticFactory.getSimpleNavOptions());
                }
                loadingDialog.dismiss();
            });
        });
    }

    @Override
    public void onPageSelected(int position) {
        // 更改旗帜颜色
        this.currentMarkColor = markColorList.get(position);
        this.creditComplete.getDrawable().setTint(getResources().getColor(currentMarkColor.getMapColorID(), null));
        // 更改总数
        StaticFactory.getExecutorService().submit(() -> {
            int count = wordAnalysisHandler.countFlagRankByFlagColor(currentMarkColor);
            int supplementCount = wordSupplementReviewHandler.countSupplementFlagColor(currentMarkColor);
            updateUIHandler.post(() -> {
                this.completeCount.setText(String.valueOf(count));
                this.supplementCount.setText(String.valueOf(supplementCount));
            });
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}