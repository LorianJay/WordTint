package com.github.lorenj.wordtint.ui.adapter.morefeatures.segmentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.service.autofill.TextValueSanitizer;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.vo.ReciteRecordVO;
import com.github.lorenj.wordtint.database.vo.TextSegmentationVO;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.customview.CenteringImageSpan;
import com.github.lorenj.wordtint.ui.adapter.history.RecordDiffCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SegmentationListAdapter extends RecyclerView.Adapter<SegmentationListAdapter.SegmentationViewHolder>
        implements RecyclerViewAdapterItemChange<TextSegmentationVO> {

    private final Context context;
    private final List<TextSegmentationVO> allTextSegmentationList = new ArrayList<>();

    public SegmentationListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SegmentationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SegmentationViewHolder(LayoutInflater.from(context).inflate(R.layout.item_text_segmentation, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SegmentationViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TextSegmentationVO textSegmentationVO = allTextSegmentationList.get(position);
        String originalText = textSegmentationVO.getSegmentationText();

        if (originalText == null) {
            holder.segmentationResult.setText("");
            return;
        }

        // 1. 拼接占位空格
        String fullText = originalText + "  ";
        SpannableString spannableString = new SpannableString(fullText);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_copy);
        // 图标尺寸契合字体大小
        int iconSize = (int) (holder.segmentationResult.getTextSize() * 1.3f);
        drawable.setBounds(0, 0, iconSize, iconSize);

        // 居中对齐的 ImageSpan
        CenteringImageSpan imageSpan = new CenteringImageSpan(drawable);

        int startIndex = fullText.length() - 1;
        int endIndex = fullText.length();
        spannableString.setSpan(imageSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 绑定点击事件到该图标区域
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                copyToClipboard(originalText);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 4. 允许富文本点击，并移除默认的高亮背景
        holder.segmentationResult.setMovementMethod(LinkMovementMethod.getInstance());

        // 5. 送入渲染
        holder.segmentationResult.setText(spannableString);
    }

    @Override
    public int getItemCount() {
        return allTextSegmentationList.size();
    }

    @Override
    public void replaceAll(Collection<TextSegmentationVO> newSegmentationList) {
        this.allTextSegmentationList.clear();
        this.allTextSegmentationList.addAll(newSegmentationList);
        notifyItemRangeChanged(0, allTextSegmentationList.size());
    }

    /**
     * 提取出的通用复制方法
     */
    private void copyToClipboard(String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("copied_text", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, context.getString(R.string.text_copied), Toast.LENGTH_SHORT).show();
        }
    }

    public class SegmentationViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView segmentationResult;

        public SegmentationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.segmentationResult = itemView.findViewById(R.id.tv_text_segmentation_result);
        }
    }

}
