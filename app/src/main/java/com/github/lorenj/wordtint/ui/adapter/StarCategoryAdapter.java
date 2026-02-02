package com.github.lorenj.wordtint.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.entity.relation.WordStarWithWordIdEntity;
import com.github.lorenj.wordtint.entity.dto.WordCategoryDTO;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.handler.StarFunctionHandler;
import com.github.lorenj.wordtint.ui.adapter.listener.MoveAndSwipedListener;
import com.github.lorenj.wordtint.ui.adapter.listener.StateChangedListener;

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

    public StarCategoryAdapter(Context context, StarFunctionHandler starFunctionHandler) {
        this.context = context;
        this.startFunctionHandler = starFunctionHandler;
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
        holder.title.setText(startFunctionHandler.calculationTitle(wordStarWithWordIdEntity.wordStarEntity));
        holder.describe.setText(startFunctionHandler.calculationDescribe(wordStarWithWordIdEntity.wordStarEntity));
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
            implements StateChangedListener, View.OnTouchListener, View.OnClickListener, StartSingleCategoryWordAdapter.FunctionContentCallBack {
        public View itemView, scroller;
        public TextView title, describe, edit, delete, addWord;
        public LinearLayout openList;
        public ImageView listState;
        public ImageButton move;
        /*
         如果某个分类展开与否的状态需要由StartFunctionHandler来控制,则托管由StartFunctionHandler来实现该功能.
         目前而言,暂时不需要外部接入,内部就能够完成,就内部来实现这个功能
         */
        private boolean isOpen;
        // 显示当前收藏夹下的所有单词的RecyclerView
        public RecyclerView underNowStartAllWord;
        public StartSingleCategoryWordAdapter startSingleCategoryWordAdapter;
        public ItemTouchHelper touchHelper;

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
            this.underNowStartAllWord = itemView.findViewById(R.id.ll_item_star_section);
            // 加载当前分类下的所有单词
            //this.underNowStartAllWord.setLayoutManager(new LinearLayoutManager(context));
            //this.startSingleCategoryWordAdapter = new StartSingleCategoryWordAdapter(context);
            //this.underNowStartAllWord.setAdapter(startSingleCategoryWordAdapter);
            //this.touchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(startSingleCategoryWordAdapter));
            //this.touchHelper.attachToRecyclerView(underNowStartAllWord);
            //startSingleCategoryWordAdapter.setCategoryWordFunctionHandler(startFunctionHandler);
            //startSingleCategoryWordAdapter.setFunctionListener(this);

            this.move.setOnTouchListener(this);
            this.delete.setOnClickListener(this);
            this.edit.setOnClickListener(this);
            this.openList.setOnClickListener(this);
            this.addWord.setOnClickListener(this);
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
                if (isOpen) {
                    underNowStartAllWord.setVisibility(View.GONE);
                    listState.setRotation(90);
                    listState.getDrawable().setTint(context.getResources().getColor(R.color.dark_gray, null));
                } else {
                    underNowStartAllWord.setVisibility(View.VISIBLE);
                    listState.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_blue_dark, null));
                    listState.setRotation(180);
                }
                isOpen = !isOpen;
            } else if (clickViewId == R.id.ll_item_star_swipe_add) {
                /*
                Optional.ofNullable(startFunctionHandler.getCurrentViewWord())
                        .ifPresentOrElse(wordCategoryWordDTO -> {
                    startSingleCategoryWordAdapter.addItem(wordCategoryWordDTO);
                    title.setText(startFunctionHandler.calculationTitle(getAdapterPosition()));
                    describe.setText(startFunctionHandler.calculationDescribe(getAdapterPosition()));
                }, () -> {
                    // 不能添加单词到分类的提示
                    Toast errorAddTint = Toast.makeText(context, context.getResources().getString(R.string.error_add_hint), Toast.LENGTH_SHORT);
                    errorAddTint.setGravity(Gravity.CENTER, 0, 500);
                    errorAddTint.show();
                });

                 */
            }
        }

        @Override
        public void startDrag(RecyclerView.ViewHolder viewHolder) {
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

}
