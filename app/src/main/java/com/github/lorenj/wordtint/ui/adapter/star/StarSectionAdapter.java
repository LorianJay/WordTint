package com.github.lorenj.wordtint.ui.adapter.star;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.entity.WordStarWordIdEntity;
import com.github.lorenj.wordtint.database.entity.relation.WordStarWithWordIdEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.handler.StarFunctionHandler;
import com.github.lorenj.wordtint.ui.adapter.StarResultAdapter;
import com.github.lorenj.wordtint.ui.adapter.listener.MoveAndSwipedListener;
import com.github.lorenj.wordtint.ui.adapter.listener.StateChangedListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * 每个分类中的每个单词Adapter
 *
 * @author cnsukidayo
 * @date 2023/1/9 14:35
 */
public class StarSectionAdapter extends RecyclerView.Adapter<StarSectionAdapter.StarSectionViewHolder>
        implements MoveAndSwipedListener, RecyclerViewAdapterItemChange<WordStarWordIdEntity> {

    private final Context context;
    private FunctionContentCallBack functionContentCallBack;
    /**
     * 不同收藏夹的单词也要共享缓存<br>
     * 所以必须在顶层的收藏夹创建
     */
    private final RecyclerView.RecycledViewPool resultPool;
    /**
     * 当前是否正在移动单词的标识
     */
    private boolean itemMoving = false;
    private final Handler updateUIHandler = new Handler(Looper.getMainLooper());
    private final List<WordStarWordIdEntity> wordStarWordIdEntityList = new ArrayList<>();
    private final StarFunctionHandler starFunctionHandler;

    public StarSectionAdapter(Context context,
                              StarFunctionHandler starFunctionHandler,
                              RecyclerView.RecycledViewPool resultPool) {
        this.context = context;
        this.starFunctionHandler = starFunctionHandler;
        this.resultPool = resultPool;
    }

    @NonNull
    @Override
    public StarSectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StarSectionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_star_section, parent, false), this);
    }

    @Override
    public void onBindViewHolder(@NonNull StarSectionViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 将第一根线设置为别的颜色
        if (position == 0) {
            holder.separator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light, null));
        } else {
            holder.separator.setBackgroundColor(context.getResources().getColor(R.color.dark_gray, null));
        }
        // 最后一个单词设置为底部圆角
        if (position == wordStarWordIdEntityList.size() - 1) {
            holder.parent.setBackground(ResourcesCompat.getDrawable(
                    context.getResources(),
                    R.drawable.background_rounded_ripple_hollow_bottom,
                    null));
        } else {
            holder.parent.setBackground(ResourcesCompat.getDrawable(
                    context.getResources(),
                    R.drawable.background_rounded_ripple_hollow_middle,
                    null));
        }
        holder.scroller.scrollTo(0, 0);
        WordStarWordIdEntity wordStarWordIdEntity = wordStarWordIdEntityList.get(position);
        FunctionWordVO currentSectionWord = starFunctionHandler.getWordDetailByWordId(wordStarWordIdEntity);
        holder.starResultAdapter.addItem(currentSectionWord);
        holder.wordOrigin.setText(currentSectionWord.getValue().get(WordStructure.WORD_ORIGIN));
    }

    @Override
    public void onBindViewHolder(@NonNull StarSectionViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
            return;
        }

        for (Object payload : payloads) {
            if (payload == Item.ITEM_MOVE) {
                Log.d("itemchange", "itemchange");
                if (position == 0) {
                    holder.separator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light, null));
                } else {
                    holder.separator.setBackgroundColor(context.getResources().getColor(R.color.dark_gray, null));
                }
                if (position == wordStarWordIdEntityList.size() - 1) {
                    holder.parent.setBackground(ResourcesCompat.getDrawable(
                            context.getResources(),
                            R.drawable.background_rounded_ripple_hollow_bottom,
                            null));
                } else {
                    holder.parent.setBackground(ResourcesCompat.getDrawable(
                            context.getResources(),
                            R.drawable.background_rounded_ripple_hollow_middle,
                            null));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return wordStarWordIdEntityList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return StarListAdapter.BOOK_SECTION;
    }

    public void onItemMove(int fromPosition, int toPosition) {
        WordStarWordIdEntity fromWord = wordStarWordIdEntityList.get(fromPosition);
        WordStarWordIdEntity toWord = wordStarWordIdEntityList.get(toPosition);
        notifyItemMoved(fromPosition, toPosition);
        Collections.swap(wordStarWordIdEntityList, fromPosition, toPosition);
        starFunctionHandler.moveStarInnerWord(fromWord, toWord);
        if (fromPosition == 0 || toPosition == 0) {
            notifyItemChanged(fromPosition, Item.ITEM_MOVE);
            notifyItemChanged(toPosition, Item.ITEM_MOVE);
        }
        if (fromPosition == wordStarWordIdEntityList.size() - 1
                || toPosition == wordStarWordIdEntityList.size() - 1) {
            notifyItemChanged(fromPosition, Item.ITEM_MOVE);
            notifyItemChanged(toPosition, Item.ITEM_MOVE);
        }
    }

    @Override
    public void onItemDismiss(int position) {
    }

    @Override
    public void replaceAll(Collection<WordStarWordIdEntity> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new StarSectionDiffCallback(this.wordStarWordIdEntityList, new ArrayList<>(newData)));
        this.wordStarWordIdEntityList.clear();
        this.wordStarWordIdEntityList.addAll(newData);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void addItem(WordStarWordIdEntity item) {
        List<WordStarWordIdEntity> newList = new ArrayList<>(this.wordStarWordIdEntityList);
        newList.add(item);
        replaceAll(newList);
    }

    public void setFunctionListener(FunctionContentCallBack functionContentCallBack) {
        this.functionContentCallBack = functionContentCallBack;
    }

    public static class StarSectionViewHolder extends RecyclerView.ViewHolder implements
            StateChangedListener, View.OnClickListener, View.OnLongClickListener {
        private LinearLayout parent;
        /**
         * separator颜色线条
         */
        private final View itemView, scroller, separator;
        private final TextView delete, wordOrigin;
        private final RecyclerView starResult;
        private StarResultAdapter starResultAdapter;
        private LinearLayout sectionMove;

        public StarSectionViewHolder(@NonNull View itemView, StarSectionAdapter starSectionAdapter) {
            super(itemView);
            this.itemView = itemView;
            this.parent = itemView.findViewById(R.id.ll_star_section_parent);
            this.delete = itemView.findViewById(R.id.tv_star_section_delete);
            this.scroller = itemView.findViewById(R.id.sll_star_section);
            this.wordOrigin = itemView.findViewById(R.id.tv_star_section_origin);
            this.starResult = itemView.findViewById(R.id.rv_star_section_result);
            this.separator = itemView.findViewById(R.id.v_star_section_separate);
            this.sectionMove = itemView.findViewById(R.id.ll_star_section_move);
            this.delete.setOnClickListener(this);

            this.sectionMove.setOnLongClickListener(this);
            starResult.setLayoutManager(new LinearLayoutManager(starSectionAdapter.context));
            starResult.setRecycledViewPool(starSectionAdapter.resultPool);
            starResultAdapter = new StarResultAdapter(starSectionAdapter.context);
            starResult.setAdapter(starResultAdapter);
        }

        @Override
        public void onItemSelected() {
            itemView.setAlpha(0.5f);
        }

        @Override
        public void onItemClear() {
            itemView.setAlpha(1.0f);
            // 批量更新本次移动的情况
            StaticFactory.getExecutorService().execute(() -> {
                StarSectionAdapter starSectionAdapter = (StarSectionAdapter) getBindingAdapter();
                int position = getBindingAdapterPosition();
                if (starSectionAdapter == null) return;
                WordStarWithWordIdEntity wordStarWithWordId = starSectionAdapter.starFunctionHandler.getStarById(starSectionAdapter.wordStarWordIdEntityList.get(0).starId);
                starSectionAdapter.starFunctionHandler.batchUpdateStarInnerWordList(wordStarWithWordId.wordStarEntity);
                starSectionAdapter.updateUIHandler.post(() -> starSectionAdapter.functionContentCallBack.updateCategoryMessage());
            });
        }

        @Override
        public void onClick(View v) {
            int clickViewId = v.getId();
            StarSectionAdapter starSectionAdapter = (StarSectionAdapter) getBindingAdapter();
            if (starSectionAdapter == null) return;
            // 强制使用当前绑定的adapter的id
            if (clickViewId == R.id.tv_star_section_delete) {
                int position = getBindingAdapterPosition();
                WordStarWordIdEntity innerStarWordIdEntity = starSectionAdapter.wordStarWordIdEntityList.get(position);
                StaticFactory.getExecutorService().execute(() -> {
                    starSectionAdapter.starFunctionHandler.removeWordFromStar(innerStarWordIdEntity);
                    starSectionAdapter.updateUIHandler.post(() -> {
                        starSectionAdapter.functionContentCallBack.updateCategoryMessage();
                        starSectionAdapter.wordStarWordIdEntityList.remove(position);
                        starSectionAdapter.notifyItemRemoved(position);
                        //starSectionAdapter.notifyItemRangeChanged(position, starSectionAdapter.getItemCount() - position);
                        if (position == 0) starSectionAdapter.notifyItemChanged(0, Item.ITEM_MOVE);
                    });
                });
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int clickViewId = v.getId();
            StarSectionAdapter starSectionAdapter = (StarSectionAdapter) getBindingAdapter();
            if (starSectionAdapter == null) return false;
            if (clickViewId == R.id.ll_star_section_move &&
                    starSectionAdapter.functionContentCallBack != null) {
                starSectionAdapter.functionContentCallBack.startDrag(this);
            }
            return false;
        }
    }

    /**
     * 功能函数上下文回调接口
     */
    public interface FunctionContentCallBack {
        void startDrag(RecyclerView.ViewHolder viewHolder);

        /**
         * 当发生单词移动时更新,如果收藏夹采用默认的命名规则;则该方法会更新收藏夹的名称
         */
        void updateCategoryMessage();
    }

    private enum Item {
        ITEM_MOVE
    }
}
