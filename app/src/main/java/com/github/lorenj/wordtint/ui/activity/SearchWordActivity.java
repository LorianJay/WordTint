package com.github.lorenj.wordtint.ui.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.entity.WordSearchEntity;
import com.github.lorenj.wordtint.database.entity.WordStarEntity;
import com.github.lorenj.wordtint.entity.dto.WordCategoryWordDTO;
import com.github.lorenj.wordtint.handler.StarFunctionHandler;
import com.github.lorenj.wordtint.handler.impl.AbstractStarFunctionHandler;
import com.github.lorenj.wordtint.ui.adapter.SimpleItemTouchHelperCallback;
import com.github.lorenj.wordtint.ui.adapter.StarResultAdapter;
import com.github.lorenj.wordtint.ui.adapter.star.StarCategoryAdapter;
import com.github.lorenj.wordtint.ui.adapter.wordsearch.ResultWebViewHandler;
import com.github.lorenj.wordtint.ui.adapter.wordsearch.SelectWordListAdapter;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SearchWordActivity extends AppCompatActivity
        implements View.OnClickListener, SearchView.OnQueryTextListener {

    /**
     * UI相关
     */
    private ImageView backToTrace;
    private AlertDialog loadingDialog = null;
    private WebView lightResult;
    private TextView starCurrentWord;
    private RecyclerView starList;
    private TextView starCreateCategory;
    private ImageView controlPlay;
    private LinearLayout resultArea;
    // 收藏夹列表单词显示适配器
    private StarResultAdapter chineseAnswerAdapterDrawer;
    private StarCategoryAdapter starCategoryAdapter;
    private Handler updateUIHandler = new Handler(Looper.getMainLooper());
    private LinearLayout analysisWord, openStarDrawer;
    private TextView originWord;
    // 收藏界抽屉布局
    private DrawerLayout starDrawer;
    // 新型中文显示处理器
    private ResultWebViewHandler resultWebViewHandler, starResultWebViewHandler;

    // 对于本类来说,如果当前的格式是经典模式,则代表当前展示的功能是查词功能,否则当前是联想模式,不包含查词的功能
    private SearchView searchInput;

    /**
     * 执行器
     */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> currentTask = null;
    // 单词搜索列表
    private RecyclerView selectWordList;
    // 单词搜索列表的adapter
    private SelectWordListAdapter selectWordListAdapter;
    // 当前选中的单词
    private WordCategoryWordDTO currentViewWord = null;
    // 当前是否是最后一页,为了防止重复请求
    private volatile boolean lastList = false;
    // 判断当前是否有任务正在执行
    private volatile boolean isRunning = false;
    // 单词音频播放器
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    /**
     * 数据库对象
     */
    private APPDatabase appDatabase;
    /**
     * 当前查询的页面参数
     */
    private int page = 0;
    private final int PAGE_SIZE = 25;
    private boolean lastPage = false;

    /**
     * 单词收藏需要获取当前的单词id
     */
    private StarFunctionHandler starFunctionHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_word);
        bindView();
        initView();
    }

    @Override
    public void onDestroy() {
        if (currentTask != null && !currentTask.isDone()) currentTask.cancel(true);
        super.onDestroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            setResult(RESULT_OK);
            finish();
        }
        return true;
    }

