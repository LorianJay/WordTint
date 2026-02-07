package com.github.lorenj.wordtint.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.ui.activity.MockExamineActivity;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private View rootView;

    /**
     * UI
     */
    private TextView currentVersion;
    private RelativeLayout mockExamine;

    public SettingFragment() {
    }

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
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        bindView();
        initView();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();
        if (clickId == R.id.rl_setting_mock) {
            Intent intent = new Intent(getActivity(), MockExamineActivity.class);
            startActivity(intent);
        }
    }

    private void initView() {
        try {
            PackageManager packageManager = getContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            currentVersion.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void bindView() {
        currentVersion = rootView.findViewById(R.id.tx_setting_version);
        mockExamine = rootView.findViewById(R.id.rl_setting_mock);


        mockExamine.setOnClickListener(this);
    }

}