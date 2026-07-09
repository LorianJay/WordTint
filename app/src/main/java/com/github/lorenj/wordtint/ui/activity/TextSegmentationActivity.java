package com.github.lorenj.wordtint.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.vo.TextSegmentationVO;
import com.github.lorenj.wordtint.ui.adapter.morefeatures.segmentation.SegmentationHeaderAdapter;
import com.github.lorenj.wordtint.ui.adapter.morefeatures.segmentation.SegmentationListAdapter;
import com.github.lorenj.wordtint.ui.adapter.morefeatures.segmentation.TextSegmentationViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TextSegmentationActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton backButton;
    private RecyclerView segmentationList;
    /**
     * 适配器
     */
    private SegmentationHeaderAdapter segmentationHeaderAdapter;
    private SegmentationListAdapter segmentationListAdapter;
    private TextSegmentationViewModel textSegmentationViewModel;

    /**
     * 选择查词功能
     */
    private final Handler selectionHandler = new Handler(Looper.getMainLooper());
    private Runnable selectionCheckRunnable;
    private String lastSelectedWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_segmentation);
        bindView();
        initView();
        initGlobalSelectionListener();
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.ib_text_segmentation_back) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getCurrentFocus() == null) return;
        if (getCurrentFocus().getId() != R.id.et_text_segmentation_input) {
            selectionHandler.post(selectionCheckRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        selectionHandler.removeCallbacks(selectionCheckRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectionHandler.removeCallbacks(selectionCheckRunnable);
    }

    private void initGlobalSelectionListener() {
        selectionCheckRunnable = new Runnable() {
            @Override
            public void run() {
                View focusedView = getCurrentFocus();
                if (focusedView instanceof TextView && focusedView.getId() != R.id.et_text_segmentation_input) {
                    TextView tv = (TextView) focusedView;
                    int start = tv.getSelectionStart();
                    int end = tv.getSelectionEnd();
                    if (start != end && start != -1 && end != -1) {
                        int min = Math.max(0, Math.min(start, end));
                        int max = Math.max(0, Math.max(start, end));
                        CharSequence text = tv.getText();
                        if (max <= text.length()) {
                            String currentWord = text.subSequence(min, max).toString().trim();
                            if (!currentWord.isEmpty() && !currentWord.equals(lastSelectedWord)) {
                                lastSelectedWord = currentWord;
                                // 调出查词面板

                            }
                        }
                    }
                }
                // 每 150 毫秒微调轮询一次,保障拖动时能有极高的响应速度
                selectionHandler.postDelayed(this, 150);
            }
        };
        // 当页面布局渲染或焦点改变时,确保启动/恢复轮询器
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalFocusChangeListener(
                (oldFocus, newFocus) -> {
                    selectionHandler.removeCallbacks(selectionCheckRunnable);
                    // 如果新焦点移出了输入框,进入了列表,开始高频捕获
                    if (newFocus.getId() != R.id.et_text_segmentation_input) {
                        selectionHandler.post(selectionCheckRunnable);
                    }
                });
    }

    private void initView() {
        textSegmentationViewModel = new ViewModelProvider(this).get(TextSegmentationViewModel.class);
        LinearLayoutManager recordListLayoutManager = new LinearLayoutManager(this);
        this.segmentationList.setLayoutManager(recordListLayoutManager);
        this.segmentationHeaderAdapter = new SegmentationHeaderAdapter(this, textSegmentationViewModel);
        this.segmentationListAdapter = new SegmentationListAdapter(this);
        ConcatAdapter concatAdapter = new ConcatAdapter(segmentationHeaderAdapter, segmentationListAdapter);
        this.segmentationList.setAdapter(concatAdapter);
        textSegmentationViewModel
                .getSegmentationList()
                .observe(this, list -> {
                    if (list == null || list.isEmpty()) return;
                    segmentationListAdapter.replaceAll(list);
                });
        backButton.setOnClickListener(this);
    }

    private void bindView() {
        backButton = findViewById(R.id.ib_text_segmentation_back);
        segmentationList = findViewById(R.id.rv_segmentation_list);
    }


}