/*
    @Override
    public void viewClickCallBack(WordDTOLocal wordDTOLocal) {
        // 设置搜索单词的回调事件
        if (queryTimer != null) {
            queryTimer.cancel();
            isRunning = false;
        }
        // 将当前搜索得到的单词转换为选中的单词
        this.currentViewWord = new WordCategoryWordDTO();
        currentViewWord.setWordId(wordDTOLocal.getId());
        visibleWordAllMessage(wordDTOLocal);
    }
 */

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.iv_search_word_back) {
            setResult(RESULT_OK);
            finish();
        } else if (itemId == R.id.fragment_search_word_click_analysis_word) {

        } else if (itemId == R.id.tv_recite_star_create) {
            // 添加一个新的收藏夹
            View addNewCategory = getLayoutInflater().inflate(R.layout.dialog_recite_new_star, null);
            EditText categoryTile = addNewCategory.findViewById(R.id.et_new_star_title);
            EditText categoryDescribe = addNewCategory.findViewById(R.id.et_new_star_describe);
            new AlertDialog.Builder(this)
                    .setView(addNewCategory)
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                        WordStarEntity wordStarEntity = new WordStarEntity();
                        wordStarEntity.title = categoryTile.getText().toString();
                        wordStarEntity.describeInfo = categoryDescribe.getText().toString();
                        StaticFactory.getExecutorService().execute(() -> {
                            starFunctionHandler.createNewStar(wordStarEntity);
                            updateUIHandler.post(() -> starCategoryAdapter.notifyItemInserted(starFunctionHandler.getAllStarList().size() - 1));
                        });
                    })
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    })
                    .show();
        } else if (itemId == R.id.sv_search_word_control) {
            searchInput.setIconified(false);
            searchInput.requestFocus();
        } else if (itemId == R.id.ll_search_word_star) {
            starDrawer.openDrawer(GravityCompat.END);
        } else if (itemId == R.id.iv_search_word_control_play) {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    }

    /**
     * 当用户点击提交之后,立即执行搜索操作
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return onQueryTextChange(query);
    }

    /**
     * 搜索列表监听事件<br>
     * 当搜索列表有文字发生改变的时候,后台就应该进行搜索查询了
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            if (currentTask != null && !currentTask.isDone()) currentTask.cancel(true);
            this.isRunning = true;
            this.currentTask = this.scheduler.schedule(getQueryTask(), 1000, TimeUnit.MILLISECONDS);
            page = 0;
        }
        return false;
    }

    /**
     * 获得具体的查询函数
     */
    private TimerTask getQueryTask() {
        return new TimerTask() {
            @Override
            public void run() {
                StaticFactory.getExecutorService().submit(() -> {
                    // 显示单词搜索列表,隐藏单词详情列表
                    String searchText = searchInput.getQuery().toString();
                    Integer countWord = appDatabase.wordSearchDao().countWordLikeOrigin(searchText);
                    lastPage = (page + 1) * PAGE_SIZE >= countWord;
                    List<WordSearchEntity> wordSearchEntityList = appDatabase.wordSearchDao().searchWordLikeOrigin(
                            searchText,
                            page * PAGE_SIZE,
                            PAGE_SIZE);
                    if (!lastPage) {
                        WordSearchEntity loadMore = new WordSearchEntity();
                        loadMore.wordId = -1;
                        loadMore.wordOrigin = "-1";
                        wordSearchEntityList.add(loadMore);
                    }
                    updateUIHandler.post(() -> {
                        selectWordList.setVisibility(View.VISIBLE);
                        resultArea.setVisibility(View.GONE);
                        // 第一次是全量更新,第二次是增量更新
                        if (page == 0) {
                            selectWordListAdapter.replaceAll(wordSearchEntityList);
                        } else {
                            selectWordListAdapter.addAll(wordSearchEntityList);
                        }
                    });
                });
                isRunning = false;
            }
        };

    }


    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        starDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        this.appDatabase = APPDatabase.getInstance(this);
        // 隐藏单词的附加显示内容、隐藏收藏界面的答案信息
        this.resultWebViewHandler = new ResultWebViewHandler(this, findViewById(R.id.wv_search_word_result));
        this.starResultWebViewHandler = new ResultWebViewHandler(this, findViewById(R.id.wv_star_recite_result));
        originWord.setText("");
        starCurrentWord.setText("");
        loadingDialog = new AlertDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_loading, null))
                .setCancelable(false)
                .show();
        // 中文结果显示的RecyclerView
        this.starList.setLayoutManager(new LinearLayoutManager(this));
        this.selectWordList.setLayoutManager(new LinearLayoutManager(this));
        StaticFactory.getExecutorService().submit(() -> {
            starFunctionHandler = new AbstractStarFunctionHandler(this) {
                @Override
                public Integer getCurrentFocusWordId() {
                    return null;
                }
            };
            this.starCategoryAdapter = new StarCategoryAdapter(this, starFunctionHandler);
            // 初始化单词列表的adapter
            this.selectWordListAdapter = new SelectWordListAdapter(this);
            // 设置选中单词的回调事件
            //this.selectWordListAdapter.setRecycleViewItemClickCallBack(this);
            // 绑定ItemTouchHelper,实现单个列表的编辑删除等功能
            ItemTouchHelper touchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(starCategoryAdapter));
            starCategoryAdapter.setStartDragListener(touchHelper::startDrag);
            updateUIHandler.post(() -> {
                this.starList.setAdapter(starCategoryAdapter);
                this.selectWordList.setAdapter(selectWordListAdapter);
                touchHelper.attachToRecyclerView(starList);
                this.lightResult.setVisibility(View.GONE);
                loadingDialog.dismiss();
            });
        });
        // 检测当前单词搜索列表是否滑动到底部需要加载更多的单词
        selectWordList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 滑动到底部
                if (!recyclerView.canScrollVertically(1) && !lastList) {
                    // 优先执行搜索事件或者已有的分页查询事件
                    if (!isRunning) {
                        isRunning = true;
                        if (currentTask != null && !currentTask.isDone()) currentTask.cancel(true);
                        page++;
                        scheduler.schedule(getQueryTask(), 1000, TimeUnit.MILLISECONDS);
                    }
                }
            }
        });

    }

    private void bindView() {
        this.backToTrace = findViewById(R.id.iv_search_word_back);
        this.resultArea = findViewById(R.id.ll_search_word_result);
        this.originWord = findViewById(R.id.tv_recite_origin);
        this.lightResult = findViewById(R.id.wv_search_word_result);
        this.selectWordList = findViewById(R.id.rv_search_word_select_list);

        this.analysisWord = findViewById(R.id.ll_search_word_analysis);
        // 收藏夹相关
        this.starDrawer = findViewById(R.id.dl_search_word);
        this.openStarDrawer = findViewById(R.id.ll_search_word_star);
        this.starCurrentWord = findViewById(R.id.tv_recite_star_current_word);
        this.starList = findViewById(R.id.rc_recite_star_list);
        this.starCreateCategory = findViewById(R.id.tv_recite_star_create);

        this.controlPlay = findViewById(R.id.iv_search_word_control_play);
        this.searchInput = findViewById(R.id.sv_search_word_control);

        this.searchInput.setOnClickListener(this);
        this.searchInput.setOnQueryTextListener(this);
        this.backToTrace.setOnClickListener(this);
        this.analysisWord.setOnClickListener(this);
        this.openStarDrawer.setOnClickListener(this);
        this.controlPlay.setOnClickListener(this);
    }
}