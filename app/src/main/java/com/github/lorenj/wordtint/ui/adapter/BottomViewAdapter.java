package com.github.lorenj.wordtint.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class BottomViewAdapter extends FragmentStateAdapter {
    /**
     * 所有fragment的缓存
     */
    private final List<Fragment> mFragmentList;

    public BottomViewAdapter(@NonNull FragmentActivity fragmentActivity,
                             List<Fragment> mFragmentList) {
        super(fragmentActivity);
        this.mFragmentList = mFragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }
}
 
