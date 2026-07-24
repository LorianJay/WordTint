package com.github.lorenj.wordtint.ui.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.entity.WordOriginEntity;
import com.github.lorenj.wordtint.database.entity.WordSearchEntity;
import com.github.lorenj.wordtint.database.entity.WordStarEntity;
import com.github.lorenj.wordtint.database.entity.relation.WordStarWithWordIdEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.StarFunctionHandler;
import com.github.lorenj.wordtint.handler.WordAudioHandler;
import com.github.lorenj.wordtint.handler.impl.AbstractStarFunctionHandler;
import com.github.lorenj.wordtint.ui.adapter.common.LoadMoreAdapter;
import com.github.lorenj.wordtint.ui.adapter.common.SimpleItemTouchHelperCallback;
import com.github.lorenj.wordtint.ui.adapter.star.StarListAdapter;
import com.github.lorenj.wordtint.ui.adapter.star.StarResultAdapter;
import com.github.lorenj.wordtint.ui.adapter.star.StarSimpleAdapter;
import com.github.lorenj.wordtint.ui.adapter.wordsearch.ResultWebViewHandler;
import com.github.lorenj.wordtint.ui.adapter.wordsearch.SelectWordListAdapter;
import com.github.lorenj.wordtint.ui.adapter.wordsearch.WordSearchViewModel;
import com.github.lorenj.wordtint.ui.dialog.WordOriginEditDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private LinearLayout editOriginButton;
    // 收藏夹列表单词显示适配器
    private StarResultAdapter chineseAnswerAdapterDrawer;
    private StarListAdapter starListAdapter;
    private StarSimpleAdapter starSimpleAdapter;
    private Handler updateUIHandler = new Handler(Looper.getMainLooper());
    private LinearLayout analysisWord, openStarDrawer;
    private ImageView starMove;
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
    private LoadMoreAdapter loadMoreAdapter;
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
    private WordSearchViewModel wordSearchViewModel;
    private int currentFocusWordId = 0;
    private boolean textChange = false;
    private long searchDelay = 500;
    /**
     * 是否正在排序收藏夹
     */
    private boolean sortStar = false;
    /**
     * 单词音频播放器
     */
    private final WordAudioHandler wordAudioHandler = StaticFactory.wordAudioHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_word);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.iv_search_word_back) {
            setResult(RESULT_OK);
            finish();
        }
        if (itemId == R.id.tv_recite_star_create) {
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
                            WordStarWithWordIdEntity newStar = starFunctionHandler.createNewStar(wordStarEntity);
                            updateUIHandler.post(() -> starListAdapter.addItem(newStar));
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
            wordAudioHandler.playWordAudio(starFunctionHandler.getCurrentFocusWord(), this);
        }
        if (itemId == R.id.ll_search_word_edit_origin) {
            StaticFactory.getExecutorService().execute(() -> {
                FunctionWordVO currentWord = starFunctionHandler.getCurrentFocusWord();
                List<WordOriginEntity> originList = appDatabase.wordOriginDao()
                        .findAllOriginWordById(currentWord.getWordId());
                Map<String, String> originalValues = new HashMap<>();
                for (WordOriginEntity entity : originList) {
                    originalValues.put(entity.key, entity.value);
                }
                updateUIHandler.post(() -> {
                    WordOriginEditDialog.show(SearchWordActivity.this, currentWord, originalValues,
                            (word, newCustomValues) -> {
                                StaticFactory.getExecutorService().execute(() -> {
                                    for (Map.Entry<String, String> entry : newCustomValues.entrySet()) {
                                        int rows = appDatabase.wordOriginDao().updateCustomValue(
                                                word.getWordId(), entry.getKey(), entry.getValue());
                                        if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                                            if (rows == 0) {
                                                WordOriginEntity newEntity = new WordOriginEntity();
                                                newEntity.wordId = word.getWordId();
                                                newEntity.key = entry.getKey();
                                                newEntity.value = "";
                                                newEntity.customValue = entry.getValue();
                                                appDatabase.wordOriginDao().insert(newEntity);
                                            }
                                            word.getValue().put(WordStructure.valueOf(entry.getKey()), entry.getValue());
                                        } else {
                                            String defaultValue = originalValues.get(entry.getKey());
                                            if (defaultValue != null) {
                                                word.getValue().put(WordStructure.valueOf(entry.getKey()), defaultValue);
                                            } else {
                                                word.getValue().remove(WordStructure.valueOf(entry.getKey()));
                                            }
                                        }
                                    }
                                    updateUIHandler.post(() -> visibleWordAllMessage(word));
                                });
                            });
                });
            });
        }
        if (itemId == R.id.iv_recite_star_move) {
            sortStar = !sortStar;
            if (sortStar) {
                this.starMove.getDrawable().setTint(getResources().getColor(R.color.theme_color, null));
                this.starList.setAdapter(starSimpleAdapter);
            } else {
                this.starMove.getDrawable().setTint(getResources().getColor(R.color.dark_gray, null));
                this.starListAdapter.refreshConcatAdapter();
                this.starList.setAdapter(starListAdapter.getGlobalAdapter());
            }
        }
    }

    /**
     * 当用户点击提交之后,立即执行搜索操作<br>
     * 如果自从上次搜索结束之后,输入框的内容没有发生任何改变<br>
     * 则无需重复搜索,直接复用上次的搜索结果
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (TextUtils.isEmpty(query)) return false;
        if (textChange) {
            return onQueryTextChange(query);
        } else {
            resultArea.setVisibility(View.GONE);
            selectWordList.setVisibility(View.VISIBLE);
        }
        return false;
    }

    /**
     * 搜索列表监听事件<br>
     * 当搜索列表有文字发生改变的时候,后台就应该进行搜索查询了
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            textChange = true;
            if (currentTask != null && !currentTask.isDone()) currentTask.cancel(true);
            page = 0;
            this.currentTask = this.scheduler.schedule(getQueryTask(), searchDelay, TimeUnit.MILLISECONDS);
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
                    updateUIHandler.post(() -> {
                        selectWordList.setVisibility(View.VISIBLE);
                        resultArea.setVisibility(View.GONE);
                        loadMoreAdapter.setVisible(lastPage ? View.GONE : View.VISIBLE);
                        // 第一次是全量更新,第二次是增量更新
                        if (page == 0) {
                            selectWordListAdapter.replaceAll(wordSearchEntityList);
                        } else {
                            selectWordListAdapter.addAll(wordSearchEntityList);
                        }
                    });
                });
            }
        };
    }


    private void initView() {
        loadingDialog = new AlertDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_loading, null))
                .setCancelable(false)
                .show();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        starDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        this.appDatabase = APPDatabase.getInstance(this);
        // 隐藏单词的附加显示内容、隐藏收藏界面的答案信息
        this.resultWebViewHandler = new ResultWebViewHandler(this, findViewById(R.id.wv_search_word_result));
        this.starResultWebViewHandler = new ResultWebViewHandler(this, findViewById(R.id.wv_star_recite_result));
        originWord.setText("");
        starCurrentWord.setText("");
        // 设置选中单词的回调事件
        this.wordSearchViewModel = new WordSearchViewModel();
        this.wordSearchViewModel.getCurrentSelectWord()
                .observe(this, wordSearchEntity -> {
                    if (currentTask != null && !currentTask.isDone()) currentTask.cancel(true);
                    currentFocusWordId = wordSearchEntity.wordId;
                    StaticFactory.getExecutorService().execute(() -> {
                        FunctionWordVO currentFocusWord = starFunctionHandler.getCurrentFocusWord();
                        wordAudioHandler.playWordAudio(currentFocusWord, this);
                        updateUIHandler.post(() -> {
                            if (currentFocusWord != null) visibleWordAllMessage(currentFocusWord);
                        });
                    });
                });
        // 中文结果显示的RecyclerView
        this.starList.setLayoutManager(new LinearLayoutManager(this));
        this.selectWordList.setLayoutManager(new LinearLayoutManager(this));
        StaticFactory.getExecutorService().submit(() -> {
            starFunctionHandler = new AbstractStarFunctionHandler(this) {
                @Override
                public Integer getCurrentFocusWordId() {
                    return currentFocusWordId;
                }
            };
            // 初始化单词列表的adapter
            this.selectWordListAdapter = new SelectWordListAdapter(this, wordSearchViewModel);
            this.loadMoreAdapter = new LoadMoreAdapter(this);
            ConcatAdapter concatAdapter = new ConcatAdapter(selectWordListAdapter, loadMoreAdapter);
            // 绑定ItemTouchHelper,实现收藏夹拖拽移动功能
            ItemTouchHelper starSimpleTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback());
            // 设置recycleView的各个adapter
            this.starListAdapter = new StarListAdapter(this, starFunctionHandler, starSimpleTouchHelper);
            this.starListAdapter.refreshConcatAdapter();
            this.starSimpleAdapter = new StarSimpleAdapter(this, starFunctionHandler);
            starSimpleAdapter.setStartDragListener(starSimpleTouchHelper::startDrag);
            updateUIHandler.post(() -> {
                this.starList.setAdapter(starListAdapter.getGlobalAdapter());
                this.selectWordList.setAdapter(concatAdapter);
                starSimpleTouchHelper.attachToRecyclerView(starList);
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
                if (!recyclerView.canScrollVertically(1) && !lastPage) {
                    // 优先执行搜索事件或者已有的分页查询事件
                    if (currentTask != null && !currentTask.isDone()) currentTask.cancel(true);
                    page++;
                    currentTask = scheduler.schedule(getQueryTask(), searchDelay, TimeUnit.MILLISECONDS);
                }
            }
        });
        // 不允许弹出选项卡
        EditText searchEditText = searchInput.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setCustomInsertionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
        searchEditText.setLongClickable(false);
        searchEditText.setOnLongClickListener(v -> true);
    }

    private void visibleWordAllMessage(FunctionWordVO functionWordVO) {
        // 设置主界面的单词全部信息
        Optional.ofNullable(functionWordVO.getValue().get(WordStructure.WORD_ORIGIN))
                .ifPresent(wordDTOS -> originWord.setText(wordDTOS));
        resultWebViewHandler.displayWordResult(functionWordVO);
        // 设置收藏夹信息
        starResultWebViewHandler.displayWordResult(functionWordVO);
        // 设置右侧展开列表单词的原文
        Optional.ofNullable(functionWordVO.getValue().get(WordStructure.WORD_ORIGIN))
                .ifPresent(wordDTOS -> starCurrentWord
                        .setText(wordDTOS));
        // 刷新相关UI
        resultArea.setVisibility(View.VISIBLE);
        selectWordList.setVisibility(View.GONE);
        textChange = false;
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
        this.starMove = findViewById(R.id.iv_recite_star_move);
        this.starList = findViewById(R.id.rc_recite_star_list);
        this.starCreateCategory = findViewById(R.id.tv_recite_star_create);

        this.controlPlay = findViewById(R.id.iv_search_word_control_play);
        this.searchInput = findViewById(R.id.sv_search_word_control);
        this.editOriginButton = findViewById(R.id.ll_search_word_edit_origin);

        this.searchInput.setOnClickListener(this);
        this.searchInput.setOnQueryTextListener(this);
        this.backToTrace.setOnClickListener(this);
        this.analysisWord.setOnClickListener(this);
        this.openStarDrawer.setOnClickListener(this);
        this.starMove.setOnClickListener(this);
        this.starCreateCategory.setOnClickListener(this);
        this.controlPlay.setOnClickListener(this);
        this.editOriginButton.setOnClickListener(this);
    }
}