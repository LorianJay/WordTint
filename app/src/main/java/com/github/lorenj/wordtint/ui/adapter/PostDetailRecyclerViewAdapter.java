package com.github.lorenj.wordtint.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.entity.Comment;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class PostDetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerViewAdapterItemChange<Comment> {

    private final Context context;
    private final List<Comment> allComments = new ArrayList<>(5);
    private final Handler updateUIHandler;
    private final Random random = new Random();
    private Spanned markDownText;
    private RelativeLayout commentOrderLayout;
    // 帖子的id
    private final Long postId;
    // markdown原文
    private String markdownOrigin;

    public PostDetailRecyclerViewAdapter(Context context, Long postId) {
        this.context = context;
        this.postId = postId;
        updateUIHandler = new Handler(context.getMainLooper());
    }

    public void setMarkDownText(Spanned markDownText) {
        this.markDownText = markDownText;
    }

    // 后期这里可以变为获取任意的一个Item
    public RelativeLayout getCommentOrderLayout() {
        return commentOrderLayout;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new CommentHeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_element_header, parent, false));
        } else if (viewType == 2) {
            return new TransparentViewHolder(LayoutInflater.from(context).inflate(R.layout.transparent, parent, false), 300);
        }
        return new CommentViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof CommentViewHolder) {
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            Comment comment = allComments.get(position - 1);
            commentViewHolder.commentAuthorFace.setImageResource(comment.getFace());
            commentViewHolder.name.setText(comment.getName());
            commentViewHolder.level.setText(String.valueOf(comment.getLevel()));
            commentViewHolder.comment.setText(comment.getCommentContext());
            commentViewHolder.praise.setText(String.valueOf(comment.getPraise()));
            if (!commentViewHolder.firstBind) {
                commentViewHolder.firstBind = true;
            } else {
                commentViewHolder.updateStatus();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == getItemCount() - 1) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return allComments.size() + 2;
    }

    @Override
    public void addItem(Comment item) {
        allComments.add(item);
        notifyItemInserted(allComments.size());
    }

    @Override
    public void removeItem(Comment item) {

    }

    @Override
    public void addAll(Collection<Comment> comments) {
        for (Comment comment : comments) {
            addItem(comment);
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder implements Runnable, View.OnClickListener {
        private View itemView;
        private ImageFilterView commentAuthorFace;
        private TextView name, level, date, comment, open, praise;
        private int originLines, maxLines;
        // 展开的状态,一开始是false.也就是说一开始是折叠的状态
        private boolean openStatus = false;
        private boolean firstBind = false;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.commentAuthorFace = itemView.findViewById(R.id.comment_element_face);
            this.name = itemView.findViewById(R.id.comment_element_name);
            this.level = itemView.findViewById(R.id.comment_element_level);
            this.comment = itemView.findViewById(R.id.comment_element_context);
            this.open = itemView.findViewById(R.id.comment_element_open);
            this.praise = itemView.findViewById(R.id.comment_element_praise);
            updateUIHandler.post(this);
            this.open.setOnClickListener(this);
        }

        @Override
        public void run() {
            originLines = comment.getLineCount();
            maxLines = comment.getMaxLines();
            if (originLines <= maxLines) {
                open.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.comment_element_open:
                    openStatus = !openStatus;
                    updateStatus();
                    break;
            }
        }

        public void updateStatus() {
            if (originLines <= maxLines) {
                open.setVisibility(View.GONE);
            } else {
                open.setVisibility(View.VISIBLE);
            }
            if (openStatus) {
                open.setText(R.string.fold);
                comment.setMaxLines(originLines);
            } else {
                open.setText(R.string.open);
                comment.setMaxLines(maxLines);
            }
        }

    }

    public class CommentHeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView markDownTextView;

        public CommentHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.markDownTextView = itemView.findViewById(R.id.fragment_post_markdown_context);
            commentOrderLayout = itemView.findViewById(R.id.fragment_post_comment_order_layout);
        }
    }

}
