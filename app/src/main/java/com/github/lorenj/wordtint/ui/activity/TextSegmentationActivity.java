package com.github.lorenj.wordtint.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_segmentation);
        bindView();
        initView();
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
