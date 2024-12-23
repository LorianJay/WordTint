package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.StartViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class RankFragment extends Fragment {

    private View rootView;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;
    private List<Fragment> listFragment;
    private String[] pageTitle;

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
        List<String> list = new ArrayList<>();
        this.listFragment = new ArrayList<>();
        for (FlagColor flagColor : FlagColor.values()) {
            if (flagColor == FlagColor.GREEN || flagColor == FlagColor.BROWN) {
                continue;
            }
            list.add(flagColor.name());
            listFragment.add(new FlagPageFragment(flagColor));
        }
        this.pageTitle = list.toArray(new String[]{});
        StartViewAdapter startViewAdapter = new StartViewAdapter(getChildFragmentManager(), listFragment);
        viewPager.setAdapter(startViewAdapter);
        slidingTabLayout.setViewPager(viewPager, pageTitle);
    }

    private void bindView() {
        this.viewPager = rootView.findViewById(R.id.fragment_i_start_viewpage);
        this.slidingTabLayout = rootView.findViewById(R.id.slide);
    }
}