package com.github.lorenj.wordtint.ui.adapter.wordsearch;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.utils.DPUtils;

/**
 * 加载更多
 */
public class LoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private int visible = View.GONE;

    public LoadMoreAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.theme_color)));
        RelativeLayout loadMoreLinearLayout = new RelativeLayout(context);
        loadMoreLinearLayout.setGravity(Gravity.CENTER);
        loadMoreLinearLayout.addView(progressBar, RelativeLayout.LayoutParams.MATCH_PARENT, DPUtils.dp2px(20));
        return new LoadMoreViewHolder(loadMoreLinearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    }

    public void setVisible(int visible) {
        if (this.visible == visible) return;
        this.visible = visible;
        if (visible == View.VISIBLE) notifyItemInserted(0);
        if (visible == View.GONE) notifyItemRemoved(0);

    }

    @Override
    public int getItemCount() {
        return visible == View.VISIBLE ? 1 : 0;
    }


    /**
     * 加载更多的组件
     */
    public static class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}