package com.github.lorenj.wordtint.ui.adapter.morefeatures.segmentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.vo.TextSegmentationVO;
import com.github.lorenj.wordtint.ui.adapter.customview.CenteringImageSpan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author lorianjay
 * @date 2026/7/9 21:58
 */
public class SegmentationHeaderAdapter extends RecyclerView.Adapter<SegmentationHeaderAdapter.SegmentationHeaderViewHolder> {

    private final Context context;
    private final TextSegmentationViewModel textSegmentationViewModel;

    public SegmentationHeaderAdapter(Context context, TextSegmentationViewModel textSegmentationViewModel) {
        this.context = context;
        this.textSegmentationViewModel = textSegmentationViewModel;
    }

    @NonNull
    @Override
    public SegmentationHeaderAdapter.SegmentationHeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SegmentationHeaderAdapter.SegmentationHeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_segmentation_header, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SegmentationHeaderAdapter.SegmentationHeaderViewHolder holder, @SuppressLint("RecyclerView") int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class SegmentationHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private EditText segmentationInput;
        private TextView textSegmentation;

        public SegmentationHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            segmentationInput = itemView.findViewById(R.id.et_text_segmentation_input);
            textSegmentation = itemView.findViewById(R.id.btn_text_segmentation);
            this.textSegmentation.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemId = v.getId();
            if (itemId == R.id.btn_text_segmentation) {
                textSegmentationViewModel.segmentText(segmentationInput.getText().toString());
            }
        }
    }

}
