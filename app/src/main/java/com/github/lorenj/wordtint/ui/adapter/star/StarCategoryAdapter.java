package com.github.lorenj.wordtint.ui.adapter.star;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.entity.WordStarWordIdEntity;
import com.github.lorenj.wordtint.database.entity.relation.WordStarWithWordIdEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.entity.dto.WordCategoryDTO;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.handler.StarFunctionHandler;
import com.github.lorenj.wordtint.ui.adapter.SimpleItemTouchHelperCallback;
import com.github.lorenj.wordtint.ui.adapter.listener.MoveAndSwipedListener;
import com.github.lorenj.wordtint.ui.adapter.listener.StateChangedListener;

import java.util.List;
import java.util.function.Consumer;


/**
 * 每个分类的Adapter
 *
 * @author cnsukidayo
 * @date 2023/1/7 17:35
 */
public class StarCategoryAdapter extends RecyclerView.Adapter<StarCategoryAdapter.SingleSingleViewHolder>
        implements MoveAndSwipedListener, RecyclerViewAdapterItemChange<WordCategoryDTO> {

    private final Context context;
    private Consumer<RecyclerView.ViewHolder> startDragListener;
    private final StarFunctionHandler startFunctionHandler;
    private final android.os.Handler updateUIHandler = new Handler(Looper.getMainLooper());
    /**
     * 二级列表贡献缓存
     */
    private final RecyclerView.RecycledViewPool starSectionPool;

    public StarCategoryAdapter(Context context, StarFunctionHandler starFunctionHandler) {
        this.context = context;
        this.startFunctionHandler = starFunctionHandler;
        this.starSectionPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public SingleSingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SingleSingleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_star_parent, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SingleSingleViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WordStarWithWordIdEntity wordStarWithWordIdEntity = startFunctionHandler.getAllStarList().get(position);
        // 重置改变,防止由于复用而导致的显示问题
        holder.scroller.scrollTo(0, 0);
        holder.starSection.setVisibility(View.GONE);
        holder.listState.setRotation(90);
        holder.listState.getDrawable().setTint(context.getResources().getColor(R.color.dark_gray, null));
        holder.fold = true;
        holder.title.setText(startFunctionHandler.calculationTitle(wordStarWithWordIdEntity.wordStarEntity));
        holder.describe.setText(startFunctionHandler.calculationDescribe(wordStarWithWordIdEntity.wordStarEntity));
        // 刷新收藏夹id
        int currentStarId = startFunctionHandler.getAllStarList().get(position).wordStarEntity.id;
        holder.starCategorySectionAdapter.setStarId(currentStarId);
        holder.starCategorySectionAdapter.notifyItemRangeChanged(0, wordStarWithWordIdEntity.wordStarWordIdEntityList.size());
    }

    @Override
    public void onBindViewHolder(@NonNull SingleSingleViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) super.onBindViewHolder(holder, position, payloads);
        for (Object payload : payloads) {
            if (payload == Item.ITEM_CLICK) {
                int currentStarId = startFunctionHandler.getAllStarList().get(position).wordStarEntity.id;
                holder.starCategorySectionAdapter.setStarId(currentStarId);
                if (holder.fold) {
                    holder.starSection.setVisibility(View.GONE);
                    holder.listState.setRotation(90);
                    holder.listState.getDrawable().setTint(context.getResources().getColor(R.color.dark_gray, null));
                } else {
                    holder.starSection.setVisibility(View.VISIBLE);
                    holder.listState.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_blue_dark, null));
                    holder.listState.setRotation(180);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return startFunctionHandler.getAllStarList().size();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        WordStarWithWordIdEntity fromStar = startFunctionHandler.getAllStarList().get(fromPosition);
        WordStarWithWordIdEntity toStar = startFunctionHandler.getAllStarList().get(toPosition);
        notifyItemMoved(fromPosition, toPosition);
        startFunctionHandler.moveStar(fromStar, toStar);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    /**
     * 设置拖拽的回调方法
     */
    public void setStartDragListener(Consumer<RecyclerView.ViewHolder> startDragListener) {
        this.startDragListener = startDragListener;
    }

    public class SingleSingleViewHolder extends RecyclerView.ViewHolder
            implements StateChangedListener, View.OnTouchListener, View.OnClickListener, StarCategorySectionAdapter.FunctionContentCallBack {
        public View itemView, scroller;
        public TextView title, describe, edit, delete, addWord;
        public LinearLayout openList;
        public ImageView listState;
        public ImageButton move;
        /**
         * 是否折叠,这个变量状态不一致不太要紧
         */
        private boolean fold = true;
        /**
         * 收藏夹下的所有单词
         */
        public RecyclerView starSection;
        public ItemTouchHelper touchHelper;
        public StarCategorySectionAdapter starCategorySectionAdapter;

        public SingleSingleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.title = itemView.findViewById(R.id.tv_item_star_title);
            this.describe = itemView.findViewById(R.id.tv_item_star_describe);
            this.scroller = itemView.findViewById(R.id.sll_item_star_parent);
            this.openList = itemView.findViewById(R.id.ll_item_star);
            this.listState = itemView.findViewById(R.id.iv_item_star_fold);
            this.edit = itemView.findViewById(R.id.ll_item_star_swipe_edit);
            this.delete = itemView.findViewById(R.id.ll_item_star_swipe_delete);
            this.move = itemView.findViewById(R.id.ll_item_star_swipe_move);
            this.addWord = itemView.findViewById(R.id.ll_item_star_swipe_add);
            this.starSection = itemView.findViewById(R.id.ll_item_star_section);

            this.move.setOnTouchListener(this);
            this.delete.setOnClickListener(this);
            this.edit.setOnClickListener(this);
            this.openList.setOnClickListener(this);
            this.addWord.setOnClickListener(this);

            starCategorySectionAdapter = new StarCategorySectionAdapter(context, startFunctionHandler);
            starSection.setLayoutManager(new LinearLayoutManager(context));
            starSection.setRecycledViewPool(starSectionPool);
            starSection.setAdapter(starCategorySectionAdapter);

            touchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(starCategorySectionAdapter));
            touchHelper.attachToRecyclerView(starSection);

        }

        @Override
        public void onItemSelected() {
            itemView.setAlpha(0.5f);
        }

        @Override
        public void onItemClear() {
            itemView.setAlpha(1.0f);
            StaticFactory.getExecutorService().execute(startFunctionHandler::batchUpdateCurrentStar);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int clickViewId = v.getId();
            if (clickViewId == R.id.ll_item_star_swipe_move
                    && event.getAction() == MotionEvent.ACTION_DOWN
                    && startDragListener != null) {
                startDragListener.accept(this);
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            int clickViewId = v.getId();
            WordStarWithWordIdEntity wordStarWithWordIdEntity = startFunctionHandler.getAllStarList().get(getAdapterPosition());
            if (clickViewId == R.id.ll_item_star_swipe_delete) {
                // 删除当前Item
                StaticFactory.getExecutorService().execute(() -> {
                    startFunctionHandler.removeStar(wordStarWithWordIdEntity);
                    updateUIHandler.post(() -> notifyItemRemoved(getAdapterPosition()));
                });
            } else if (clickViewId == R.id.ll_item_star_swipe_edit) {
                // 编辑收藏夹信息
                View editStart = LayoutInflater.from(context).inflate(R.layout.dialog_recite_new_star, null);
                EditText starTitle = editStart.findViewById(R.id.et_new_star_title);
                EditText starDescribe = editStart.findViewById(R.id.et_new_star_describe);
                new AlertDialog.Builder(context)
                        .setView(editStart)
                        .setCancelable(true)
                        .setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                            wordStarWithWordIdEntity.wordStarEntity.title = starTitle.getText().toString();
                            wordStarWithWordIdEntity.wordStarEntity.describeInfo = starDescribe.getText().toString();
                            StaticFactory.getExecutorService().execute(() -> {
                                startFunctionHandler.updateStar(wordStarWithWordIdEntity);
                                updateUIHandler.post(() -> notifyItemChanged(getAdapterPosition()));
                            });
                        })
                        .setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> {
                        })
                        .show();
            } else if (clickViewId == R.id.ll_item_star) {
                fold = !fold;
                notifyItemChanged(getAdapterPosition(), Item.ITEM_CLICK);
            } else if (clickViewId == R.id.ll_item_star_swipe_add) {
                FunctionWordVO currentFocusWord = startFunctionHandler.getCurrentFocusWord();
                if (currentFocusWord == null) {
                    // 不能添加单词到分类的提示
                    Toast errorAddTint = Toast.makeText(context, context.getResources().getString(R.string.error_add_hint), Toast.LENGTH_SHORT);
                    errorAddTint.setGravity(Gravity.CENTER, 0, 500);
                    errorAddTint.show();
                    return;
                }
                WordStarWordIdEntity wordStarWordIdEntity = new WordStarWordIdEntity();
                wordStarWordIdEntity.starId = wordStarWithWordIdEntity.wordStarEntity.id;
                wordStarWordIdEntity.wordId = currentFocusWord.getWordId();
                StaticFactory.getExecutorService().execute(() -> {
                    if (!startFunctionHandler.addWordToStar(wordStarWordIdEntity)) return;
                    updateUIHandler.post(() -> {
                        title.setText(startFunctionHandler.calculationTitle(wordStarWithWordIdEntity.wordStarEntity));
                        describe.setText(startFunctionHandler.calculationDescribe(wordStarWithWordIdEntity.wordStarEntity));
                        starCategorySectionAdapter.notifyItemInserted(wordStarWithWordIdEntity.wordStarWordIdEntityList.size() - 1);
                    });
                });
            }
        }

        @Override
        public void startDrag(RecyclerView.ViewHolder viewHolder) {
            // viewHolder->ItemTouchHelper->viewHolder
            this.touchHelper.startDrag(viewHolder);
        }

        @Override
        public int getCurrentWordCategoryPosition() {
            return getAdapterPosition();
        }

        @Override
        public void updateCategoryMessage() {
            //title.setText(startFunctionHandler.calculationTitle(getAdapterPosition()));
            //describe.setText(startFunctionHandler.calculationDescribe(getAdapterPosition()));
        }
    }

    private enum Item {
        ITEM_CLICK
    }

}
