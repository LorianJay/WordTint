package com.github.lorenj.wordtint.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.lorenj.wordtint.R;

public class TextSegmentationActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton backButton;
    private EditText inputEditText;
    private TextView segmentationButton;
    private TextView resultTextView;

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
        // 删除所有换行符
        String processed = input.replaceAll("\\r?\\n", "");
        // 检查是否以句号结尾
        boolean endsWithPeriod = processed.endsWith(".");
        // 在每个句号后添加两个换行符
        processed = processed.replace(".", ".\n\n");
        // 如果原文不以句号结尾, 移除末尾多余的换行符
        if (!endsWithPeriod && processed.endsWith("\n\n")) {
            processed = processed.substring(0, processed.length() - 2);
        }
        resultTextView.setText(processed);
    }

    private void initView() {
        backButton.setOnClickListener(this);
        segmentationButton.setOnClickListener(this);
    }

    private void bindView() {
        backButton = findViewById(R.id.ib_text_segmentation_back);
        inputEditText = findViewById(R.id.et_text_segmentation_input);
        segmentationButton = findViewById(R.id.btn_text_segmentation);
        resultTextView = findViewById(R.id.tv_text_segmentation_result);
    }


}
