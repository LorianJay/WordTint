package com.github.lorenj.wordtint.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;


public class HomePictureViewAdapter extends RecyclerView.Adapter<HomePictureViewAdapter.RecyclerViewHolder> {

    private Context context;

    public HomePictureViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageFilterView imageView = new ImageFilterView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMarginStart(context.getResources().getDimensionPixelSize(R.dimen.fragment_home_image_rotation_margin));
        params.setMarginEnd(context.getResources().getDimensionPixelSize(R.dimen.fragment_home_image_rotation_margin));
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setRound(context.getResources().getDimensionPixelSize(R.dimen.fragment_home_image_rotation_radius));
        return new RecyclerViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        if (position % 4 == 0) {
            holder.imageView.setImageResource(R.drawable.post_cover0);
        } else if (position % 3 == 1) {
            holder.imageView.setImageResource(R.drawable.post_cover1);
        } else if (position % 3 == 2) {
            holder.imageView.setImageResource(R.drawable.post_cover2);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public RecyclerViewHolder(@NonNull ImageView itemView) {
            super(itemView);
            this.imageView = itemView;
        }
    }
}
 
