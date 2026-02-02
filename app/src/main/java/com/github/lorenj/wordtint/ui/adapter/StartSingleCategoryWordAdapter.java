package com.github.lorenj.wordtint.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.entity.dto.WordCategoryWordDTO;
import com.github.lorenj.wordtint.handler.StarSectionFunctionHandler;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.listener.MoveAndSwipedListener;
import com.github.lorenj.wordtint.ui.adapter.listener.StateChangedListener;


/**
 * 每个分类中的每个单词Adapter
 *
 * @author cnsukidayo
 * @date 2023/1/9 14:35
 */
public class StartSingleCategoryWordAdapter extends RecyclerView.Adapter<StartSingleCategoryWordAdapter.SingleCategoryWordViewHolder> implements
        MoveAndSwipedListener, RecyclerViewAdapterItemChange<WordCategoryWordDTO> {

    private final Context context;
    private FunctionContentCallBack functionContentCallBack;
    // 用于处理单词收藏功能的Handler
    private StarSectionFunctionHandler starSectionFunctionHandler;
    // 当前是否正在移动单词的标识
    private volatile boolean itemMoving = false;

    public StartSingleCategoryWordAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SingleCategoryWordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SingleCategoryWordViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_start_single_category_word_element, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SingleCategoryWordViewHolder holder, @SuppressLint("RecyclerView") int position) {
        /*
        // 将第一根线设置为别的颜色
        if (position == 0) {
            holder.separator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light, null));
        } else {
            holder.separator.setBackgroundColor(context.getResources().getColor(R.color.dark_gray, null));
        }
        if (itemMoving) {
            // 如果物体正在移动,则只更新线条颜色;
            return;
        }
        // 重置改变，防止由于复用而导致的显示问题
        holder.scroller.scrollTo(0, 0);
        WordDTOLocal wordDTOLocal = categoryWordFunctionHandler.getWordDetailByWordId(functionContentCallBack.getCurrentWordCategoryPosition(), position);
        Optional.ofNullable(wordDTOLocal.getValue().get(EnglishStructure.WORD_ORIGIN))
                .ifPresent(holder.wordOrigin::setText);
        // 最后一个嵌套,单词中文意思的嵌套
        holder.chineseAnswerRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.starResultAdapter = new StarResultAdapter(context, 2L);
        holder.chineseAnswerRecyclerView.setAdapter(holder.starResultAdapter);
        //holder.starResultAdapter.addItem(categoryWordFunctionHandler.getWordDetailByWordId(functionContentCallBack.getCurrentWordCategoryPosition(), position));
        String phraseTranslation = Optional.ofNullable(wordDTOLocal.getValue().get(EnglishStructure.PHRASE_TRANSLATION))
                .orElse("");
        // 设置介词短语
        Optional.ofNullable(wordDTOLocal.getValue().get(EnglishStructure.PHRASE))
                .ifPresentOrElse(wordDTOS -> {
                            holder.phraseAnswer.setText(wordDTOS +
                                    " " +
                                    phraseTranslation);
                            holder.phraseHint.setVisibility(View.VISIBLE);
                            holder.phraseAnswer.setVisibility(View.VISIBLE);
                        }
                        , () -> {
                            holder.phraseHint.setVisibility(View.GONE);
                            holder.phraseAnswer.setVisibility(View.GONE);
                        });
         */
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        //return categoryWordFunctionHandler.getStarWordCount(functionContentCallBack.getCurrentWordCategoryPosition());
        return 0;
    }

    public void onItemMove(int fromPosition, int toPosition) {
        //categoryWordFunctionHandler.moveStarInnerWord(functionContentCallBack.getCurrentWordCategoryPosition(), fromPosition, toPosition);
        functionContentCallBack.updateCategoryMessage();
        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(fromPosition, Boolean.FALSE);
        notifyItemChanged(toPosition, Boolean.FALSE);
    }

    @Override
    public void onItemDismiss(int position) {
    }

    public void setCategoryWordFunctionHandler(StarSectionFunctionHandler starSectionFunctionHandler) {
        this.starSectionFunctionHandler = starSectionFunctionHandler;
    }

    public void setFunctionListener(FunctionContentCallBack functionContentCallBack) {
        this.functionContentCallBack = functionContentCallBack;
    }

    @Override
    public void addItem(WordCategoryWordDTO wordCategoryWordDTO) {
        //categoryWordFunctionHandler.addWordToStar(functionContentCallBack.getCurrentWordCategoryPosition(), wordCategoryWordDTO);
        //notifyItemChanged(categoryWordFunctionHandler.getStarWordCount(functionContentCallBack.getCurrentWordCategoryPosition()) - 1);
    }

    @Override
    public void removeItem(WordCategoryWordDTO item) {

    }

    public class SingleCategoryWordViewHolder extends RecyclerView.ViewHolder implements
            StateChangedListener, View.OnTouchListener, View.OnClickListener {
        // separator颜色线条
        private final View itemView, scroller, separator;
        private final TextView delete, wordOrigin, phraseAnswer, phraseHint;
        private final ImageButton move;
        private final RecyclerView chineseAnswerRecyclerView;
        private StarResultAdapter starResultAdapter;

        public SingleCategoryWordViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.delete = itemView.findViewById(R.id.fragment_word_credit_start_word_delete);
            this.scroller = itemView.findViewById(R.id.fragment_word_credit_start_single_category_word_scroller);
            this.move = itemView.findViewById(R.id.fragment_word_credit_start_word_move);
            this.wordOrigin = itemView.findViewById(R.id.fragment_word_credit_start_single_category_word_origin);
            this.chineseAnswerRecyclerView = itemView.findViewById(R.id.fragment_word_credit_start_category_chinese_answer);
            this.phraseHint = itemView.findViewById(R.id.fragment_word_credit_start_phrase_hint);
            this.phraseAnswer = itemView.findViewById(R.id.fragment_word_credit_start_phrase_answer);
            this.separator = itemView.findViewById(R.id.fragment_word_credit_start_single_category_word_separator);
            this.move.setOnTouchListener(this);
            this.delete.setOnClickListener(this);
        }

        @Override
        public void onItemSelected() {
            itemView.setAlpha(0.5f);
            itemMoving = true;
        }

        @Override
        public void onItemClear() {
            itemView.setAlpha(1.0f);
            itemMoving = false;
            // 批量更新本次移动的情况
            //categoryWordFunctionHandler.batchUpdateStarInnerWordList(functionContentCallBack.getCurrentWordCategoryPosition());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int clickViewId = v.getId();
            if (clickViewId == R.id.fragment_word_credit_start_word_move &&
                    event.getAction() == MotionEvent.ACTION_DOWN &&
                    functionContentCallBack != null) {
                functionContentCallBack.startDrag(this);
            }
            return false;
        }


        @Override
        public void onClick(View v) {
            int clickViewId = v.getId();
            if (clickViewId == R.id.fragment_word_credit_start_word_delete) {
                //categoryWordFunctionHandler.removeWordFromStar(functionContentCallBack.getCurrentWordCategoryPosition(), getAdapterPosition());
                functionContentCallBack.updateCategoryMessage();
                // 更新第0个元素的内容,把蓝色线条画上
                notifyItemChanged(0);
                notifyItemRemoved(getAdapterPosition());
            }
        }
    }

    /**
     * 功能函数上下文回调接口
     */
    public interface FunctionContentCallBack {
        void startDrag(RecyclerView.ViewHolder viewHolder);

        /**
         * 得到当前单词隶属于哪个收藏夹
         *
         * @return 返回收藏夹的position
         */
        int getCurrentWordCategoryPosition();

        /**
         * 当发生单词移动时更新,如果收藏夹采用默认的命名规则;则该方法会更新收藏夹的名称
         */
        void updateCategoryMessage();

    }

}
