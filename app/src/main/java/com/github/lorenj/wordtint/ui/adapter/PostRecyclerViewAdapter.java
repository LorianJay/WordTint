package com.github.lorenj.wordtint.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.lorenj.wordtint.context.AnyLanguageWordProperties;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.entity.dto.PostAbstractVO;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.customview.RoundImageView;
import com.github.lorenj.wordtint.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;


public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.RecyclerViewHolder>
        implements RecyclerViewAdapterItemChange<PostAbstractVO> {

    private final Context context;

    private final List<PostAbstractVO> allPostDTOList = new ArrayList<>();

    public PostRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_home_post_cover_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PostAbstractVO postAbstractVO = allPostDTOList.get(position);
        holder.title.setText(postAbstractVO.getPostDTO().getTitle());
        holder.userName.setText(postAbstractVO.getPostCreateUser().getNick());
        // 点赞数显示处理
        float praiseCount = new Random().nextInt(100);
        String praiseCountText = String.valueOf(praiseCount);
        if (praiseCount >= 1000 && praiseCount < 10000) {
            praiseCountText = String.format("%.1f", praiseCount / 1000) + context.getResources().getString(R.string.thousand);
        } else if (praiseCount >= 10000) {
            praiseCountText = String.format("%.1f", praiseCount / 10000) + context.getResources().getString(R.string.ten_thousand);
        }else {
            praiseCountText = String.format("%.0f", praiseCount);
        }
        holder.praiseCount.setText(praiseCountText);
        Glide.with(context)
                .load(Uri.parse(AnyLanguageWordProperties.imagePrefix + postAbstractVO.getPostDTO().getCoverUrl()))
                .into(holder.postCover);
    }

    @Override
    public int getItemCount() {
        return allPostDTOList.size();
    }

    @Override
    public void addItem(PostAbstractVO item) {
        allPostDTOList.add(item);
        notifyItemChanged(allPostDTOList.size() - 1);
    }

    @Override
    public void removeItem(PostAbstractVO item) {

    }

    @Override
    public void addAll(Collection<PostAbstractVO> postDTOS) {
        allPostDTOList.addAll(postDTOS);
        notifyItemRangeChanged(0, allPostDTOList.size());
    }

    @Override
    public void replaceAll(Collection<PostAbstractVO> postAbstractVOS) {
        allPostDTOList.clear();
        allPostDTOList.addAll(postAbstractVOS);
        notifyItemRangeChanged(0, postAbstractVOS.size());
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final View itemView;
        private final RoundImageView postCover;
        private final TextView title, userName, praiseCount;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.postCover = this.itemView.findViewById(R.id.fragment_home_post_cover_element_cover);
            this.title = this.itemView.findViewById(R.id.fragment_home_post_cover_element_title);
            this.userName = this.itemView.findViewById(R.id.fragment_home_post_cover_element_author_name);
            this.praiseCount = this.itemView.findViewById(R.id.fragment_home_post_cover_element_praise_count);
            this.postCover.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // 得到帖子Id
            Long postId = allPostDTOList.get(getAdapterPosition()).getPostDTO().getId();
            Bundle bundle = new Bundle();
            bundle.putLong("postId", postId);
            Navigation.findNavController(v).navigate(R.id.action_main_navigation_to_navigation_post, bundle, StaticFactory.getSimpleNavOptions());
        }
    }

}
