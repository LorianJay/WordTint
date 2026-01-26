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
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.entity.dto.DataPage;
import com.github.lorenj.wordtint.entity.local.WordDTOLocal;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.github.lorenj.wordtint.utils.DPUtils;
import com.github.lorenj.wordtint.R;


/**
 * 这是一个标准的RecyclerViewAdapter
 */
public class SelectWordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerViewAdapterItemChange<WordDTOLocal> {

    private final Context context;
    // 所有单词选择列表
    private DataPage<WordDTOLocal> selectWordPage;
    /**
     * 设置点击子划分的回调事件
     */
    private RecycleViewItemClickCallBack<WordDTOLocal> recycleViewItemOnClickListener;

    public SelectWordListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new SelectWordViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_search_word_choice_element, parent, false));
        } else {
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.theme_color)));
            RelativeLayout loadMoreLinearLayout = new RelativeLayout(context);
            loadMoreLinearLayout.setGravity(Gravity.CENTER);
            loadMoreLinearLayout.addView(progressBar, RelativeLayout.LayoutParams.MATCH_PARENT, DPUtils.dp2px(20));
            return new LoadMoreViewHolder(loadMoreLinearLayout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof SelectWordViewHolder) {
            SelectWordViewHolder selectWordViewHolder = (SelectWordViewHolder) holder;
            WordDTOLocal wordESDTO = selectWordPage.getContent().get(position);
            selectWordViewHolder.wordOrigin.setText(wordESDTO.getOrigin());
        }
    }

    @Override
    public int getItemCount() {
        if (selectWordPage != null) {
            // 如果是最后一个元素则不添加最后的loadMore视图
            if (selectWordPage.isLast()) {
                return selectWordPage.getContent().size();
            }
            // 否则多一个元素,该元素就是最后的LoadMore
            return selectWordPage.getContent().size() + 1;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        // 如果当前的position是selectWordPage的size表示当前这个组件是LoadMore组件
        if (position == selectWordPage.getContent().size()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void addItem(WordDTOLocal item) {

    }

    @Override
    public void removeItem(WordDTOLocal item) {

    }

    @Override
    public void addAllWithDataPage(DataPage<WordDTOLocal> dataPage) {
        // 这里需要拷贝响应的属性
        selectWordPage.setFirst(dataPage.isFirst());
        selectWordPage.setLast(dataPage.isLast());
        selectWordPage.getContent().addAll(dataPage.getContent());
        notifyItemRangeInserted(selectWordPage.getContent().size() - 1, dataPage.getContent().size());
    }

    @Override
    public void replaceAllWithDataPage(DataPage<WordDTOLocal> dataPage) {
        int preCount = getItemCount();
        // 必须先移除旧数据再添加新数据,否则会造成状态不一致
        notifyItemRangeRemoved(0, preCount);
        this.selectWordPage = dataPage;
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public void setRecycleViewItemClickCallBack(RecycleViewItemClickCallBack<WordDTOLocal> recycleViewItemClickCallBack) {
        this.recycleViewItemOnClickListener = recycleViewItemClickCallBack;
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
            recycleViewItemOnClickListener.viewClickCallBack(selectWordPage.getContent().get(getAdapterPosition()));
        }
    }

    /**
     * 加载更多的组件
     */
    public class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

}
