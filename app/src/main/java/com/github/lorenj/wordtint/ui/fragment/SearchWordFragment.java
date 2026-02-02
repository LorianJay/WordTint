package com.github.lorenj.wordtint.ui.fragment;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.AnyLanguageWordProperties;
import com.github.lorenj.wordtint.context.pathsystem.document.WordContextPath;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.entity.dto.DataPage;
import com.github.lorenj.wordtint.entity.dto.SearchWordParam;
import com.github.lorenj.wordtint.entity.dto.WordCategoryParam;
import com.github.lorenj.wordtint.entity.dto.WordCategoryWordDTO;
import com.github.lorenj.wordtint.entity.dto.WordStructureDTO;
import com.github.lorenj.wordtint.entity.local.WordDTOLocal;
import com.github.lorenj.wordtint.enums.structure.EnglishStructure;
import com.github.lorenj.wordtint.handler.StarFunctionHandler;
import com.github.lorenj.wordtint.handler.impl.AbstractStarFunctionHandler;
import com.github.lorenj.wordtint.handler.impl.WordSearchHandlerImpl;
import com.github.lorenj.wordtint.ui.MainActivity;
import com.github.lorenj.wordtint.ui.adapter.SimpleItemTouchHelperCallback;
import com.github.lorenj.wordtint.ui.adapter.StarCategoryAdapter;
import com.github.lorenj.wordtint.ui.adapter.StarResultAdapter;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.github.lorenj.wordtint.ui.adapter.wordsearch.ResultWebViewHandler;
import com.github.lorenj.wordtint.ui.adapter.wordsearch.SelectWordListAdapter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author sukidayo
 * @date Thursday, February 09, 2023
 */
