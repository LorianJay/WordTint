package com.github.lorenj.wordtint.ui.adapter.star;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.entity.WordStarEntity;
import com.github.lorenj.wordtint.database.entity.relation.WordStarWithWordIdEntity;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.handler.StarFunctionHandler;
import com.github.lorenj.wordtint.ui.adapter.listener.MoveAndSwipedListener;
import com.github.lorenj.wordtint.ui.adapter.listener.StateChangedListener;

import java.util.function.Consumer;


/**
 * 专门用于排序的adapter
 *
 * @author cnsukidayo
 * @date 2023/1/7 17:35
 */
public class StarSimpleAdapter extends RecyclerView.Adapter<StarSimpleAdapter.StarHeaderViewHolder>
        implements MoveAndSwipedListener, RecyclerViewAdapterItemChange<WordStarEntity> {

    private final Context context;
    private Consumer<RecyclerView.ViewHolder> startDragListener;
    private final StarFunctionHandler startFunctionHandler;

    public StarSimpleAdapter(Context context, StarFunctionHandler starFunctionHandler) {
        this.context = context;
        this.startFunctionHandler = starFunctionHandler;
    }

    @NonNull
    @Override
    public StarHeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StarHeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_star_simple, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StarHeaderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WordStarEntity wordStarEntity = startFunctionHandler.getAllStarList().get(position).wordStarEntity;
        holder.title.setText(startFunctionHandler.calculationTitle(wordStarEntity));
        holder.describe.setText(startFunctionHandler.calculationDescribe(wordStarEntity));
    }

    @Override
    public int getItemCount() {
        return startFunctionHandler.getAllStarList().size();
    }

    /**
     * 设置拖拽的回调方法
     */
    public void setStartDragListener(Consumer<RecyclerView.ViewHolder> startDragListener) {
        this.startDragListener = startDragListener;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        WordStarWithWordIdEntity fromStar = startFunctionHandler.getAllStarList().get(fromPosition);
        WordStarWithWordIdEntity toStar = startFunctionHandler.getAllStarList().get(toPosition);
        notifyItemMoved(fromPosition, toPosition);
        startFunctionHandler.moveStar(fromStar, toStar);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public class StarHeaderViewHolder extends RecyclerView.ViewHolder
            implements StateChangedListener, View.OnLongClickListener {
        public View itemView;
        public TextView title, describe;
        public LinearLayout clickLayout;

        public StarHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.clickLayout = itemView.findViewById(R.id.ll_simple_star);
            this.title = itemView.findViewById(R.id.tv_simple_star_title);
            this.describe = itemView.findViewById(R.id.tv_simple_star_describe);
            this.clickLayout.setOnLongClickListener(this);
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
        public boolean onLongClick(View v) {
            int clickViewId = v.getId();
            if (clickViewId == R.id.ll_simple_star
                    && startDragListener != null) {
                startDragListener.accept(this);
            }
            return false;
        }

    }
}
