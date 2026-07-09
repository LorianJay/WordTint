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
        String[] lines = processed.split(REGEX);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            sb.append(lines[i].trim());
            if (i < lines.length - 1) {
                sb.append(".").append("\n\n\n");
            }
        }
        sb.append("\n\n\n");
        processed = sb.toString();
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
