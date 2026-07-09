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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.vo.TextSegmentationVO;
import com.github.lorenj.wordtint.ui.adapter.morefeatures.segmentation.SegmentationListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TextSegmentationActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton backButton;
    private EditText inputEditText;
    private TextView segmentationButton;
    private RecyclerView segmentationList;
    /**
     * 适配器
     */
    private SegmentationListAdapter segmentationListAdapter;
    /**
     * 分段正则表达式
     */
    private final static String REGEX = "(?<!\\d|Mr|Ms|Dr|Vs|e\\.g|i\\.e)\\.(?!\\d|[a-zA-Z0-9-]+\\.[a-zA-Z])";
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
        } else if (itemId == R.id.btn_text_segmentation) {
            segmentText();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getCurrentFocus() != inputEditText) {
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
                if (focusedView instanceof TextView && focusedView != inputEditText) {
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
                    if (newFocus != inputEditText) {
                        selectionHandler.post(selectionCheckRunnable);
                    }
                });
    }

    /**
     * 文字分段: 删除所有换行符, 在句号结尾的句子后添加两个换行符
     */
    private void segmentText() {
        String input = inputEditText.getText().toString();
        if (input.isEmpty()) {
            return;
        }
        // 将所有换行符转为空格
        String processed = input.replaceAll("\\r?\\n", " ");
        StringBuilder format = new StringBuilder();
        List<TextSegmentationVO> allSegmentationList = Arrays.stream(processed.split(REGEX))
                .map(s -> {
                    format.setLength(0);
                    TextSegmentationVO textSegmentationVO = new TextSegmentationVO();
                    textSegmentationVO.setSegmentationText(format.append(s.trim()).append(".").toString());
                    return textSegmentationVO;
                })
                .collect(Collectors.toList());
        segmentationListAdapter.replaceAll(allSegmentationList);
    }

    private void initView() {
        LinearLayoutManager recordListLayoutManager = new LinearLayoutManager(this);
        this.segmentationList.setLayoutManager(recordListLayoutManager);
        this.segmentationListAdapter = new SegmentationListAdapter(this);
        this.segmentationList.setAdapter(segmentationListAdapter);
        backButton.setOnClickListener(this);
        segmentationButton.setOnClickListener(this);
    }

    private void bindView() {
        backButton = findViewById(R.id.ib_text_segmentation_back);
        inputEditText = findViewById(R.id.et_text_segmentation_input);
        segmentationButton = findViewById(R.id.btn_text_segmentation);
        segmentationList = findViewById(R.id.rv_segmentation_list);
    }


}
