package com.github.lorenj.wordtint.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.entity.local.WordAnalysisLocal;
import com.github.lorenj.wordtint.entity.local.WordDTOLocal;
import com.github.lorenj.wordtint.handler.WordAnalysisHandler;
import com.github.lorenj.wordtint.handler.impl.WordAnalysisHandlerImpl;
import com.github.lorenj.wordtint.ui.adapter.WordAnalysisProgressRecyclerViewAdapter;
import com.github.lorenj.wordtint.R;

public class AnalysisWordFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private ImageButton backToTrace;
    private WordDTOLocal currentWord;
    private WordAnalysisHandler wordAnalysisHandler;
    private WordAnalysisLocal currentWordShow;
    private RecyclerView analysisProgress;
    private Handler updateUIHandler;
    private TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            initView();
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_analysis_word, container, false);
        bindView();
        initView();
        return rootView;
    }

    private void showView() {
        WordAnalysisProgressRecyclerViewAdapter wordAnalysisProgressRecyclerViewAdapter = new WordAnalysisProgressRecyclerViewAdapter(getContext());
        wordAnalysisProgressRecyclerViewAdapter.addItem(currentWordShow);
        this.updateUIHandler.post(() -> {
            analysisProgress.setAdapter(wordAnalysisProgressRecyclerViewAdapter);
        });

    }

    private void initView() {
        this.title.setText(R.string.analysis_word);
        this.updateUIHandler = new Handler();
        this.wordAnalysisHandler = new WordAnalysisHandlerImpl(getContext());
        this.analysisProgress.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        Bundle bundle = getArguments();
        if (bundle != null) {
            // 得到当前要分析的单词
            //this.currentWord = bundle.getSerializable(WordCreditFragment.ANALYSIS_WORD, WordDTOLocal.class);
            StaticFactory
                    .getExecutorService()
                    .submit(() -> {
                        // 异步查询单词信息(可能比较费时)
                        currentWordShow = wordAnalysisHandler.queryWordAnalysis(Math.toIntExact(currentWord.getId()));
                        showView();
                    });

        }
    }

    private void bindView() {
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.analysisProgress = rootView.findViewById(R.id.fragment_analysis_progress);
        this.title = rootView.findViewById(R.id.toolbar_title);

        this.backToTrace.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.toolbar_back_to_trace) {
            Navigation.findNavController(getView()).popBackStack();
        }
    }
}