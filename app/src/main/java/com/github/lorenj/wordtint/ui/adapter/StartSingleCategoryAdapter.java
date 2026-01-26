package com.github.lorenj.wordtint.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.github.lorenj.wordtint.entity.dto.WordCategoryDTO;
import com.github.lorenj.wordtint.entity.dto.WordCategoryDetailVO;
import com.github.lorenj.wordtint.handler.CategoryFunctionHandler;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.listener.MoveAndSwipedListener;
import com.github.lorenj.wordtint.ui.adapter.listener.StateChangedListener;
import com.github.lorenj.wordtint.R;

import java.util.Optional;
import java.util.function.Consumer;


/**
 * 每个分类的Adapter
 *
 * @author cnsukidayo
 * @date 2023/1/7 17:35
 */
public class StartSingleCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements MoveAndSwipedListener, RecyclerViewAdapterItemChange<WordCategoryDTO> {

    private final Context context;
    // 用于回调startDrag方法的接口,该接口耦合了,定义方式不好.
    private Consumer<RecyclerView.ViewHolder> startDragListener;
    // 用于处理单词收藏功能的Handler
    private CategoryFunctionHandler startFunctionHandler;

    public StartSingleCategoryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SingleSingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SingleSingleViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_start_single_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 重置改变,防止由于复用而导致的显示问题
        if (holder instanceof SingleSingleViewHolder) {
            SingleSingleViewHolder singleSingleViewHolder = (SingleSingleViewHolder) holder;
            singleSingleViewHolder.scroller.scrollTo(0, 0);
            singleSingleViewHolder.underNowStartAllWord.setVisibility(View.GONE);
            singleSingleViewHolder.title.setText(startFunctionHandler.calculationTitle(position));
            singleSingleViewHolder.describe.setText(startFunctionHandler.calculationDescribe(position));
        }
    }

    @Override
    public int getItemCount() {
        return startFunctionHandler.categoryListSize();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        startFunctionHandler.moveCategory(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        startFunctionHandler.removeCategory(position);
        notifyItemRemoved(position);
    }

    public void setStartFunctionHandler(CategoryFunctionHandler startFunctionHandler) {
        this.startFunctionHandler = startFunctionHandler;
    }

    public void setStartDragListener(Consumer<RecyclerView.ViewHolder> startDragListener) {
        this.startDragListener = startDragListener;
    }


    @Override
    public void addItem(WordCategoryDTO wordCategoryDTO) {
        startFunctionHandler.addNewCategory(wordCategoryDTO);
        notifyItemInserted(startFunctionHandler.categoryListSize() - 1);
    }

    @Override
    public void removeItem(WordCategoryDTO wordCategoryDTO) {

    }

    protected class SingleSingleViewHolder extends RecyclerView.ViewHolder
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
            this.title = itemView.findViewById(R.id.fragment_word_credit_start_single_category_title);
            this.describe = itemView.findViewById(R.id.fragment_word_credit_start_single_category_detail);
            this.scroller = itemView.findViewById(R.id.fragment_word_credit_start_single_category_scroller);
            this.openList = itemView.findViewById(R.id.fragment_word_credit_start_open_list);
            this.listState = itemView.findViewById(R.id.fragment_word_credit_start_list_state);
            this.edit = itemView.findViewById(R.id.fragment_word_credit_start_edit);
            this.delete = itemView.findViewById(R.id.fragment_word_credit_start_delete);
            this.move = itemView.findViewById(R.id.fragment_word_credit_start_move);
            this.addWord = itemView.findViewById(R.id.fragment_word_credit_start_add_word);
            this.underNowStartAllWord = itemView.findViewById(R.id.fragment_word_credit_start_single_category_word_recycler_view);
            // 加载当前分类下的所有单词
            this.underNowStartAllWord.setLayoutManager(new LinearLayoutManager(context));
            this.startSingleCategoryWordAdapter = new StartSingleCategoryWordAdapter(context);
            this.underNowStartAllWord.setAdapter(startSingleCategoryWordAdapter);
            this.touchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(startSingleCategoryWordAdapter));
            this.touchHelper.attachToRecyclerView(underNowStartAllWord);
            startSingleCategoryWordAdapter.setCategoryWordFunctionHandler(startFunctionHandler);
            startSingleCategoryWordAdapter.setFunctionListener(this);

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
            startFunctionHandler.updateWordCategoryList();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int clickViewId = v.getId();
            if (clickViewId == R.id.fragment_word_credit_start_move &&
                    event.getAction() == MotionEvent.ACTION_DOWN && startDragListener != null) {
                startDragListener.accept(this);
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            int clickViewId = v.getId();
            if (clickViewId == R.id.fragment_word_credit_start_delete) {
                // 删除当前Item
                startFunctionHandler.removeCategory(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
            } else if (clickViewId == R.id.fragment_word_credit_start_edit) {
                // 编辑收藏夹信息
                View editStart = LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_start_edit_new_dialog, null);
                EditText categoryTile = editStart.findViewById(R.id.fragment_word_credit_start_new_title);
                EditText categoryDescribe = editStart.findViewById(R.id.fragment_word_credit_start_new_describe);
                new AlertDialog.Builder(context)
                        .setView(editStart)
                        .setCancelable(true)
                        .setPositiveButton("确定", (dialog, which) -> {
                            WordCategoryDetailVO wordCategoryDetailVO = new WordCategoryDetailVO();
                            wordCategoryDetailVO.setTitle(categoryTile.getText().toString());
                            wordCategoryDetailVO.setDescribeInfo(categoryDescribe.getText().toString());
                            startFunctionHandler.updateWordCategoryDto(getAdapterPosition(), wordCategoryDetailVO);
                            title.setText(startFunctionHandler.calculationTitle(getAdapterPosition()));
                            describe.setText(startFunctionHandler.calculationDescribe(getAdapterPosition()));
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                        })
                        .show();
            } else if (clickViewId == R.id.fragment_word_credit_start_open_list) {
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
            } else if (clickViewId == R.id.fragment_word_credit_start_add_word) {
                Optional.ofNullable(startFunctionHandler.getCurrentViewWord()).ifPresentOrElse(wordCategoryWordDTO -> {
                    startSingleCategoryWordAdapter.addItem(wordCategoryWordDTO);
                    title.setText(startFunctionHandler.calculationTitle(getAdapterPosition()));
                    describe.setText(startFunctionHandler.calculationDescribe(getAdapterPosition()));
                }, () -> {
                    // 不能添加单词到分类的提示
                    Toast errorAddTint = Toast.makeText(context, context.getResources().getString(R.string.error_add_hint), Toast.LENGTH_SHORT);
                    errorAddTint.setGravity(Gravity.CENTER, 0, 500);
                    errorAddTint.show();
                });
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
            title.setText(startFunctionHandler.calculationTitle(getAdapterPosition()));
            describe.setText(startFunctionHandler.calculationDescribe(getAdapterPosition()));
        }
    }

}
