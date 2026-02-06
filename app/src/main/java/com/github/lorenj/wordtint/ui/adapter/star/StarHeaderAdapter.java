package com.github.lorenj.wordtint.ui.adapter.star;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.entity.WordStarEntity;
import com.github.lorenj.wordtint.database.entity.WordStarWordIdEntity;
import com.github.lorenj.wordtint.database.entity.relation.WordStarWithWordIdEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.handler.StarFunctionHandler;

import java.util.List;


/**
 * 每个分类的Adapter
 *
 * @author cnsukidayo
 * @date 2023/1/7 17:35
 */
public class StarHeaderAdapter extends RecyclerView.Adapter<StarHeaderAdapter.StarHeaderViewHolder>
        implements RecyclerViewAdapterItemChange<WordStarEntity>, StarSectionAdapter.FunctionContentCallBack {

    private final Context context;
    private final android.os.Handler updateUIHandler = new Handler(Looper.getMainLooper());
    private final WordStarWithWordIdEntity wordStarWithWordIdEntity;
    private final StarFunctionHandler starFunctionHandler;
    /**
     * 当前收藏夹是否被折叠
     */
    private boolean fold = true;
    private final ConcatAdapter starListAdapter;
    private final StarSectionAdapter starSectionAdapter;
    private final ItemTouchHelper touchHelper;

    public StarHeaderAdapter(Context context,
                             WordStarWithWordIdEntity wordStarWithWordIdEntity,
                             StarFunctionHandler starFunctionHandler,
                             ConcatAdapter starListAdapter,
                             RecyclerView.RecycledViewPool resultPool,
                             ItemTouchHelper touchHelper) {
        this.context = context;
        this.wordStarWithWordIdEntity = wordStarWithWordIdEntity;
        this.starFunctionHandler = starFunctionHandler;
        this.starListAdapter = starListAdapter;
        this.starSectionAdapter = new StarSectionAdapter(context, starFunctionHandler, resultPool);
        this.touchHelper = touchHelper;
        this.starSectionAdapter.setFunctionListener(this);
    }

    @NonNull
    @Override
    public StarHeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StarHeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_star_parent, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StarHeaderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 重置改变,防止由于复用而导致的显示问题
        holder.scroller.scrollTo(0, 0);
        holder.openList.getBackground().setTint(context.getColor(R.color.dark_gray));
        if (fold) {
            holder.listState.setRotation(90);
            holder.listState.getDrawable().setTint(context.getResources().getColor(R.color.dark_gray, null));
            holder.openList.setBackground(ResourcesCompat.getDrawable(
                    context.getResources(),
                    R.drawable.background_rounded_ripple_hollow,
                    null));
        }
        if (!fold) {
            holder.listState.setRotation(180);
            holder.listState.getDrawable().setTint(context.getResources().getColor(R.color.light_blue_ff, null));
            holder.openList.setBackground(ResourcesCompat.getDrawable(
                    context.getResources(),
                    R.drawable.background_rounded_ripple_hollow_top,
                    null));
        }
        holder.title.setText(starFunctionHandler.calculationTitle(wordStarWithWordIdEntity.wordStarEntity));
        holder.describe.setText(starFunctionHandler.calculationDescribe(wordStarWithWordIdEntity.wordStarEntity));
    }

    @Override
    public void onBindViewHolder(@NonNull StarHeaderViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
            return;
        }
        for (Object payload : payloads) {
            if (payload == Item.STAR_EDIT) {
                holder.title.setText(starFunctionHandler.calculationTitle(wordStarWithWordIdEntity.wordStarEntity));
                holder.describe.setText(starFunctionHandler.calculationDescribe(wordStarWithWordIdEntity.wordStarEntity));
            }
            holder.openList.getBackground().setTint(context.getColor(R.color.dark_gray));
            if (payload == Item.STAR_FOLD && fold) {
                holder.listState.setRotation(90);
                holder.listState.getDrawable().setTint(context.getResources().getColor(R.color.dark_gray, null));
                holder.openList.setBackground(ResourcesCompat.getDrawable(
                        context.getResources(),
                        R.drawable.background_rounded_ripple_hollow,
                        null));
            }
            if (payload == Item.STAR_FOLD && !fold) {
                holder.listState.setRotation(180);
                holder.listState.getDrawable().setTint(context.getResources().getColor(R.color.light_blue_ff, null));
                holder.openList.setBackground(ResourcesCompat.getDrawable(
                        context.getResources(),
                        R.drawable.background_rounded_ripple_hollow_top,
                        null));

            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return StarListAdapter.STAR;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void startDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

    @Override
    public void updateCategoryMessage() {
        notifyItemChanged(0);
    }

    public static class StarHeaderViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public View itemView, scroller;
        public TextView title, describe, edit, delete, addWord;
        public LinearLayout openList;
        public ImageView listState;

        public StarHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.title = itemView.findViewById(R.id.tv_item_star_title);
            this.describe = itemView.findViewById(R.id.tv_item_star_describe);
            this.scroller = itemView.findViewById(R.id.sll_item_star_scroller);
            this.openList = itemView.findViewById(R.id.ll_item_star);
            this.listState = itemView.findViewById(R.id.iv_item_star_fold);
            this.edit = itemView.findViewById(R.id.ll_item_star_swipe_edit);
            this.delete = itemView.findViewById(R.id.ll_item_star_swipe_delete);
            this.addWord = itemView.findViewById(R.id.ll_item_star_swipe_add);

            this.delete.setOnClickListener(this);
            this.edit.setOnClickListener(this);
            this.openList.setOnClickListener(this);
            this.addWord.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickViewId = v.getId();
            // 编辑收藏夹信息
            StarHeaderAdapter starHeaderAdapter = (StarHeaderAdapter) getBindingAdapter();
            if (starHeaderAdapter == null) return;
            if (clickViewId == R.id.ll_item_star_swipe_edit) {
                View editStart = LayoutInflater.from(starHeaderAdapter.context).inflate(R.layout.dialog_recite_new_star, null);
                EditText starTitle = editStart.findViewById(R.id.et_new_star_title);
                EditText starDescribe = editStart.findViewById(R.id.et_new_star_describe);
                new AlertDialog.Builder(starHeaderAdapter.context)
                        .setView(editStart)
                        .setCancelable(true)
                        .setPositiveButton(starHeaderAdapter.context.getString(R.string.confirm), (dialog, which) -> {
                            starHeaderAdapter.wordStarWithWordIdEntity.wordStarEntity.title = starTitle.getText().toString();
                            starHeaderAdapter.wordStarWithWordIdEntity.wordStarEntity.describeInfo = starDescribe.getText().toString();
                            StaticFactory.getExecutorService().execute(() -> {
                                starHeaderAdapter.starFunctionHandler.updateStar(starHeaderAdapter.wordStarWithWordIdEntity);
                                starHeaderAdapter.updateUIHandler.post(() -> starHeaderAdapter.notifyItemChanged(getBindingAdapterPosition(), Item.STAR_EDIT));
                            });
                        })
                        .setNegativeButton(starHeaderAdapter.context.getString(R.string.cancel), (dialog, which) -> {
                        })
                        .show();
            }
            // 添加单词
            if (clickViewId == R.id.ll_item_star_swipe_add) {
                FunctionWordVO currentFocusWord = starHeaderAdapter.starFunctionHandler.getCurrentFocusWord();
                if (currentFocusWord == null) {
                    // 不能添加单词到分类的提示
                    Toast errorAddTint = Toast.makeText(starHeaderAdapter.context, starHeaderAdapter.context.getResources().getString(R.string.error_add_hint), Toast.LENGTH_SHORT);
                    errorAddTint.setGravity(Gravity.CENTER, 0, 500);
                    errorAddTint.show();
                    return;
                }
                WordStarWordIdEntity wordStarWordIdEntity = new WordStarWordIdEntity();
                wordStarWordIdEntity.starId = starHeaderAdapter.wordStarWithWordIdEntity.wordStarEntity.id;
                wordStarWordIdEntity.wordId = currentFocusWord.getWordId();
                StaticFactory.getExecutorService().execute(() -> {
                    if (!starHeaderAdapter.starFunctionHandler.addWordToStar(wordStarWordIdEntity))
                        return;
                    starHeaderAdapter.updateUIHandler.post(() -> {
                        starHeaderAdapter.notifyItemChanged(getBindingAdapterPosition(), Item.STAR_EDIT);
                        starHeaderAdapter.starSectionAdapter.addItem(wordStarWordIdEntity);
                    });
                });
            }
            // 删除当前Item
            if (clickViewId == R.id.ll_item_star_swipe_delete) {
                StaticFactory.getExecutorService().execute(() -> {
                    starHeaderAdapter.starFunctionHandler.removeStar(starHeaderAdapter.wordStarWithWordIdEntity);
                    starHeaderAdapter.updateUIHandler.post(() -> {
                        starHeaderAdapter.starListAdapter.removeAdapter(starHeaderAdapter.starSectionAdapter);
                        starHeaderAdapter.starListAdapter.removeAdapter(starHeaderAdapter);
                    });
                });
            }
            // 展开收藏夹
            if (clickViewId == R.id.ll_item_star) {
                starHeaderAdapter.fold = !starHeaderAdapter.fold;
                starHeaderAdapter.notifyItemChanged(getBindingAdapterPosition(), Item.STAR_FOLD);
                // 如果当前是折叠就添加,否则就删除
                if (starHeaderAdapter.fold) {
                    starHeaderAdapter.starListAdapter.removeAdapter(starHeaderAdapter.starSectionAdapter);
                } else {
                    int headerIndex = starHeaderAdapter.starListAdapter.getAdapters().indexOf(starHeaderAdapter);
                    starHeaderAdapter.starSectionAdapter.replaceAll(starHeaderAdapter.wordStarWithWordIdEntity.wordStarWordIdEntityList);
                    starHeaderAdapter.starListAdapter.addAdapter(headerIndex + 1, starHeaderAdapter.starSectionAdapter);
                }
            }
        }
    }

    private enum Item {
        STAR_EDIT,
        STAR_FOLD
    }

}
