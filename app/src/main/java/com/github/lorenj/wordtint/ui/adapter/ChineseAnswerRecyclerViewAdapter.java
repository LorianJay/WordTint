package com.github.lorenj.wordtint.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.entity.local.WordDTOLocal;
import com.github.lorenj.wordtint.enums.structure.EnglishStructure;
import com.github.lorenj.wordtint.ui.adapter.support.answer.AnswerElement;
import com.github.lorenj.wordtint.R;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChineseAnswerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;

    // 当前的RecyclerViewAdapter是可以复用的,根据不同的界面,组件有不同的样式
    private RecyclerViewState recyclerViewState = RecyclerViewState.MAIN;
    /**
     * 当前单词详细信息
     */
    private WordDTOLocal currentWord;
    /**
     * 存储所有单词组装信息的集合
     */
    private List<AnswerElement> answerElementList = new ArrayList<>();

    /**
     * 计算文本宽度的工具
     */
    private final Paint textWidthHandler = new Paint();

    /**
     * 字体的大小
     */
    private final float textSize;

    /**
     * 文本的最大宽度
     */
    private float maxTextWidth;

    /**
     * 暂存的文本长度
     */
    private float previousTextWidth;

    public ChineseAnswerRecyclerViewAdapter(Context context) {
        this.context = context;
        TextView textView = LayoutInflater.from(context).inflate(R.layout.item_recite_result, null)
                .findViewById(R.id.tv_recite_result_key);
        textSize = textView.getTextSize();
        textWidthHandler.setTextSize(textSize);
    }

    /**
     * 展示一个单词的中文意思,该方法只负责将组件展示出来.至于哪些组件要动态地隐藏,由调用者决定.
     *
     * @param currentWordMap 待展示的单词
     */
    public void showWordChineseMessage(WordDTOLocal currentWord) {
        int preCount = answerElementList.size();
        // 拷贝当前Map,单词结构信息不允许修改
        this.currentWord = currentWord;
        this.answerElementList = viewResolve(currentWord);
        notifyItemRangeChanged(0, Math.max(getItemCount(), preCount));
    }

    public void setRecyclerViewState(RecyclerViewState recyclerViewState) {
        this.recyclerViewState = recyclerViewState;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 根据不同的状态,解析不同的页面
        if (viewType == RecyclerViewState.DRAWER.getViewType()) {
            return new AnswerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recite_result, parent, false));
        }
        return new AnswerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_chinese_answer_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof AnswerViewHolder) {
            AnswerViewHolder answerViewHolder = (AnswerViewHolder) holder;
            AnswerElement answerElement = answerElementList.get(position);
            answerViewHolder.meaningCategoryHint.setText(answerElement.getKey());
            answerViewHolder.meaningCategoryHint.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = answerViewHolder.itemView.getLayoutParams();
            if (layoutParams instanceof FlexboxLayoutManager.LayoutParams) {
                FlexboxLayoutManager.LayoutParams flexLayoutParams = (FlexboxLayoutManager.LayoutParams) layoutParams;
                // 重置缓存
                flexLayoutParams.setWrapBefore(answerElement.hasBreak());
            }
        } else if (holder instanceof DrawerAnswerViewHolder) {
        }
    }

    @Override
    public int getItemCount() {
        return answerElementList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return recyclerViewState.getViewType();
    }

    /**
     * 得到当前最长的文本宽度
     *
     * @return 返回文本宽度值, 用于动态设置recycleView的宽度
     */
    public float getMaxTextWidth() {
        return maxTextWidth;
    }


    /**
     * 解析视图
     *
     * @param currentWordMap 单词结构体
     */
    private List<AnswerElement> viewResolve(WordDTOLocal currentWord) {
        Set<Map.Entry<EnglishStructure, String>> values = currentWord.getValue().entrySet();
        List<AnswerElement> renderingResult = new ArrayList<>(values.size() * 2);
        for (Map.Entry<EnglishStructure, String> value : values) {
            EnglishStructure key = value.getKey();
            renderingResult.add(new AnswerElement.Builder()
                    .key(context.getResources().getString(key.getTitleHint()))
                    .order(key.getOrder())
                    .hasBreak(key.isTitleBreak())
                    .build());
            renderingResult.add(new AnswerElement.Builder()
                    .key(value.getValue())
                    .order(key.getOrder())
                    .build());

        }
        // 计算最长文本宽度
        for (AnswerElement computeNode : renderingResult) {
            previousTextWidth += textWidthHandler.measureText(computeNode.getKey());
            if (computeNode.hasBreak()) {
                previousTextWidth = 0;
                maxTextWidth = Math.max(previousTextWidth, maxTextWidth);
            }
        }
        return renderingResult;
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private final TextView meaningCategoryHint, meaningCategoryAnswer;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            meaningCategoryHint = this.itemView.findViewById(R.id.tv_recite_result_key);
            meaningCategoryAnswer = this.itemView.findViewById(R.id.tv_recite_result_value);
        }
    }

    public static class DrawerAnswerViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private final TextView meaningCategoryHint, meaningCategoryAnswer;

        public DrawerAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            meaningCategoryHint = this.itemView.findViewById(R.id.tv_recite_result_key);
            meaningCategoryAnswer = this.itemView.findViewById(R.id.tv_recite_result_value);
        }
    }


    public enum RecyclerViewState {
        MAIN(0),
        DRAWER(2);

        private final int viewType;

        RecyclerViewState(int viewType) {
            this.viewType = viewType;
        }

        public int getViewType() {
            return viewType;
        }
    }

}
