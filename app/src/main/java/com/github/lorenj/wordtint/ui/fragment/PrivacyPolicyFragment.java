package com.github.lorenj.wordtint.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.R;

/**
 * 用户隐私政策页面
 */
public class PrivacyPolicyFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private TextView markDownTextView, title;
    private Handler updateUIHandler;
    private ProgressBar loadingBar;
    private ImageButton backToTrace;
    private String markdownOrigin;

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
        rootView = inflater.inflate(R.layout.fragment_user_agreement, container, false);
        updateUIHandler = new Handler();
        bindView();
        // 展示标题信息
        this.title.setText(getResources().getString(R.string.fragment_user_information_privacy_policy));
        showMarkDown();
        return rootView;
    }

    private void showMarkDown() {
        StaticFactory.getExecutorService().submit(() -> {
            StaticFactory
                    .getExecutorService()
                    .execute(() -> {
                    });

        });
    }

    @Override
    public void onClick(View v) {
        Navigation.findNavController(getView()).popBackStack();
    }

    private void bindView() {
        this.markDownTextView = rootView.findViewById(R.id.fragment_user_information_markdown_context);
        this.title = rootView.findViewById(R.id.toolbar_title);
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.loadingBar = rootView.findViewById(R.id.progress_bar_loading);
        this.backToTrace.setOnClickListener(this);
    }
}