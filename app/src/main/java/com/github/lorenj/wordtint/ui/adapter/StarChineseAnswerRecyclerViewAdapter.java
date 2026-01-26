package com.github.lorenj.wordtint.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.entity.local.WordDTOLocal;
import com.github.lorenj.wordtint.enums.structure.BaseStructure;
import com.github.lorenj.wordtint.enums.structure.EnglishStructure;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.R;

import java.util.Map;
import java.util.Optional;

public class StarChineseAnswerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerViewAdapterItemChange<WordDTOLocal> {

    private final Context context;
    private WordDTOLocal currentWord;

    private final Map<Integer, BaseStructure> metaInfoFilterMap;

    public StarChineseAnswerRecyclerViewAdapter(Context context, Long languageId) {
        this.context = context;
        metaInfoFilterMap = StaticFactory.getWordMetaInfoFilter().getMetaInfoFilterMap(languageId);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StarChineseAnswerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_drawer_chinese_answer_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof StarChineseAnswerViewHolder) {
            StarChineseAnswerViewHolder starChineseAnswerViewHolder = (StarChineseAnswerViewHolder) holder;
            // position对应wordStructureId
            BaseStructure baseStructure = Optional.ofNullable(metaInfoFilterMap.get(position)).orElse(EnglishStructure.DEFAULT);
            starChineseAnswerViewHolder.meaningCategoryHint.setText(context.getResources().getString(baseStructure.getTitleHint()));
            String value = currentWord.getValue().get(baseStructure);
            if (!TextUtils.isEmpty(value)) {
                starChineseAnswerViewHolder.meaningCategoryAnswer.setText(value);
                starChineseAnswerViewHolder.meaningCategoryHint.setVisibility(View.VISIBLE);
                starChineseAnswerViewHolder.meaningCategoryAnswer.setVisibility(View.VISIBLE);
            } else {
                starChineseAnswerViewHolder.meaningCategoryHint.setVisibility(View.GONE);
                starChineseAnswerViewHolder.meaningCategoryAnswer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return currentWord == null ? 0 : metaInfoFilterMap.size();
    }

    @Override
    public void addItem(WordDTOLocal item) {
        currentWord = item;
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public void removeItem(WordDTOLocal item) {

    }


    public static class StarChineseAnswerViewHolder extends RecyclerView.ViewHolder {
        private final TextView meaningCategoryHint, meaningCategoryAnswer;

        public StarChineseAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            meaningCategoryHint = itemView.findViewById(R.id.fragment_word_credit_meaning_category_hint);
            meaningCategoryAnswer = itemView.findViewById(R.id.fragment_word_credit_textview_meaning_category_answer);
        }
    }
}

