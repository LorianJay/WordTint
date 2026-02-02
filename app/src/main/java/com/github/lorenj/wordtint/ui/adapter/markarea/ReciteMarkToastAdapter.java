package com.github.lorenj.wordtint.ui.adapter.markarea;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.handler.WordFunctionHandler;

import java.util.List;


/**
 * @author cnsukidayo
 * @date 2023/1/7 17:35
 */
public class ReciteMarkToastAdapter extends RecyclerView.Adapter<ReciteMarkToastAdapter.ToastViewHolder>
        implements RecyclerViewAdapterItemChange<MarkColor> {

    private final Context context;
    private final WordFunctionHandler wordFunctionHandler;

    public ReciteMarkToastAdapter(Context context, WordFunctionHandler wordFunctionHandler) {
        this.context = context;
        this.wordFunctionHandler = wordFunctionHandler;
    }

    @NonNull
    @Override
    public ToastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ToastViewHolder(LayoutInflater.from(context).inflate(R.layout.item_toast_mark, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ToastViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MarkColor markColor = MarkColor.values()[position];
        holder.toastMark.getDrawable().setTint(context.getResources().getColor(markColor.getMapColorID(), null));
        holder.viewMark.setBackgroundColor(context.getResources().getColor(markColor.getMapColorID(), null));
        if (!wordFunctionHandler.getWordFunctionHandlerState().isFunctionAreaFold()) {
            // 如果当前状态是展开
            holder.toastMark.setVisibility(View.VISIBLE);
            holder.viewMark.setVisibility(wordFunctionHandler.getCurrentFocusWord().getMarkColorList().contains(markColor) ?
                    View.VISIBLE : View.INVISIBLE);
        } else {
            // 如果当前状态是折叠
            holder.toastMark.setVisibility(View.GONE);
            holder.viewMark.setVisibility(wordFunctionHandler.getCurrentFocusWord().getMarkColorList().contains(markColor) ?
                    View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ToastViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) super.onBindViewHolder(holder, position, payloads);
        // 一定是展开状态才可以点击
        for (Object payload : payloads) {
            MarkColor markColor = MarkColor.values()[position];
            if (payload == Item.CLICK_MARK) {
                holder.viewMark.setVisibility(wordFunctionHandler.getCurrentFocusWord().getMarkColorList().contains(markColor) ?
                        View.VISIBLE : View.INVISIBLE);
            }
            if (payload == Item.SWITCH_SELECT) {
                if (position == wordFunctionHandler.getWordFunctionHandlerState().getCurrentFocusSwitchPosition()) {
                    holder.toastMark.setForeground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.fg_selected_border, null));
                    holder.toastMark.setScaleX(0.9f);
                    holder.toastMark.setScaleY(0.9f);
                }
            }
            if (payload == Item.SWITCH_DESELECT) {
                holder.toastMark.setForeground(null);
                holder.toastMark.setScaleX(1f);
                holder.toastMark.setScaleY(1f);
            }
        }
    }

    @Override
    public int getItemCount() {
        return MarkColor.values().length;
    }

    public class ToastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View itemView;
        public ImageView toastMark;
        public View viewMark;

        public ToastViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.toastMark = itemView.findViewById(R.id.iv_toast_mark);
            this.viewMark = itemView.findViewById(R.id.iv_view_mark);
            this.toastMark.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemId = v.getId();
            if (itemId != R.id.iv_toast_mark ||
                    wordFunctionHandler.getWordFunctionHandlerState().isSwitching()) {
                return;
            }
            MarkColor markColor = MarkColor.values()[getAdapterPosition()];
            // 如果当前正在选择变色龙
            if (wordFunctionHandler.getWordFunctionHandlerState().isSelectChameleon()) {
                wordFunctionHandler.getWordFunctionHandlerState().setFunctionAreaFold(true);
                wordFunctionHandler.getWordFunctionHandlerState().setSelectChameleon(false);
                wordFunctionHandler.getWordFunctionHandlerState().setChameleon(markColor);
                notifyItemRangeChanged(0, getItemCount());
                return;
            }
            // 如果是棕色就跳过(逻辑写死)
            if (markColor == MarkColor.BROWN) return;
            FunctionWordVO currentFocusWord = wordFunctionHandler.getCurrentFocusWord();
            if (currentFocusWord.getMarkColorList().contains(markColor)) {
                currentFocusWord.getMarkColorList().remove(markColor);
            } else {
                currentFocusWord.getMarkColorList().add(markColor);
            }
            notifyItemChanged(getAdapterPosition(), Item.CLICK_MARK);
        }
    }

    public enum Item {
        /**
         * 点击标签
         */
        CLICK_MARK,
        /**
         * 滑动切换选中
         */
        SWITCH_SELECT,
        /**
         * 滑动切换未选中
         */
        SWITCH_DESELECT;
    }


}