public class SearchWordFragment extends Fragment implements View.OnClickListener,
        KeyEvent.Callback,
        RecycleViewItemClickCallBack<WordDTOLocal>, SearchView.OnQueryTextListener {

    private View rootView;
    // 界面标题
    private TextView title;
    private ImageButton backToTrace;
    private AlertDialog loadingDialog = null;
    private WebView chineseAnswer;
    private RecyclerView chineseAnswerDrawer, starSingleCategory;
    // 新型中文显示处理器
    private ResultWebViewHandler resultWebViewHandler;
    // 收藏夹列表单词显示适配器
    private StarResultAdapter chineseAnswerAdapterDrawer;
    private StarCategoryAdapter starCategoryAdapter;
    private Handler updateUIHandler;
    private LinearLayout analysisWord, openStarDrawer;
    private TextView sourceWord, sourceWordDrawer;
    private TextView drawerPhraseHint, drawerPhraseAnswer, addNewCategory;
    // 收藏界抽屉布局
    private DrawerLayout startDrawer;
    private final StarFunctionHandler starFunctionHandler = new AbstractStarFunctionHandler(null) {
        @Override
        public Map<Integer, FunctionWordVO> getDict() {
            return null;
        }

        @Override
        public Integer getCurrentFocusWordId() {
            return null;
        }
    };
    // 对于本类来说,如果当前的格式是经典模式,则代表当前展示的功能是查词功能,否则当前是联想模式,不包含查词的功能
    private SearchView searchInput;
    // 当前查询单词的结构
    private List<WordStructureDTO> currentWordStructure;
    // 执行器
    private volatile Timer queryTimer;
    // 单词搜索列表
    private RecyclerView selectWordList;
    // 单词搜索列表的adapter
    private SelectWordListAdapter selectWordListAdapter;
    // 当前正在使用的单词搜索事件
    private SearchWordParam searchWordEvent = null;
    // 当前选中的单词
    private WordCategoryWordDTO currentViewWord = null;
    // 当前是否是最后一页,为了防止重复请求
    private volatile boolean lastList = false;
    // 判断当前是否有任务正在执行
    private volatile boolean isRunning = false;
    // 单词搜索handler
    private WordSearchHandlerImpl wordSearchHandler;
    // 单词音频播放器
    private final MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            initView();
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_search_word, container, false);
        bindView();
        initView();
        return rootView;
    }

    @Override
    public void onDestroy() {
        if (queryTimer != null) {
            queryTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.toolbar_back_to_trace) {
            Navigation.findNavController(getView()).popBackStack();
        } else if (itemId == R.id.fragment_search_word_click_analysis_word) {

        } else if (itemId == R.id.drawer_start_add_category) {
            View addNewCategory = getLayoutInflater().inflate(R.layout.dialog_recite_new_star, null);
            EditText categoryTile = addNewCategory.findViewById(R.id.et_new_star_title);
            EditText categoryDescribe = addNewCategory.findViewById(R.id.et_new_star_describe);
            new AlertDialog.Builder(getContext())
                    .setView(addNewCategory)
                    .setCancelable(true)
                    .setPositiveButton("确定", (dialog, which) -> {
                        WordCategoryParam wordCategoryParam = new WordCategoryParam();
                        wordCategoryParam.setTitle(categoryTile.getText().toString());
                        wordCategoryParam.setDescribeInfo(categoryDescribe.getText().toString());
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                    })
                    .show();
        } else if (itemId == R.id.fragment_search_word_search_view) {
            searchInput.setIconified(false);
            searchInput.requestFocus();
        } else if (itemId == R.id.fragment_search_word_click_star) {
            starSingleCategory.setVisibility(View.VISIBLE);
            startDrawer.openDrawer(GravityCompat.END);
        } else if (itemId == R.id.fragment_search_word_play_word) {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (startDrawer.isDrawerOpen(GravityCompat.END)) {
                startDrawer.closeDrawer(GravityCompat.END);
                return true;
            }
            Navigation.findNavController(getView()).popBackStack();
        }
        return true;
    }

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return onQueryTextChange(query);
    }

    /**
     * 搜索列表监听事件
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        // 监听用户的搜索
        if (!TextUtils.isEmpty(newText)) {
            // 保存本次查询的字符串
            // 查询单词
            SearchWordParam searchWordParam = new SearchWordParam();
            searchWordParam.setWord(newText);
            searchWordParam.setSize(20);
            searchWordParam.setCurrent(1);
            searchWordEvent = searchWordParam;
            if (queryTimer != null) {
                queryTimer.cancel();
                isRunning = false;
            }
            this.queryTimer = new Timer();
            isRunning = true;
            this.queryTimer.schedule(getQueryTask(), 1000);
        }
        return false;
    }

    private void initView() {
        this.title.setText(R.string.search_word);
        ((MainActivity) requireActivity()).setFragmentWindowSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        updateUIHandler = new Handler();
        startDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // 隐藏单词的附加显示内容、隐藏收藏界面的答案信息
        resultWebViewHandler = new ResultWebViewHandler(null,null);
        sourceWord.setText("");
        sourceWordDrawer.setText("");
        drawerPhraseHint.setVisibility(View.GONE);
        drawerPhraseAnswer.setVisibility(View.GONE);
        loadingDialog = new AlertDialog.Builder(getContext()).setView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null)).setCancelable(false).show();
        // 中文结果显示的RecyclerView需要使用FlexboxLayoutManager
        this.chineseAnswerDrawer.setLayoutManager(new LinearLayoutManager(getContext()));
        this.starSingleCategory.setLayoutManager(new LinearLayoutManager(getContext()));
        this.selectWordList.setLayoutManager(new LinearLayoutManager(getContext()));

        StaticFactory.getExecutorService().submit(() -> {
            Map<Long, WordDTOLocal> allWordDict = StaticFactory.getAllWordDict();
            wordSearchHandler = new WordSearchHandlerImpl(getContext(), allWordDict);
            this.starCategoryAdapter = new StarCategoryAdapter(getContext(),starFunctionHandler);
            // 初始化单词列表的adapter
            this.selectWordListAdapter = new SelectWordListAdapter(getContext());
            // 设置选中单词的回调事件
            this.selectWordListAdapter.setRecycleViewItemClickCallBack(this);
            // 绑定ItemTouchHelper,实现单个列表的编辑删除等功能
            ItemTouchHelper touchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(starCategoryAdapter));
            starCategoryAdapter.setStartDragListener(touchHelper::startDrag);
            // 设置收藏夹列表中中文意思显示的adapter
            this.chineseAnswerAdapterDrawer = new StarResultAdapter(getContext(), 2L);
            // 读取用户收藏夹信息
            //categoryFunctionHandler.batchCreateCategory(JsonUtils.readJsonArray(WordContextPath.WORD_STAR.getPath(), WordCategoryDetailVO.class));
            updateUIHandler.post(() -> {
                this.chineseAnswerDrawer.setAdapter(chineseAnswerAdapterDrawer);
                this.starSingleCategory.setAdapter(starCategoryAdapter);
                // 设置单词选择列表的adapter
                this.selectWordList.setAdapter(selectWordListAdapter);
                touchHelper.attachToRecyclerView(starSingleCategory);
                this.chineseAnswer.setVisibility(View.GONE);
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
                        searchWordEvent.setCurrent(searchWordEvent.getCurrent() + 1);
                        queryTimer = new Timer();
                        queryTimer.schedule(getQueryTask(), 1000);
                    }
                }
            }
        });

    }

    /**
     * 显示当前单词的所有信息,<b>具体当前要根据状态隐藏哪些信息有调用者来处理.</b><br>
     * 该方法展示的是最全面的信息,所有隐藏信息都会被显示,但是单词没有的性质(比如没有某个中文意思)那么没有的内容不会展示.
     */
    @SuppressLint("SetTextI18n")
    private void visibleWordAllMessage(WordDTOLocal wordDTOLocal) {
        // 隐藏单词选择列表,显示单词详情列表
        selectWordList.setVisibility(View.GONE);
        chineseAnswer.setVisibility(View.VISIBLE);
        sourceWord.setVisibility(View.VISIBLE);
        // 设置单词原文
        Optional.ofNullable(wordDTOLocal.getValue().get(EnglishStructure.WORD_ORIGIN))
                .ifPresent(wordDTOS -> sourceWord
                        .setText(wordDTOS));
        // 设置收藏夹单词显示
        Optional.ofNullable(wordDTOLocal.getValue().get(EnglishStructure.WORD_ORIGIN))
                .ifPresent(wordDTOS -> sourceWordDrawer
                        .setText(wordDTOS));
        //resultWebViewHandler.displayWordResult(wordDTOLocal);
        // 设置收藏夹中文翻译
        //chineseAnswerAdapterDrawer.addItem(wordDTOLocal);
        // 播放声音
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(new File(AnyLanguageWordProperties.getExternalFilesDir(),
                    WordContextPath.WORD_AUDIO.getPath() + wordDTOLocal.getAudioPath()).getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ignored) {
        }
        // todo 在这里设置recycleView的宽度
    }

    /**
     * 获得具体的查询函数
     */
    private TimerTask getQueryTask() {
        return new TimerTask() {
            @Override
            public void run() {
                StaticFactory.getExecutorService().submit(() -> {
                    DataPage<WordDTOLocal> page = wordSearchHandler.searchWord(searchWordEvent);
                    updateUIHandler.post(() -> {
                        // 显示单词搜索列表,隐藏单词详情列表
                        selectWordList.setVisibility(View.VISIBLE);
                        chineseAnswer.setVisibility(View.GONE);
                        sourceWord.setVisibility(View.GONE);
                        lastList = page.isLast();
                        if (page.isFirst()) {
                            // 如果是第一页就替换
                            selectWordListAdapter.replaceAllWithDataPage(page);
                        } else {
                            // 否则就添加所有数据到集合中
                            selectWordListAdapter.addAllWithDataPage(page);
                        }
                    });
                });
                isRunning = false;
            }
        };

    }

    private void bindView() {
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.title = rootView.findViewById(R.id.toolbar_title);
        this.sourceWord = rootView.findViewById(R.id.fragment_search_word_source_word);
        this.chineseAnswer = rootView.findViewById(R.id.fragment_word_credit_chinese_answer);
        this.analysisWord = rootView.findViewById(R.id.fragment_search_word_click_analysis_word);
        this.startDrawer = rootView.findViewById(R.id.fragment_search_word_start_drawer);
        this.openStarDrawer = rootView.findViewById(R.id.fragment_search_word_click_star);
        this.chineseAnswerDrawer = rootView.findViewById(R.id.drawer_star_chinese_answer_recycler_view);
        this.starSingleCategory = rootView.findViewById(R.id.start_category_recycler);
        this.sourceWordDrawer = rootView.findViewById(R.id.fragment_word_credit_drawer_word_origin);
        this.drawerPhraseHint = rootView.findViewById(R.id.drawer_star_phrase_hint);
        this.drawerPhraseAnswer = rootView.findViewById(R.id.drawer_star_phrase_answer);
        this.addNewCategory = rootView.findViewById(R.id.drawer_start_add_category);
        this.searchInput = rootView.findViewById(R.id.fragment_search_word_search_view);
        this.selectWordList = rootView.findViewById(R.id.fragment_search_word_select_recycler_view);
        // 播放按钮
        ImageButton playWord = rootView.findViewById(R.id.fragment_search_word_play_word);

        this.searchInput.setOnClickListener(this);
        this.searchInput.setOnQueryTextListener(this);
        this.backToTrace.setOnClickListener(this);
        this.analysisWord.setOnClickListener(this);
        this.openStarDrawer.setOnClickListener(this);
        this.addNewCategory.setOnClickListener(this);
        playWord.setOnClickListener(this);
    }

    // ----下面是一些用不到的方法----

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }


}