package com.github.lorenj.wordtint.ui.adapter.wordsearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.entity.WordSearchEntity;
import com.github.lorenj.wordtint.entity.local.WordDTOLocal;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.github.lorenj.wordtint.utils.DPUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * 这是一个标准的RecyclerViewAdapter
 */
public class SelectWordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerViewAdapterItemChange<WordSearchEntity> {

    private final Context context;
    /**
     * 所有单词选择列表
     */
    private final List<WordSearchEntity> allWordSearchList = new ArrayList<>();
    /**
     * 设置点击子划分的回调事件
     */
    private RecycleViewItemClickCallBack<WordDTOLocal> recycleViewItemOnClickListener;

    private final int LOAD_MORE_VIEW = 0;
    private final int WORD_VIEW = 1;

    public SelectWordListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == WORD_VIEW) {
            return new SelectWordViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_search_word_choice_element, parent, false));
        } else if (viewType == LOAD_MORE_VIEW) {
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.theme_color)));
            RelativeLayout loadMoreLinearLayout = new RelativeLayout(context);
            loadMoreLinearLayout.setGravity(Gravity.CENTER);
            loadMoreLinearLayout.addView(progressBar, RelativeLayout.LayoutParams.MATCH_PARENT, DPUtils.dp2px(20));
            return new LoadMoreViewHolder(loadMoreLinearLayout);
        }
        throw new RuntimeException();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof SelectWordViewHolder) {
            SelectWordViewHolder selectWordViewHolder = (SelectWordViewHolder) holder;
            WordSearchEntity wordSearchEntity = allWordSearchList.get(position);
            selectWordViewHolder.wordOrigin.setText(wordSearchEntity.wordOrigin);
        }
    }

    @Override
    public int getItemCount() {
        return allWordSearchList.size();
    }

    /**
     * 如果当前的position是selectWordPage的size表示当前这个组件是LoadMore组件
     */
    @Override
    public int getItemViewType(int position) {
        return allWordSearchList.get(position).wordId == -1 ?
                LOAD_MORE_VIEW : WORD_VIEW;
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
        int lastIndex = this.allWordSearchList.size() - 1;
        if (lastIndex >= 0 && this.allWordSearchList.get(lastIndex).wordId == -1) {
            this.allWordSearchList.remove(lastIndex);
            notifyItemRemoved(lastIndex);
        }
        int startPosition = this.allWordSearchList.size();
        this.allWordSearchList.addAll(moreData);
        notifyItemRangeInserted(startPosition, moreData.size());
    }

    @Override
    public void setRecycleViewItemClickCallBack(RecycleViewItemClickCallBack<WordSearchEntity> recycleViewItemClickCallBack) {
    }

    /**
     * 单词选择列表
     */
    public class SelectWordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView wordOrigin;
        private LinearLayout choiceElementWord;

        public SelectWordViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.wordOrigin = itemView.findViewById(R.id.fragment_search_word_choice_element_word_origin);
            this.choiceElementWord = itemView.findViewById(R.id.fragment_search_word_choice_element_word);
            this.choiceElementWord.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //recycleViewItemOnClickListener.viewClickCallBack(selectWordPage.getContent().get(getAdapterPosition()));
        }
    }

    /**
     * 加载更多的组件
     */
    public static class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

}
