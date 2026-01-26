package com.github.lorenj.wordtint.ui.fragment;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.entity.PostCover;
import com.github.lorenj.wordtint.test.BeanTest;
import com.github.lorenj.wordtint.ui.MainActivity;
import com.github.lorenj.wordtint.ui.adapter.HomePictureViewAdapter;
import com.github.lorenj.wordtint.ui.adapter.PostRecyclerViewAdapter;
import com.github.lorenj.wordtint.ui.adapter.listener.NavigationItemSelectListener;
import com.github.lorenj.wordtint.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class HomeFragment extends Fragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, NestedScrollView.OnScrollChangeListener, NavigationItemSelectListener, View.OnFocusChangeListener {

    private View rootView;
    private ImageFilterView popDrawerLayoutButton;
    // 当用户点击头像后弹出抽屉布局的回调时间,委托上一层来执行该事件
    private OnClickListener popDrawerListener;
    private SwipeRefreshLayout downRefreshLayout;
    private Handler updateUIHandler;
    private RecyclerView postRecyclerView;
    private ViewPager2 imageRotationViewPager;
    private NestedScrollView nestedScrollView;
    private volatile int preImageRotationPosition;
    private LinearLayout imageRotationContainer;
    // 搜索框
    private SearchView searchView;
    // 搜索框的提示组件
    private TextView searchViewHint;
    // 用户是否正在滑动图片的标识
    private volatile boolean userSlideImage;
    private volatile boolean isLoadMore;
    // 跳转帖子发布页面按钮
    private ImageView pushPostCardView;

    private StaggeredGridLayoutManager postRecyclerViewLayoutManager;
    private PostRecyclerViewAdapter postRecyclerViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        updateUIHandler = new Handler();
        MainActivity mainActivity = (MainActivity) rootView.getContext();
        bindView();
        initView();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.fragment_home_user_face) {
            popDrawerListener.onClickUserFace(v);
        } else if (itemId == R.id.fragment_home_search_view) {
            searchView.setIconified(false);
            searchView.requestFocus();
        } else if (itemId == R.id.fragment_home_push_post) {
            Navigation.findNavController(getView()).navigate(R.id.action_main_navigation_to_navigation_push_post, null,
                    StaticFactory.getSimpleNavOptions());
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        // 解决上划冲突
        if (!downRefreshLayout.isRefreshing()) {
            if (scrollY - oldScrollY > 0) {
                downRefreshLayout.setEnabled(false);
            }
            downRefreshLayout.setEnabled(true);
        }
        // 下滑加载更多,当滑动的距离+组件的高度-可滑动的范围<2000时加载新的数据(防止网络卡顿的预加载方案)
        if (!isLoadMore && nestedScrollView.computeVerticalScrollRange() - (nestedScrollView.getScrollY() + nestedScrollView.getHeight()) < 1000) {
            StaticFactory.getExecutorService().submit(() -> {
                isLoadMore = true;
                // 先加载好所有的数据,然后再统一更新UI
                List<PostCover> list = new ArrayList<>(6);
                for (int i = 0; i < 6; i++) {
                    PostCover postCover = BeanTest.createPostCover();
                    list.add(postCover);
                }
                //updateUIHandler.post(() -> postRecyclerViewAdapter.addAll(list));
                isLoadMore = false;
            });
        }
    }

    @Override
    public void onRefresh() {
    }


    @Override
    public void onClickCurrentPage(@NonNull MenuItem item) {
        nestedScrollView.smoothScrollTo(nestedScrollView.getScrollX(), 0);
        downRefreshLayout.setRefreshing(true);
        onRefresh();
    }

    /**
     * 设置当HomeFragment页面点击头像后交由父级页面的回调时间
     *
     * @param popDrawerListener 回调接口
     */
    public void setPopDrawerListener(OnClickListener popDrawerListener) {
        this.popDrawerListener = popDrawerListener;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int itemId = v.getId();
        if (itemId == R.id.fragment_home_search_view) {
            searchViewHint.setVisibility(hasFocus ? ViewGroup.INVISIBLE : ViewGroup.VISIBLE);
        }
    }

    public interface OnClickListener {
        void onClickUserFace(View v);
    }

    /**
     * 初始化所有帖子,耗时方法放到线程池中执行,UI到时候统一更新.
     */
    private void initView() {
        this.postRecyclerViewLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        this.postRecyclerView.setLayoutManager(postRecyclerViewLayoutManager);
        this.postRecyclerViewAdapter = new PostRecyclerViewAdapter(getContext());
        this.postRecyclerView.setAdapter(postRecyclerViewAdapter);
        this.postRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int distance = getResources().getDimensionPixelSize(R.dimen.fragment_home_post_distance);
                outRect.set(distance, distance, distance, distance);
            }
        });
        this.imageRotationViewPager.setAdapter(new HomePictureViewAdapter(getContext()));
        this.imageRotationViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                View viewOval = imageRotationContainer.getChildAt(preImageRotationPosition);
                viewOval.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray, null)));
                viewOval.setAlpha(0.5f);
                viewOval = imageRotationContainer.getChildAt(position);
                viewOval.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white, null)));
                viewOval.setAlpha(1.0f);
                preImageRotationPosition = position;
                // 如果当前用户正在滑动图片,那么让图片自动轮播在一段时间内失效,虽然说下面的setCurrentItem方法也会当前监听时间,但是确保下面的方法调用userSlideImage = false;
                userSlideImage = true;
            }
        });
        // 获取帖子信息
        // 实现图片定时轮转 todo 实际上做起来挺麻烦的,目前这里只是做一个样式
        StaticFactory.getExecutorService().submit(() -> {
            // 获取首页帖子信息
            //CoreServiceRequestFactory.getInstance()
            //        .postRequest()
            //        .getPostListUncheck()
            //        .success(data -> {
            //            List<PostAbstractVO> postAbstractVOList = data.getData();
            //            updateUIHandler.post(() -> postRecyclerViewAdapter.addAll(postAbstractVOList));
            //        })
            //        .execute();
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!userSlideImage) {
                    int tempPosition = preImageRotationPosition;
                    tempPosition++;
                    if (tempPosition == 3) {
                        tempPosition = 0;
                    }
                    final int finalTempPosition = tempPosition;
                    updateUIHandler.post(() -> imageRotationViewPager.setCurrentItem(finalTempPosition));
                }
                userSlideImage = false;
            }
        });
    }

    private void bindView() {
        this.popDrawerLayoutButton = rootView.findViewById(R.id.fragment_home_user_face);
        this.downRefreshLayout = rootView.findViewById(R.id.fragment_home_swipe_refresh);
        this.postRecyclerView = rootView.findViewById(R.id.fragment_home_post_recycler_view);
        this.imageRotationViewPager = rootView.findViewById(R.id.fragment_home_picture_rotation);
        this.nestedScrollView = rootView.findViewById(R.id.fragment_home_nested_scroll_view);
        this.imageRotationContainer = rootView.findViewById(R.id.fragment_home_picture_rotation_oval_container);
        this.searchView = rootView.findViewById(R.id.fragment_home_search_view);
        this.searchViewHint = rootView.findViewById(R.id.fragment_home_search_view_hint);
        this.pushPostCardView = rootView.findViewById(R.id.fragment_home_push_post);

        this.popDrawerLayoutButton.setOnClickListener(this);
        this.nestedScrollView.setOnScrollChangeListener(this);
        this.searchView.setOnClickListener(this);
        this.searchView.setOnQueryTextFocusChangeListener(this);
        this.pushPostCardView.setOnClickListener(this);

        downRefreshLayout.setSize(CircularProgressDrawable.LARGE);
        downRefreshLayout.setColorSchemeResources(R.color.theme_color);
        downRefreshLayout.setOnRefreshListener(this);
    }

}