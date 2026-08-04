package com.github.lorenj.wordtint.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.dao.WordOriginDao;
import com.github.lorenj.wordtint.database.entity.WordBookSectionWordIdEntity;
import com.github.lorenj.wordtint.database.entity.WordNoteEntity;
import com.github.lorenj.wordtint.database.entity.WordOriginEntity;
import com.github.lorenj.wordtint.database.entity.WordSearchEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.StarFunctionHandler;
import com.github.lorenj.wordtint.handler.WordOriginEditHandler;
import com.github.lorenj.wordtint.handler.impl.AbstractStarFunctionHandler;
import com.github.lorenj.wordtint.ui.adapter.morefeatures.segmentation.SegmentationHeaderAdapter;
import com.github.lorenj.wordtint.ui.adapter.morefeatures.segmentation.SegmentationListAdapter;
import com.github.lorenj.wordtint.ui.adapter.morefeatures.segmentation.TextSegmentationViewModel;
import com.github.lorenj.wordtint.ui.adapter.wordsearch.ResultWebViewHandler;
import com.github.lorenj.wordtint.handler.impl.WordOriginEditHandlerImpl;
import com.github.lorenj.wordtint.utils.WordUtils;
import com.google.android.material.appbar.AppBarLayout;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TextSegmentationActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton backButton;
    private RecyclerView segmentationList;
    /**
     * 适配器
     */
    private SegmentationHeaderAdapter segmentationHeaderAdapter;
    private SegmentationListAdapter segmentationListAdapter;
    private TextSegmentationViewModel textSegmentationViewModel;

    /**
     * 选择查词功能
     */
    private final Handler updateUIHandler = new Handler(Looper.getMainLooper());
    private Runnable selectionCheckRunnable;
    private String lastSelectedWord = "";
    /**
     * 所有单词列表
     */
    private List<WordSearchEntity> allWordSearchList;
    private final LevenshteinDistance distance = LevenshteinDistance.getDefaultInstance();
    /**
     * 单词编辑处理器
     */
    private WordOriginEditHandler wordOriginEditHandler;
    /**
     * 数据库
     */
    private WordOriginDao wordOriginDao;
    private APPDatabase appDatabase;
    /**
     * 单词收藏需要获取当前的单词id
     */
    private StarFunctionHandler starFunctionHandler = null;
    private Integer currentFocusWordId;
    /**
     * 单词搜索的布局
     */
    private RelativeLayout segmentationSearch;
    private LinearLayout editOrigin, editNote, addWord, deleteWord;
    private TextView wordOrigin;
    private WebView wordResult;
    private ResultWebViewHandler resultWebViewHandler;
    /**
     * 顶部栏
     */
    private AppBarLayout appBarLayout;
    private Toast globalToast;
    /**
     * 单词书籍TEXT_SEGMENT的id默认为90
     */
    public static int TEXT_SEGMENT_SECTION_ID = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_segmentation);
        // 边到边适配
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        bindView();
        initView();
        initGlobalSelectionListener();
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.ib_text_segmentation_back) {
            finish();
        }
        if (itemId == R.id.ll_text_segmentation_note) {
            final EditText inputEditText = new EditText(this);
            inputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            inputEditText.setMinLines(4);
            inputEditText.setMaxLines(4);
            inputEditText.setVerticalScrollBarEnabled(true);
            inputEditText.setMovementMethod(ScrollingMovementMethod.getInstance());
            inputEditText.setGravity(Gravity.TOP);
            inputEditText.setPadding(30, 30, 30, 30);

            FunctionWordVO currentWord = starFunctionHandler.getCurrentFocusWord();
            WordNoteEntity tempNoteEntity = currentWord.getWordNoteEntity();
            if (tempNoteEntity == null) {
                tempNoteEntity = new WordNoteEntity();
                tempNoteEntity.setWordId(currentWord.getWordId());
            }
            inputEditText.setText(tempNoteEntity.getWordNote());
            final WordNoteEntity wordNoteEntity = tempNoteEntity;
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.write_note))
                    .setView(inputEditText)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getText(R.string.save), (dialog, which) -> {
                        String noteContext = inputEditText.getText().toString();
                        wordNoteEntity.setWordNote(noteContext);
                        currentWord.setWordNoteEntity(wordNoteEntity);
                        updateUIHandler.post(() -> visibleWordAllMessage(currentWord));
                        StaticFactory.getExecutorService()
                                .execute(() -> appDatabase.wordNoteDao().upsert(wordNoteEntity));
                    })
                    .setNegativeButton(getResources().getText(R.string.cancel), (dialog, which) -> {
                    })
                    .show();
        }
        if (itemId == R.id.ll_text_segmentation_edit_origin) {
            StaticFactory.getExecutorService().execute(() -> {
                FunctionWordVO currentWord = starFunctionHandler.getCurrentFocusWord();
                updateUIHandler.post(() -> wordOriginEditHandler.edit(currentWord,
                        () -> updateUIHandler.post(() -> visibleWordAllMessage(currentWord))));
            });
        }
        if (itemId == R.id.ll_text_segmentation_add_word) {
            StaticFactory.getExecutorService().execute(() -> {
                WordOriginEntity wordOriginEntity = new WordOriginEntity();
                wordOriginEntity.wordId = Math.toIntExact(WordUtils.calculateWordId(lastSelectedWord));
                wordOriginEntity.key = WordStructure.WORD_ORIGIN.name();
                wordOriginEntity.value = lastSelectedWord;
                appDatabase.runInTransaction(() -> {
                    // 判断单词是否已经存在
                    List<WordOriginEntity> checkWordExist = appDatabase.wordOriginDao().findAllOriginWordById(wordOriginEntity.wordId);
                    if (checkWordExist != null &&
                            !checkWordExist.isEmpty()) {
                        updateUIHandler.post(() -> {
                            if (globalToast != null) globalToast.cancel();
                            globalToast = Toast.makeText(TextSegmentationActivity.this,
                                    this.getString(R.string.word_exist),
                                    Toast.LENGTH_SHORT);
                            globalToast.show();
                        });
                        return;
                    }
                    // 插入单词
                    appDatabase.wordOriginDao().insert(wordOriginEntity);
                    // 获取单词order
                    Integer order = appDatabase.wordBookSectionDao()
                            .getMaxOrderBySectionId(TEXT_SEGMENT_SECTION_ID)
                            .orElse(1);
                    WordBookSectionWordIdEntity wordBookSectionWordIdEntity = new WordBookSectionWordIdEntity();
                    wordBookSectionWordIdEntity.sectionId = TEXT_SEGMENT_SECTION_ID;
                    wordBookSectionWordIdEntity.wordId = Math.toIntExact(wordOriginEntity.wordId);
                    wordBookSectionWordIdEntity.order = order + 1;
                    appDatabase.wordBookSectionDao().insert(wordBookSectionWordIdEntity);
                    // 插入search表
                    WordSearchEntity wordSearchEntity = new WordSearchEntity();
                    wordSearchEntity.wordId = wordOriginEntity.wordId;
                    wordSearchEntity.wordOrigin = wordOriginEntity.value;
                    appDatabase.wordSearchDao().insert(wordSearchEntity);
                    allWordSearchList.add(wordSearchEntity);
                    updateUIHandler.post(() -> {
                        if (globalToast != null) globalToast.cancel();
                        globalToast = Toast.makeText(TextSegmentationActivity.this,
                                String.format(getString(R.string.add_success), lastSelectedWord),
                                Toast.LENGTH_SHORT);
                        globalToast.show();
                    });
                });
            });
        }
        if (itemId == R.id.ll_text_segmentation_delete_word) {
            StaticFactory.getExecutorService().execute(() -> appDatabase.runInTransaction(() -> {
                // 确保单词只存在于TEXT_SEGMENT_SECTION_ID中
                WordBookSectionWordIdEntity wordBookSectionWordIdEntity = appDatabase.wordBookSectionDao()
                        .findBySectionIdAndWordId(TEXT_SEGMENT_SECTION_ID, currentFocusWordId);
                if (wordBookSectionWordIdEntity == null) return;
                appDatabase.wordBookSectionDao().deleteBySectionIdAndWordId(TEXT_SEGMENT_SECTION_ID, currentFocusWordId);
                appDatabase.wordOriginDao().deleteAllByWordId(currentFocusWordId);
                appDatabase.wordSearchDao().deleteByWordId(currentFocusWordId);
                allWordSearchList = allWordSearchList.stream()
                        .filter(tmp -> tmp.wordId != currentFocusWordId)
                        .collect(Collectors.toList());
                updateUIHandler.post(() -> {
                    if (globalToast != null) globalToast.cancel();
                    globalToast = Toast.makeText(TextSegmentationActivity.this,
                            String.format(getString(R.string.delete_success), lastSelectedWord),
                            Toast.LENGTH_SHORT);
                    globalToast.show();
                });
            }));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getCurrentFocus() == null) return;
        if (getCurrentFocus().getId() != R.id.et_text_segmentation_input) {
            updateUIHandler.post(selectionCheckRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUIHandler.removeCallbacks(selectionCheckRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUIHandler.removeCallbacks(selectionCheckRunnable);
    }

    private void initGlobalSelectionListener() {
        selectionCheckRunnable = new Runnable() {
            @Override
            public void run() {
                View focusedView = getCurrentFocus();
                if (focusedView instanceof TextView tv && focusedView.getId() != R.id.et_text_segmentation_input) {
                    int start = tv.getSelectionStart();
                    int end = tv.getSelectionEnd();
                    if (start != end && start != -1 && end != -1) {
                        int min = Math.max(0, Math.min(start, end));
                        int max = Math.max(0, Math.max(start, end));
                        CharSequence text = tv.getText();
                        if (max <= text.length()) {
                            String currentWord = text.subSequence(min, max).toString().trim();
                            if (!currentWord.isEmpty() && !currentWord.equals(lastSelectedWord)) {
                                lastSelectedWord = currentWord;
                                // 调出查词面板
                                StaticFactory.getExecutorService().execute(() -> {
                                    WordSearchEntity bestMatch = allWordSearchList.stream()
                                            .min(Comparator.comparingInt(word -> distance.apply(currentWord, word.wordOrigin)))
                                            .get();
                                    currentFocusWordId = bestMatch.wordId;
                                    FunctionWordVO currentFocusWord = starFunctionHandler.getCurrentFocusWord();
                                    updateUIHandler.post(() -> {
                                        if (currentFocusWord != null)
                                            visibleWordAllMessage(currentFocusWord);
                                    });
                                });
                            }
                        }
                    }
                    if (start == end) {
                        lastSelectedWord = "";
                        segmentationSearch.setVisibility(View.GONE);
                    }
                }
                // 每 150 毫秒微调轮询一次,保障拖动时能有极高的响应速度
                updateUIHandler.postDelayed(this, 300);
            }
        };
        // 当页面布局渲染或焦点改变时,确保启动/恢复轮询器
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalFocusChangeListener(
                (oldFocus, newFocus) -> {
                    updateUIHandler.removeCallbacks(selectionCheckRunnable);
                    // 如果新焦点移出了输入框,进入了列表,开始高频捕获
                    if (newFocus.getId() != R.id.et_text_segmentation_input) {
                        updateUIHandler.post(selectionCheckRunnable);
                    }
                });
    }

    private void visibleWordAllMessage(FunctionWordVO functionWordVO) {
        segmentationSearch.setVisibility(View.VISIBLE);
        // 设置主界面的单词全部信息
        String wordOriginText = Optional.ofNullable(functionWordVO)
                .map(FunctionWordVO::getValue)
                .map(map -> map.get(WordStructure.WORD_ORIGIN))
                .map(wordOriginEntity -> wordOriginEntity.value)
                .orElse("");
        wordOrigin.setText(wordOriginText);
        resultWebViewHandler.displayWordResult(functionWordVO);
    }

    private void initView() {
        wordOriginEditHandler = new WordOriginEditHandlerImpl(this);
        textSegmentationViewModel = new ViewModelProvider(this).get(TextSegmentationViewModel.class);
        LinearLayoutManager recordListLayoutManager = new LinearLayoutManager(this);
        this.segmentationList.setLayoutManager(recordListLayoutManager);
        this.segmentationHeaderAdapter = new SegmentationHeaderAdapter(this, textSegmentationViewModel);
        this.segmentationListAdapter = new SegmentationListAdapter(this);
        ConcatAdapter concatAdapter = new ConcatAdapter(segmentationHeaderAdapter, segmentationListAdapter);
        this.segmentationList.setAdapter(concatAdapter);
        textSegmentationViewModel
                .getSegmentationList()
                .observe(this, list -> {
                    if (list == null || list.isEmpty()) return;
                    segmentationListAdapter.replaceAll(list);
                });
        backButton.setOnClickListener(this);
        // 查询所有单词
        this.appDatabase = APPDatabase.getInstance(TextSegmentationActivity.this);
        StaticFactory.getExecutorService().execute(() -> allWordSearchList =
                appDatabase.wordSearchDao().findAll());
        wordOriginDao = appDatabase.wordOriginDao();
        StaticFactory.getExecutorService().submit(() -> {
            starFunctionHandler = new AbstractStarFunctionHandler(this) {
                @Override
                public Integer getCurrentFocusWordId() {
                    return currentFocusWordId;
                }
            };
        });
        segmentationSearch.setVisibility(View.GONE);
    }

    private void bindView() {
        appBarLayout = findViewById(R.id.abl_text_segmentation);
        backButton = findViewById(R.id.ib_text_segmentation_back);
        segmentationList = findViewById(R.id.rv_segmentation_list);
        this.segmentationSearch = findViewById(R.id.ll_text_segmentation_search);
        this.wordOrigin = findViewById(R.id.tv_text_segmentation_origin);
        this.wordResult = findViewById(R.id.wv_text_segmentation_result);
        this.editNote = findViewById(R.id.ll_text_segmentation_note);
        this.editOrigin = findViewById(R.id.ll_text_segmentation_edit_origin);
        this.addWord = findViewById(R.id.ll_text_segmentation_add_word);
        this.deleteWord = findViewById(R.id.ll_text_segmentation_delete_word);
        this.editNote.setOnClickListener(this);
        this.editOrigin.setOnClickListener(this);
        this.addWord.setOnClickListener(this);
        this.deleteWord.setOnClickListener(this);
        this.resultWebViewHandler = new ResultWebViewHandler(this, wordResult);
    }


}
