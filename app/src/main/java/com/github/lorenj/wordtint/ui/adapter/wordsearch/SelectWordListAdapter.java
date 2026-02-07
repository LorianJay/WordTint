package com.github.lorenj.wordtint.ui.adapter.wordsearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.entity.WordSearchEntity;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * 这是一个标准的RecyclerViewAdapter
 */
public class SelectWordListAdapter extends RecyclerView.Adapter<SelectWordListAdapter.SelectWordViewHolder>
        implements RecyclerViewAdapterItemChange<WordSearchEntity> {

    private final Context context;
    /**
     * 所有单词选择列表
     */
    private final List<WordSearchEntity> allWordSearchList = new ArrayList<>();
    /**
     * 用于绑定当前选中单词的ViewModel
     */
    private final WordSearchViewModel wordSearchViewModel;

    public SelectWordListAdapter(Context context,
                                 WordSearchViewModel wordSearchViewModel) {
        this.context = context;
        this.wordSearchViewModel = wordSearchViewModel;
    }

    @NonNull
    @Override
    public SelectWordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectWordViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_word, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectWordViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WordSearchEntity wordSearchEntity = allWordSearchList.get(position);
        holder.wordOrigin.setText(wordSearchEntity.wordOrigin);
    }

    @Override
    public int getItemCount() {
        return allWordSearchList.size();
    }

    @Override
    public void replaceAll(Collection<WordSearchEntity> newWordList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new WordDiffCallback(this.allWordSearchList, new ArrayList<>(newWordList)));
        this.allWordSearchList.clear();
        this.allWordSearchList.addAll(newWordList);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void addAll(Collection<WordSearchEntity> moreData) {
        int startPosition = this.allWordSearchList.size();
        this.allWordSearchList.addAll(moreData);
        notifyItemRangeInserted(startPosition, moreData.size());
    }

    /**
     * 单词选择列表
     */
    public class SelectWordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final View itemView;
        private final TextView wordOrigin;
        private final LinearLayout choiceElementWord;

        public SelectWordViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.wordOrigin = itemView.findViewById(R.id.tv_item_search_word);
            this.choiceElementWord = itemView.findViewById(R.id.ll_item_search_word);
            this.choiceElementWord.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemId = v.getId();
            if (itemId == R.id.ll_item_search_word) {
                wordSearchViewModel.getCurrentSelectWord().setValue(allWordSearchList.get(getAbsoluteAdapterPosition()));
            }
        }
    }

}
