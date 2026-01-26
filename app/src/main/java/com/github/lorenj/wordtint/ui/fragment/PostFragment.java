package com.github.lorenj.wordtint.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.entity.Comment;
import com.github.lorenj.wordtint.test.BeanTest;
import com.github.lorenj.wordtint.ui.adapter.PostDetailRecyclerViewAdapter;
import com.github.lorenj.wordtint.R;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

/*
 * @author sukidayo
 * @date 2023/2/12 15:19:39
 */
public class PostFragment extends Fragment implements
        View.OnClickListener, AppBarLayout.OnOffsetChangedListener, View.OnScrollChangeListener {

    private View rootView;
    private Handler updateUIHandler;
    private ProgressBar loadingBar;
    private ImageButton backToTrace;
    private AppBarLayout appBarLayout;
    private RelativeLayout title;
    private LinearLayout jumpComment;
    private RelativeLayout commentOrderLayout;
    private final int[] locationOnScreen = new int[2];
    private volatile boolean isLoadMore;
    private RecyclerView commentRecyclerView;
    private PostDetailRecyclerViewAdapter commentAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_post, container, false);
        updateUIHandler = new Handler();
        bindView();
        // 先隐藏标题信息
        this.title.setVisibility(View.GONE);
        initView();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.toolbar_back_to_trace) {
            Navigation.findNavController(getView()).popBackStack();
        } else if (itemId == R.id.fragment_post_jump_comment) {
            appBarLayout.setExpanded(false, false);
            this.commentOrderLayout = commentAdapter.getCommentOrderLayout();
            commentOrderLayout.getLocationOnScreen(locationOnScreen);//测量某View相对于屏幕的距离
            int dy = locationOnScreen[1];
            commentRecyclerView.getLocationOnScreen(locationOnScreen);
            int distance = dy - locationOnScreen[1];
            if (distance > 0) {
                commentRecyclerView.fling(0, distance);
                commentRecyclerView.smoothScrollBy(0, distance);
            }
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        // 如果这两个值相等则代表当前滑动到底端了
        if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
            this.title.setVisibility(View.VISIBLE);
        } else {
            this.title.setVisibility(View.GONE);
        }
    }

    int count = 0;

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        int offset = commentRecyclerView.computeVerticalScrollOffset();  //当前滑动条在range中的偏移量
        int extent = commentRecyclerView.computeVerticalScrollExtent();// 计算滑动条的长度
        int range = commentRecyclerView.computeVerticalScrollRange();//计算滑动条的总共滑动范围range；
        Log.d("message-offset", String.valueOf(offset));
        Log.d("message-range", String.valueOf(range - extent));
        int percentage = (int) (100.0 * offset / (float) (range - extent));
        // 下滑加载更多,当滑动的距离+组件的高度-可滑动的范围<2000时加载新的数据(防止网络卡顿的预加载方案)
        if (!isLoadMore && percentage > 90) {
            StaticFactory.getExecutorService().submit(() -> {
                isLoadMore = true;
                // 先加载好所有的数据,然后再统一更新UI
                List<Comment> list = new ArrayList<>(6);
                for (int i = 0; i < 6; i++) {
                    Comment comment = BeanTest.createComment(getContext());
                    list.add(comment);
                }
                count += 10;
                if (count <= 100) {
                    updateUIHandler.post(() -> commentAdapter.addAll(list));
                }

                isLoadMore = false;
            });

        }
    }

    private void initView() {
        this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        StaticFactory.getExecutorService().submit(() -> {
            if (getArguments() == null || !getArguments().containsKey("postId")) {
                return;
            }
            long postId = getArguments().getLong("postId");
            // 加载整个页面
            this.commentAdapter = new PostDetailRecyclerViewAdapter(getContext(), postId);
            updateUIHandler.post(() -> commentRecyclerView.setAdapter(commentAdapter));
        });
        loadingBar.setVisibility(View.GONE);
    }

    private void bindView() {
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.loadingBar = rootView.findViewById(R.id.fragment_post_loading_bar);
        this.appBarLayout = rootView.findViewById(R.id.fragment_post_app_bar_layout);
        this.title = rootView.findViewById(R.id.fragment_post_title);
        this.jumpComment = rootView.findViewById(R.id.fragment_post_jump_comment);
        this.commentRecyclerView = rootView.findViewById(R.id.fragment_post_comment_recycler_view);

        this.jumpComment.setOnClickListener(this);
        this.appBarLayout.addOnOffsetChangedListener(this);
        this.backToTrace.setOnClickListener(this);
        this.commentRecyclerView.setOnScrollChangeListener(this);
    }
}