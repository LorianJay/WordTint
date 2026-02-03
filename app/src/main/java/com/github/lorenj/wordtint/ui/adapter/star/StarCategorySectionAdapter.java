package com.github.lorenj.wordtint.ui.adapter.star;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.entity.WordStarWordIdEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.entity.dto.WordCategoryWordDTO;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.handler.StarFunctionHandler;
import com.github.lorenj.wordtint.handler.StarSectionFunctionHandler;
import com.github.lorenj.wordtint.ui.adapter.StarResultAdapter;
import com.github.lorenj.wordtint.ui.adapter.listener.MoveAndSwipedListener;
import com.github.lorenj.wordtint.ui.adapter.listener.StateChangedListener;


/**
 * 每个分类中的每个单词Adapter
 *
 * @author cnsukidayo
 * @date 2023/1/9 14:35
 */
public class StarCategorySectionAdapter extends RecyclerView.Adapter<StarCategorySectionAdapter.StarSectionViewHolder>
        implements MoveAndSwipedListener, RecyclerViewAdapterItemChange<WordCategoryWordDTO> {

    private final Context context;
    private FunctionContentCallBack functionContentCallBack;
    private final StarSectionFunctionHandler starSectionFunctionHandler;
    private int starId;
    private final RecyclerView.RecycledViewPool resultPool;
    /**
     * 当前是否正在移动单词的标识
     */
    private volatile boolean itemMoving = false;
    private final android.os.Handler updateUIHandler = new Handler(Looper.getMainLooper());

    /**
     * @param context              上下文
     * @param startFunctionHandler 收藏夹处理器
     * @param starId               当前收藏夹片段对应的收藏夹id
     */
    public StarCategorySectionAdapter(Context context,
                                      StarFunctionHandler startFunctionHandler) {
        this.context = context;
        this.starSectionFunctionHandler = startFunctionHandler;
        this.resultPool = new RecyclerView.RecycledViewPool();
    }

    /**
     * 必须在父级手动绑定id,否则会出问题
     *
     * @param starId 收藏夹id
     */
    public void setStarId(int starId) {
        this.starId = starId;
    }

    @NonNull
    @Override
    public StarSectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StarSectionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_star_section, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StarSectionViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 将第一根线设置为别的颜色
        if (position == 0) {
            holder.separator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light, null));
        } else {
            holder.separator.setBackgroundColor(context.getResources().getColor(R.color.dark_gray, null));
        }
        // 如果物体正在移动,则只更新线条颜色;
        if (itemMoving) return;
        // 重置改变，防止由于复用而导致的显示问题
        holder.scroller.scrollTo(0, 0);
        // 得到当前的单词
        WordStarWordIdEntity wordStarWordIdEntity = starSectionFunctionHandler.getStarById(starId)
                .wordStarWordIdEntityList.
                get(position);
        FunctionWordVO currentSectionWord = starSectionFunctionHandler.getWordDetailByWordId(wordStarWordIdEntity);
        holder.starResult.setLayoutManager(new LinearLayoutManager(context));
        holder.starResult.setRecycledViewPool(resultPool);
        holder.starResultAdapter = new StarResultAdapter(context, currentSectionWord);
        holder.starResult.setAdapter(holder.starResultAdapter);
        holder.wordOrigin.setText(currentSectionWord.getValue().get(WordStructure.WORD_ORIGIN));
        holder.starId = starId;
        holder.starCategorySectionAdapter = this;
    }

    @Override
    public int getItemCount() {
        return starSectionFunctionHandler.getStarById(starId).wordStarWordIdEntityList.size();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        /*
        //categoryWordFunctionHandler.moveStarInnerWord(functionContentCallBack.getCurrentWordCategoryPosition(), fromPosition, toPosition);
        functionContentCallBack.updateCategoryMessage();
        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(fromPosition, Boolean.FALSE);
        notifyItemChanged(toPosition, Boolean.FALSE);
         */
    }

    @Override
    public void onItemDismiss(int position) {
    }

    public void setFunctionListener(FunctionContentCallBack functionContentCallBack) {
        this.functionContentCallBack = functionContentCallBack;
    }

    public class StarSectionViewHolder extends RecyclerView.ViewHolder implements
            StateChangedListener, View.OnTouchListener, View.OnClickListener {
        /**
         * separator颜色线条
         */
        private final View itemView, scroller, separator;
        private final TextView delete, wordOrigin;
        private final ImageButton move;
        private final RecyclerView starResult;
        private StarResultAdapter starResultAdapter;
        private StarCategorySectionAdapter starCategorySectionAdapter;
        private int starId;

        public StarSectionViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.delete = itemView.findViewById(R.id.tv_star_section_delete);
            this.scroller = itemView.findViewById(R.id.sll_star_section);
            this.move = itemView.findViewById(R.id.iv_star_section_move);
            this.wordOrigin = itemView.findViewById(R.id.tv_star_section_origin);
            this.starResult = itemView.findViewById(R.id.rv_star_section_result);
            this.separator = itemView.findViewById(R.id.v_star_section_separate);
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
            if (clickViewId == R.id.iv_star_section_move &&
                    event.getAction() == MotionEvent.ACTION_DOWN &&
                    functionContentCallBack != null) {
                functionContentCallBack.startDrag(this);
            }
            return false;
        }


        @Override
        public void onClick(View v) {
            int clickViewId = v.getId();
            if (clickViewId == R.id.tv_star_section_delete) {
                // 强制使用当前绑定的adapter的id
                int currentActiveStarId = starId;
                int position = getAdapterPosition();
                WordStarWordIdEntity innerStarWordIdEntity = starSectionFunctionHandler
                        .getStarById(currentActiveStarId)
                        .wordStarWordIdEntityList
                        .get(position);
                StaticFactory.getExecutorService().execute(() -> {
                    starSectionFunctionHandler.removeWordFromStar(innerStarWordIdEntity);
                    updateUIHandler.post(() -> {
                        starCategorySectionAdapter.notifyItemRemoved(position);
                        starCategorySectionAdapter.notifyItemRangeChanged(position, getItemCount() - position);
                        if (position == 0) starCategorySectionAdapter.notifyItemChanged(0);
                    });
                });
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

    private enum Item {
        ITEM_DELETE
    }
}
