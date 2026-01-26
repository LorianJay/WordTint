package com.github.lorenj.wordtint.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.lorenj.wordtint.ui.adapter.listener.NavigationItemSelectListener;
import com.github.lorenj.wordtint.R;

/**
 * @author sukidayo
 * @date Saturday, February 04, 2023
 */
public class HearingFragment extends Fragment implements NavigationItemSelectListener {


    private View rootView;
    private TextView spannable;

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
        rootView = inflater.inflate(R.layout.fragment_hearing, container, false);
        bindView();
        return rootView;
    }

    @Override
    public void onClickCurrentPage(@NonNull MenuItem item) {

    }

    private void bindView() {

    }


}