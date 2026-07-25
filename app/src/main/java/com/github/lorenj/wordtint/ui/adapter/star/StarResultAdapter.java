package com.github.lorenj.wordtint.ui.adapter.star;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StarResultAdapter extends RecyclerView.Adapter<StarResultAdapter.ReciteResultViewHolder>
        implements RecyclerViewAdapterItemChange<FunctionWordVO> {

    private final Context context;
    private FunctionWordVO currentSectionWord;
    private List<WordStructure> wordStructureList = new ArrayList<>();

    public StarResultAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ReciteResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReciteResultViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recite_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReciteResultViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WordStructure currentWordStructure = wordStructureList.get(position);
        if (currentWordStructure == WordStructure.PHRASE) {
            holder.parent.setOrientation(LinearLayout.VERTICAL);
        } else {
            holder.parent.setOrientation(LinearLayout.HORIZONTAL);
        }
        String displayValue;
        String value = currentSectionWord.getValue().get(WordStructure.WORD_ORIGIN).value;
        String customValue = currentSectionWord.getValue().get(WordStructure.WORD_ORIGIN).customValue;
        if (customValue != null && !customValue.isEmpty()) {
            displayValue = customValue;
        } else {
            displayValue = value;
        }
        holder.resultValue.setText(displayValue.replace("\\n", "\n"));
        holder.resultKey.setText(currentWordStructure.getKeyHint());
    }

    @Override
    public void addItem(FunctionWordVO item) {
        this.currentSectionWord = item;
        this.wordStructureList.clear();
        List<WordStructure> collect = currentSectionWord.getValue()
                .keySet()
                .stream()
                .filter(wordStructure -> wordStructure != WordStructure.WORD_ORIGIN)
                .sorted((o1, o2) -> o1.getOrder() - o2.getOrder())
                .collect(Collectors.toList());
        this.wordStructureList.addAll(collect);
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public int getItemCount() {
        return wordStructureList.size();
    }

    public static class ReciteResultViewHolder extends RecyclerView.ViewHolder {
        private final TextView resultKey, resultValue;
        private final LinearLayout parent;

        public ReciteResultViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.ll_recite_result);
            resultKey = itemView.findViewById(R.id.tv_recite_result_key);
            resultValue = itemView.findViewById(R.id.tv_recite_result_value);
        }
    }
}

