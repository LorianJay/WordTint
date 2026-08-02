package com.github.lorenj.wordtint.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.entity.WordNoteEntity;
import com.github.lorenj.wordtint.database.entity.WordOriginEntity;
import com.github.lorenj.wordtint.database.entity.WordStarEntity;
import com.github.lorenj.wordtint.database.entity.relation.WordStarWithWordIdEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.database.vo.UserRecitePreference;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.enums.ReciteMode;
import com.github.lorenj.wordtint.enums.ReciteOrigin;
import com.github.lorenj.wordtint.enums.RecitePreposition;
import com.github.lorenj.wordtint.enums.WordFunctionState;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.WordAudioHandler;
import com.github.lorenj.wordtint.handler.WordFunctionHandler;
import com.github.lorenj.wordtint.handler.impl.WordFunctionHandlerImpl;
import com.github.lorenj.wordtint.ui.MainActivity;
import com.github.lorenj.wordtint.ui.adapter.common.SimpleItemTouchHelperCallback;
import com.github.lorenj.wordtint.ui.adapter.customview.FlowingBorderView;
import com.github.lorenj.wordtint.ui.adapter.customview.HandwritingView;
import com.github.lorenj.wordtint.ui.adapter.markarea.ReciteMarkToastAdapter;
import com.github.lorenj.wordtint.ui.adapter.star.StarListAdapter;
import com.github.lorenj.wordtint.ui.adapter.star.StarSimpleAdapter;
import com.github.lorenj.wordtint.ui.adapter.wordsearch.ResultWebViewHandler;
import com.github.lorenj.wordtint.ui.fragment.BookListFragment;
import com.github.lorenj.wordtint.utils.AnimationUtil;
import com.github.lorenj.wordtint.handler.impl.WordOriginEditHandlerImpl;
import com.github.lorenj.wordtint.utils.MathUtils;
import com.google.android.material.color.MaterialColors;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MainReciteActivity extends AppCompatActivity implements View.OnClickListener,
        KeyEvent.Callback,
        View.OnTouchListener,
        View.OnLongClickListener,
        Window.Callback {

    private ImageView controlFunctionAreaFold;

    private boolean moreFunctionOpen = true;
    private WordFunctionHandler wordFunctionHandler;
    private final WordAudioHandler wordAudioHandler = StaticFactory.wordAudioHandler();
    private DrawerLayout starDrawer;
    private RecyclerView starList, reciteMarkArea;
    private ResultWebViewHandler resultWebViewHandler, starResultWebViewHandler;
    private StarListAdapter starListAdapter;
    private StarSimpleAdapter starSimpleAdapter;
    private ReciteMarkToastAdapter reciteMarkToastAdapter;

    /**
     * UI相关
     */
    private ImageView back, controlPlay;
    private HorizontalScrollView functionAreaHorizontalScrollView;
    private TextView originWord, controlNextWord, controlPreviousWord;
    private TextView currentIndex, wordCount, lightHint;
    private TextView starCurrentWord, starCreateCategory;
    private ImageView starMove;
    private AlertDialog loadingDialog = null;
    private LinearLayout functionGoto, functionMark, functionChameleon, functionSwitch, functionNote, functionEditOrigin, functionLock, functionBlueTooth;
    private LinearLayout functionShuffle, functionSection, functionMode, functionQuickPosition, functionStar, functionSearchWord, functionSaveProgress, functionAnalysis;
    private LinearLayout functionHandwriting;
    private LinearLayout lightArea;
    private CardView lightResult;
    private TableLayout functionChangeModePopLayout;
    private ImageView functionChameleonImageView, functionSwitchImageView, functionLockImageView;
    private ImageView functionBlueToothImageView, functionShuffleImageView, functionSectionImageView, functionQuickPositionImageView;
    private ImageView functionHandwritingImageView;
    private TextView functionBlueToothTextView, functionHandwritingTextView;
    private TextView windowListingWrite, windowEnglishChineseAudio, windowEnglishChinese;
    private TextView windowChineseEnglish, windowOnlyRecite, windowHidePhrase;
    private long exitLastTime = 0;
    private final Handler updateUIHandler = new Handler(Looper.getMainLooper());
    private FlowingBorderView functionChameleonBorder;
    private HandwritingView handwritingView;

    /**
     * 用户的背词风格
     */
    private UserRecitePreference userRecitePreference;

    /**
     * 滑动显示答案组件时的坐标<br>
     * 以及是否长按点击了显示答案按钮
     */
    private float switchMarkColorDY;
    /**
     * 切换的度
     */
    private final float switchMarkDegree = 62.5f;
    /**
     * 滑动改变答案按钮位置
     */
    private float lightChangeDY, lightChangeDX, lightParentLayoutY = 0, lightParentLayoutX = 0;
    /**
     * 常量
     */
    public static final String ANALYSIS_WORD = "ANALYSIS_WORD";

    /**
     * 马达
     */
    private Vibrator vibrator;

    /**
     * 蓝牙相关
     */
    private float blueToothDownX, blueToothDownY;
    /**
     * 数据库
     */
    private APPDatabase appDatabase;

    /**
     * 全局的对话框,用于弹出提示信息
     */
    private Toast globalToast;


    /**
     * 搜索单词页面的跳转返回
     */
    private final ActivityResultLauncher<Intent> searchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    CompletableFuture
                            .runAsync(wordFunctionHandler::reloadStar, StaticFactory.getExecutorService())
                            .thenRunAsync(new Runnable() {
                                @Override
                                public void run() {
                                    wordFunctionHandler.getWordFunctionHandlerState().setSortStar(true);
                                    starMove.performClick();
                                }
                            }, updateUIHandler::post);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recite);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsController controller;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars());
                // 即使滑屏也不要让它跳出来影响布局
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        // 注册返回事件回调
        getOnBackPressedDispatcher().addCallback(this, getOnBackPressedCallBack());
        bindView();
        initView();
    }

    private void initView() {
        loadingDialog = new AlertDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_loading, null))
                .setCancelable(false)
                .show();
        appDatabase = APPDatabase.getInstance(this);
        // 显示中文的处理器
        this.resultWebViewHandler = new ResultWebViewHandler(this, findViewById(R.id.wv_recite_result));
        this.starResultWebViewHandler = new ResultWebViewHandler(this, findViewById(R.id.wv_star_recite_result));
        // 锁定startDrawable的关闭
        starDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // 必须在这里设置LayoutManager
        this.starList.setLayoutManager(new LinearLayoutManager(this));
        this.reciteMarkArea.setLayoutManager(new LinearLayoutManager(this));
        // 读取状态
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        // 读取所有单词信息
        StaticFactory.getExecutorService().execute(() -> {
            // 根据自定义风格背诵,获取当前要加载的所有单词
            this.userRecitePreference = (UserRecitePreference) bundle.getSerializable(WordReciteLaunchActivity.USER_RECITE_PREFERENCE);
            // 删除bundle内容
            bundle.remove(BookListFragment.SELECT_SECTION_LIST);
            bundle.remove(BookListFragment.REVIEW_WORD_List);
            // 所有的功能均交由Handler来处理,UI必须与业务逻辑代码脱离
            this.wordFunctionHandler = new WordFunctionHandlerImpl(MainReciteActivity.this, userRecitePreference);
            // 绑定ItemTouchHelper,实现收藏夹拖拽移动功能
            ItemTouchHelper starSimpleTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback());
            // 设置recycleView的各个adapter
            this.starListAdapter = new StarListAdapter(this, wordFunctionHandler, starSimpleTouchHelper);
            this.starListAdapter.refreshConcatAdapter();
            this.starSimpleAdapter = new StarSimpleAdapter(this, wordFunctionHandler);
            this.reciteMarkToastAdapter = new ReciteMarkToastAdapter(this, wordFunctionHandler);
            this.reciteMarkArea.setItemAnimator(null);
            starSimpleAdapter.setStartDragListener(starSimpleTouchHelper::startDrag);
            // 更新UI
            updateUIHandler.post(() -> {
                this.starList.setAdapter(starListAdapter.getGlobalAdapter());
                this.reciteMarkArea.setAdapter(reciteMarkToastAdapter);
                starSimpleTouchHelper.attachToRecyclerView(starList);
                if (userRecitePreference.getReciteOrigin() == ReciteOrigin.RECITE_RECORD)
                    wordFunctionHandler.getWordFunctionHandlerState().setChameleon(MarkColor.BROWN);
                // view-model的监听事件,必须在UI线程
                wordFunctionHandler.getWordFunctionHandlerState()
                        .getChameleon()
                        .observe(this, markColor -> {
                            refreshChameleonUI();
                            reciteWord(wordFunctionHandler.getCurrentFocusWord());
                        });
                reciteWord(wordFunctionHandler.getCurrentFocusWord());
                updateChangeModePopWindowState();
                loadingDialog.dismiss();
            });
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean enableBlueTooth = wordFunctionHandler.getWordFunctionHandlerState().isEnableBlueTooth();
        // 屏蔽蓝牙未开启的点击
        if (event.getSource() == 769 && !enableBlueTooth) {
            return true;
        }
        if (enableBlueTooth && event.getSource() == 769
                && event.getAction() == KeyEvent.ACTION_DOWN
                && (keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            lightResult.performClick();
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 屏蔽蓝牙未开启的点击
        boolean enableBlueTooth = wordFunctionHandler.getWordFunctionHandlerState().isEnableBlueTooth();
        if (event.getSource() != InputDevice.SOURCE_MOUSE || !enableBlueTooth) {
            return super.dispatchTouchEvent(event);
        }
        if (event.getSource() == InputDevice.SOURCE_MOUSE) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    blueToothDownX = event.getRawX();
                    blueToothDownY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    float distinctX = event.getRawX() - blueToothDownX;
                    float distinctY = event.getRawY() - blueToothDownY;
                    if (distinctX == 0 && distinctY == 0) {
                        if (!wordFunctionHandler.getWordFunctionHandlerState().isFunctionAreaFold()) {
                            // 如果当前正展开了旗帜-则选中当前单词
                            FunctionWordVO currentFocusWord = wordFunctionHandler.getCurrentFocusWord();
                            int currentPosition = wordFunctionHandler.getWordFunctionHandlerState().getCurrentFocusBlueToothPosition();
                            MarkColor markColor = MarkColor.values()[currentPosition];
                            if (currentFocusWord.getMarkColorList().contains(markColor)) {
                                currentFocusWord.getMarkColorList().remove(markColor);
                            } else {
                                currentFocusWord.getMarkColorList().add(markColor);
                            }
                            wordFunctionHandler.getWordFunctionHandlerState().setFunctionAreaFold(true);
                            refreshMarkAreaUI();
                        } else {
                            controlPlay.performClick();
                        }
                    } else if (distinctX == 0 && distinctY > 0) {
                        if (wordFunctionHandler.getWordFunctionHandlerState().isFunctionAreaFold()) {
                            wordFunctionHandler.getWordFunctionHandlerState()
                                    .setFunctionAreaFold(!wordFunctionHandler.getWordFunctionHandlerState().isFunctionAreaFold());
                            refreshMarkAreaUI();
                        } else {
                            int currentPosition = wordFunctionHandler.getWordFunctionHandlerState().getCurrentFocusBlueToothPosition() - 1;
                            wordFunctionHandler.getWordFunctionHandlerState().setCurrentFocusBlueToothPosition(currentPosition);
                        }
                    } else if (distinctX == 0 && distinctY < 0) {
                        if (wordFunctionHandler.getWordFunctionHandlerState().isFunctionAreaFold()) {
                            wordFunctionHandler.getWordFunctionHandlerState()
                                    .setFunctionAreaFold(!wordFunctionHandler.getWordFunctionHandlerState().isFunctionAreaFold());
                            refreshMarkAreaUI();
                        } else {
                            int currentPosition = wordFunctionHandler.getWordFunctionHandlerState().getCurrentFocusBlueToothPosition() + 1;
                            wordFunctionHandler.getWordFunctionHandlerState().setCurrentFocusBlueToothPosition(currentPosition);
                        }
                    } else if (distinctY == 0 && distinctX > 0) {
                        controlPreviousWord.performClick();
                    } else if (distinctY == 0 && distinctX < 0) {
                        controlNextWord.performClick();
                    }
                    // 如果当前展开了旗帜
                    if (!wordFunctionHandler.getWordFunctionHandlerState().isFunctionAreaFold()) {
                        int currentPosition = wordFunctionHandler.getWordFunctionHandlerState().getCurrentFocusBlueToothPosition();
                        int previousPosition = wordFunctionHandler.getWordFunctionHandlerState().getPreviousFocusBlueToothPosition();
                        if (currentPosition < 0) currentPosition = 0;
                        if (currentPosition > 9) currentPosition = 9;
                        wordFunctionHandler.getWordFunctionHandlerState().setCurrentFocusBlueToothPosition(currentPosition);
                        wordFunctionHandler.getWordFunctionHandlerState().setPreviousFocusBlueToothPosition(currentPosition);
                        int finalCurrentPosition = currentPosition;
                        updateUIHandler.post(() -> {
                            reciteMarkToastAdapter.notifyItemChanged(previousPosition, ReciteMarkToastAdapter.Item.SWITCH_DESELECT);
                            reciteMarkToastAdapter.notifyItemChanged(finalCurrentPosition, ReciteMarkToastAdapter.Item.SWITCH_SELECT);
                        });
                    }
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUIHandler.post(() -> {
            this.lightParentLayoutY = lightArea.getY();
            this.lightParentLayoutX = lightArea.getX();
        });
    }

    @Override
    public void onClick(View v) {
        int clickViewId = v.getId();
        // 控制区域
        if (clickViewId == R.id.ib_main_recite_back) {
            new AlertDialog.Builder(this)
                    .setMessage(getResources().getText(R.string.back_home_inquire))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getText(R.string.confirm), (dialog, which) -> {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    })
                    .show();
        }
        if (clickViewId == R.id.iv_recite_control_fold) {
            if (moreFunctionOpen &&
                    AnimationUtil.getInstance().moveToViewBottom(functionAreaHorizontalScrollView, 450)) {
                controlFunctionAreaFold.setImageResource(R.drawable.ic_fold);
                moreFunctionOpen = !moreFunctionOpen;
            } else if (!moreFunctionOpen &&
                    AnimationUtil.getInstance().bottomMoveToViewLocation(functionAreaHorizontalScrollView, 450)) {
                moreFunctionOpen = !moreFunctionOpen;
                controlFunctionAreaFold.setImageResource(R.drawable.ic_unfold);
            }
        }
        if (clickViewId == R.id.tv_recite_control_next) {
            reciteWord(wordFunctionHandler.gotoNextWord());
        }
        if (clickViewId == R.id.tv_recite_control_previous) {
            reciteWord(wordFunctionHandler.gotoPreviousWord());
        }
        if (clickViewId == R.id.iv_recite_control_play) {
            reciteWord(wordFunctionHandler.getCurrentFocusWord());
            // 如果当前是听音频模式,需要手动播放音频
            if (wordFunctionHandler.getWordFunctionHandlerState().getCurrentReciteMode()
                    == ReciteMode.ENGLISH_TRANSLATION_CHINESE_NO_HEARING
                    || wordFunctionHandler.getWordFunctionHandlerState().getCurrentReciteMode()
                    == ReciteMode.CHINESE_TRANSLATION_ENGLISH) {
                wordAudioHandler.playWordAudio(wordFunctionHandler.getCurrentFocusWord(), this);
            }
        }
        if (clickViewId == R.id.cv_main_recite_light) {
            // 是否要隐藏介词的处理
            FunctionWordVO currentFocusWord = wordFunctionHandler.getCurrentFocusWord();
            WordOriginEntity phraseVO = currentFocusWord.getValue().get(WordStructure.PHRASE);
            if (wordFunctionHandler.getWordFunctionHandlerState().getRecitePreposition() == RecitePreposition.INVISIBLE)
                currentFocusWord.getValue().remove(WordStructure.PHRASE);
            visibleWordAllMessage(currentFocusWord);
            if (wordFunctionHandler.getWordFunctionHandlerState().getRecitePreposition() == RecitePreposition.INVISIBLE && phraseVO != null)
                currentFocusWord.getValue().put(WordStructure.PHRASE, phraseVO);
        }
        // 功能区域
        if (clickViewId == R.id.ll_recite_function_mark) {
            wordFunctionHandler.getWordFunctionHandlerState()
                    .setFunctionAreaFold(!wordFunctionHandler.getWordFunctionHandlerState().isFunctionAreaFold());
            refreshMarkAreaUI();
        }
        if (clickViewId == R.id.ll_recite_function_chameleon) {
            // 如果当前处于按色打乱模式则无法使用变色龙功能,使用区间重背功能可以使用变色龙功能
            if (wordFunctionHandler.getWordFunctionHandlerState().getWordFunctionState()
                    == WordFunctionState.SHUFFLE) {
                globalToast = Toast.makeText(this, wordFunctionHandler.getWordFunctionHandlerState()
                        .getWordFunctionState()
                        .getInfo(), Toast.LENGTH_SHORT);
                globalToast.setGravity(Gravity.CENTER, 0, 500);
                globalToast.show();
                return;
            }
            wordFunctionHandler.getWordFunctionHandlerState().setSelectChameleon(!wordFunctionHandler.getWordFunctionHandlerState().isSelectChameleon());
            wordFunctionHandler.getWordFunctionHandlerState().setFunctionAreaFold(!wordFunctionHandler.getWordFunctionHandlerState().isSelectChameleon());
            refreshMarkAreaUI();
            refreshChameleonUI();
        }
        if (clickViewId == R.id.ll_recite_function_switch) {
            wordFunctionHandler.getWordFunctionHandlerState().setEnableSwitch(!wordFunctionHandler.getWordFunctionHandlerState().isEnableSwitch());
            if (wordFunctionHandler.getWordFunctionHandlerState().isEnableSwitch()) {
                this.functionSwitchImageView.getDrawable().setTint(getResources().getColor(R.color.gold, null));
            } else {
                this.functionSwitchImageView.getDrawable().setTintList(null);
            }
        }
        if (clickViewId == R.id.ll_recite_function_note) {
            final EditText inputEditText = new EditText(this);
            inputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            inputEditText.setMinLines(4);
            inputEditText.setMaxLines(4);
            inputEditText.setVerticalScrollBarEnabled(true);
            inputEditText.setMovementMethod(ScrollingMovementMethod.getInstance());
            inputEditText.setGravity(Gravity.TOP);
            inputEditText.setPadding(30, 30, 30, 30);

            FunctionWordVO currentWord = wordFunctionHandler.getCurrentFocusWord();
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
                        StaticFactory.getExecutorService()
                                .execute(() -> appDatabase.wordNoteDao().upsert(wordNoteEntity));
                    })
                    .setNegativeButton(getResources().getText(R.string.cancel), (dialog, which) -> {
                    })
                    .show();
        }
        if (clickViewId == R.id.ll_recite_function_edit_origin) {
            FunctionWordVO currentWord = wordFunctionHandler.getCurrentFocusWord();
            wordFunctionHandler.getWordOriginEditHandler().edit(currentWord,
                    () -> updateUIHandler.post(() -> visibleWordAllMessage(currentWord)));
        }

        if (clickViewId == R.id.ll_recite_function_lock) {
            wordFunctionHandler.getWordFunctionHandlerState().setLockLight(!wordFunctionHandler.getWordFunctionHandlerState().isLockLight());
            if (wordFunctionHandler.getWordFunctionHandlerState().isLockLight()) {
                this.functionLockImageView.getDrawable().setTint(getResources().getColor(android.R.color.holo_red_dark, null));
            } else {
                // 还原light的原始位置
                lightArea.setX(lightParentLayoutX);
                lightArea.setY(lightParentLayoutY);
                functionLockImageView.getDrawable().setTint(MaterialColors.getColor(this,
                        com.google.android.material.R.attr.colorOnSurface,
                        Color.BLACK));
            }
        }
        if (clickViewId == R.id.ll_recite_function_blue_tooth) {
            wordFunctionHandler.getWordFunctionHandlerState().setEnableBlueTooth(!wordFunctionHandler.getWordFunctionHandlerState().isEnableBlueTooth());
            if (wordFunctionHandler.getWordFunctionHandlerState().isEnableBlueTooth()) {
                this.functionBlueToothImageView.getDrawable().setTint(getResources().getColor(android.R.color.holo_blue_dark, null));
                this.functionBlueToothTextView.setText(getText(R.string.close_blue_tooth));
            } else {
                this.functionBlueToothImageView.getDrawable().setTint(MaterialColors.getColor(
                        this,
                        com.google.android.material.R.attr.colorOnSurface,
                        Color.BLACK));
                this.functionBlueToothTextView.setText(getText(R.string.open_blue_tooth));
            }
        }
        if (clickViewId == R.id.ll_recite_function_handwriting) {
            wordFunctionHandler.getWordFunctionHandlerState()
                    .setEnableHandwriting(!wordFunctionHandler.getWordFunctionHandlerState().isEnableHandwriting());
            if (wordFunctionHandler.getWordFunctionHandlerState().isEnableHandwriting()) {
                this.functionHandwritingImageView.getDrawable().setTint(getResources().getColor(R.color.theme_color, null));
                this.functionHandwritingTextView.setText(getText(R.string.close_handwriting));
                this.handwritingView.setHandwritingEnabled(true);
            } else {
                this.functionHandwritingImageView.getDrawable().setTint(MaterialColors.getColor(
                        this,
                        com.google.android.material.R.attr.colorOnSurface,
                        Color.BLACK));
                this.functionHandwritingTextView.setText(getText(R.string.open_handwriting));
                this.handwritingView.setHandwritingEnabled(false);
            }
        }
        if (clickViewId == R.id.ll_recite_function_goto) {
            final EditText inputEditText = new EditText(this);
            inputEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.goto_word))
                    .setMessage(getString(R.string.goto_word_message, wordFunctionHandler.getChameleonSize()))
                    .setView(inputEditText)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                        String value = inputEditText.getText().toString();
                        int i = 0;
                        if (MathUtils.isInt(value)) {
                            i = Integer.parseInt(value);
                        }
                        if (i <= 0
                                || i > wordFunctionHandler.getChameleonSize()
                                || !MathUtils.isInt(value)) {
                            if (globalToast != null) globalToast.cancel();
                            globalToast = Toast.makeText(this,
                                    getString(R.string.section_error, wordFunctionHandler.getChameleonSize()),
                                    Toast.LENGTH_SHORT);
                            globalToast.setGravity(Gravity.CENTER, 0, 500);
                            globalToast.show();
                            return;
                        }
                        final int index = i;
                        // 异步跳转单词,可能查找时间较长
                        StaticFactory.getExecutorService().submit(() ->
                                reciteWord(wordFunctionHandler.gotoWordWithIndex(index - 1)));
                    }).setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    }).show();
        }

        if (clickViewId == R.id.ll_recite_function_shuffle) {
            // 如果当前不是普通状态和按色打乱状态,代表当前在执行别的状态,需要先锁定按色打乱的功能
            if (wordFunctionHandler.getWordFunctionHandlerState().getWordFunctionState()
                    == WordFunctionState.RANGE) {
                if (globalToast != null) globalToast.cancel();
                globalToast = Toast.makeText(
                        this,
                        wordFunctionHandler.getWordFunctionHandlerState().getWordFunctionState().getInfo(),
                        Toast.LENGTH_LONG);
                globalToast.setGravity(Gravity.CENTER, 0, 500);
                globalToast.show();
                return;
            }
            if (wordFunctionHandler.getWordFunctionHandlerState().getWordFunctionState()
                    == WordFunctionState.NONE) {
                this.functionChameleonImageView.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_prohibit_foreground, null));
                this.functionSectionImageView.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_prohibit_foreground, null));
                this.functionQuickPositionImageView.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_prohibit_foreground, null));
                MarkColor markColor = Optional.ofNullable(wordFunctionHandler.getWordFunctionHandlerState()
                                .getChameleon()
                                .getValue())
                        .orElse(MarkColor.GREEN);
                this.functionShuffleImageView.getDrawable().setTint(getResources().getColor(markColor.getMapColorID(), null));
                wordFunctionHandler.shuffle();
                reciteWord(wordFunctionHandler.gotoWordWithIndex(0));
            } else {
                this.functionChameleonImageView.setForeground(null);
                this.functionSectionImageView.setForeground(null);
                this.functionQuickPositionImageView.setForeground(null);
                this.functionShuffleImageView.getDrawable().setTint(MaterialColors.getColor(
                        this,
                        com.google.android.material.R.attr.colorOnSurface,
                        Color.BLACK));
                wordFunctionHandler.restoreWordList();
                reciteWord(wordFunctionHandler.getCurrentFocusWord());
            }
        }
        // 区间随机
        if (clickViewId == R.id.ll_recite_function_section) {
            if (wordFunctionHandler.getWordFunctionHandlerState().getWordFunctionState()
                    == WordFunctionState.SHUFFLE) {
                if (globalToast != null) globalToast.cancel();
                globalToast = Toast.makeText(this, wordFunctionHandler.getWordFunctionHandlerState()
                        .getWordFunctionState()
                        .getInfo(), Toast.LENGTH_LONG);
                globalToast.setGravity(Gravity.CENTER, 0, 500);
                globalToast.show();
                return;
            }
            if (wordFunctionHandler.getWordFunctionHandlerState().getWordFunctionState()
                    == WordFunctionState.NONE) {
                View rangeRandomWordInputView = getLayoutInflater().inflate(R.layout.dialog_word_recite_section, null);
                EditText minValue = rangeRandomWordInputView.findViewById(R.id.fragment_word_credit_dialog_section_min_value);
                EditText maxValue = rangeRandomWordInputView.findViewById(R.id.fragment_word_credit_dialog_section_max_value);
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.section_random))
                        .setMessage(getString(R.string.section_random_message, wordFunctionHandler.getChameleonSize()))
                        .setView(rangeRandomWordInputView)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                            int minRange = 0, maxRange = 0;
                            if (MathUtils.isInt(minValue.getText().toString()) &&
                                    MathUtils.isInt(maxValue.getText().toString())) {
                                minRange = Integer.parseInt(minValue.getText().toString()) - 1;
                                maxRange = Integer.parseInt(maxValue.getText().toString()) - 1;
                            }
                            if (!MathUtils.isInt(minValue.getText().toString())
                                    || !MathUtils.isInt(maxValue.getText().toString())
                                    || minRange < 0
                                    || maxRange > wordFunctionHandler.getChameleonSize()
                                    || minRange > maxRange) {
                                Toast errorToast = Toast.makeText(this, getString(R.string.section_error, wordFunctionHandler.getChameleonSize()), Toast.LENGTH_LONG);
                                errorToast.setGravity(Gravity.CENTER, 0, 500);
                                errorToast.show();
                                return;
                            }
                            // 确定执行区间随机时执行的内容
                            wordFunctionHandler.shuffleRange(minRange, maxRange);
                            reciteWord(wordFunctionHandler.gotoWordWithIndex(wordFunctionHandler.getCurrentFocusWordId()));
                            this.functionShuffleImageView.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_prohibit_foreground, null));
                            this.functionQuickPositionImageView.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_prohibit_foreground, null));
                            this.functionSectionImageView.getDrawable().setTint(ResourcesCompat.getColor(getResources(), R.color.theme_color, null));
                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                        })
                        .show();
            } else {
                this.functionShuffleImageView.setForeground(null);
                this.functionQuickPositionImageView.setForeground(null);
                this.functionSectionImageView.getDrawable().setTint(MaterialColors.getColor(
                        this,
                        com.google.android.material.R.attr.colorOnSurface,
                        Color.BLACK));
                this.wordFunctionHandler.restoreWordList();
                reciteWord(wordFunctionHandler.getCurrentFocusWord());
            }
        }
        // 保存当前的进度
        if (clickViewId == R.id.ll_recite_function_saving) {
            StaticFactory.getExecutorService().submit(() -> {
                wordFunctionHandler.saveProgress();
                updateUIHandler.post(() -> {
                    if (globalToast != null) globalToast.cancel();
                    globalToast = Toast.makeText(MainReciteActivity.this, R.string.save_success, Toast.LENGTH_LONG);
                    globalToast.setGravity(Gravity.CENTER, 0, 500);
                    globalToast.show();
                });
            });
        }

        // 快速定位
        if (clickViewId == R.id.ll_recite_function_position) {
            // 如果不是普通模式,则禁止使用快速定位功能
            if (wordFunctionHandler.getWordFunctionHandlerState().getWordFunctionState()
                    != WordFunctionState.NONE) {
                if (globalToast != null) globalToast.cancel();
                globalToast = Toast.makeText(
                        this,
                        wordFunctionHandler.getWordFunctionHandlerState().getWordFunctionState().getInfo(),
                        Toast.LENGTH_LONG);
                globalToast.setGravity(Gravity.CENTER, 0, 500);
                globalToast.show();
                return;
            }
            final EditText inputEditText = new EditText(this);
            inputEditText.setKeyListener(DigitsKeyListener.getInstance("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ "));
            inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.word_position))
                    .setMessage(getString(R.string.quick_position_message))
                    .setView(inputEditText)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getText(R.string.confirm), (dialog, which) -> {
                        String origin = inputEditText.getText().toString().trim().toLowerCase();
                        int index = wordFunctionHandler.getIndexByWordOrigin(origin);
                        // 单词未找到,则不跳转
                        if (index == -1) {
                            if (globalToast != null) globalToast.cancel();
                            globalToast = Toast.makeText(this, getString(R.string.word_position_error), Toast.LENGTH_SHORT);
                            globalToast.setGravity(Gravity.CENTER, 0, 500);
                            globalToast.show();
                            return;
                        }
                        // 异步跳转单词,可能查找时间较长
                        StaticFactory.getExecutorService().submit(() ->
                                reciteWord(wordFunctionHandler.forceGotoWordWithOutMarkColor(index)));
                    }).setNegativeButton(getResources().getText(R.string.cancel), (dialog, which) -> {
                    }).show();
        }
        // 单词搜索
        if (clickViewId == R.id.ll_recite_function_search) {
            searchLauncher.launch(new Intent(MainReciteActivity.this, SearchWordActivity.class));
        }
        // 单词分析 - 跳转到主页分析 Tab
        //if (clickViewId == R.id.ll_recite_function_analysis) {
        //    Intent intent = new Intent(this, MainActivity.class);
        //    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //    intent.putExtra(MainActivity.EXTRA_SHOW_ANALYSIS, true);
        //    startActivity(intent);
        //}
        // 收藏夹区域
        if (clickViewId == R.id.ll_recite_function_star) {
            starDrawer.openDrawer(GravityCompat.END);
        }
        if (clickViewId == R.id.tv_recite_star_create) {
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
                            WordStarWithWordIdEntity newStar = wordFunctionHandler.createNewStar(wordStarEntity);
                            updateUIHandler.post(() -> starListAdapter.addItem(newStar));
                        });
                    })
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    })
                    .show();
        }
        if (clickViewId == R.id.iv_recite_star_move) {
            boolean sortStar = wordFunctionHandler.getWordFunctionHandlerState().isSortStar();
            wordFunctionHandler.getWordFunctionHandlerState().setSortStar(!sortStar);
            sortStar = wordFunctionHandler.getWordFunctionHandlerState().isSortStar();
            if (sortStar) {
                this.starMove.getDrawable().setTint(getResources().getColor(R.color.theme_color, null));
                this.starList.setAdapter(starSimpleAdapter);
            } else {
                this.starMove.getDrawable().setTint(getResources().getColor(R.color.dark_gray, null));
                this.starListAdapter.refreshConcatAdapter();
                this.starList.setAdapter(starListAdapter.getGlobalAdapter());
            }
        }
        // 模式改变
        if (clickViewId == R.id.ll_recite_function_mode) {
            PopupWindow changeModePopupWindow = new PopupWindow(functionChangeModePopLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            changeModePopupWindow.setOutsideTouchable(true);
            changeModePopupWindow.setFocusable(true);
            changeModePopupWindow.setAnimationStyle(R.style.pop_window_anim_style);
            //changeModePopupWindow.setOnDismissListener(() -> ((ViewGroup) getParent()).removeView(popWindowChangeModeLayout));
            // PopWindow展示在某个组件的上方,这里的changeMode代表要展示在那个组件上方,popWindowChangeModeLayout代表要展示哪个组件.
            int[] location = new int[2];
            functionMode.getLocationOnScreen(location);
            functionChangeModePopLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupHeight = functionChangeModePopLayout.getMeasuredHeight();
            changeModePopupWindow.showAtLocation(
                    functionMode,
                    Gravity.NO_GRAVITY,
                    location[0],
                    location[1] - popupHeight);
        }
        if (clickViewId == R.id.window_mode_listening_write) {
            wordFunctionHandler.getWordFunctionHandlerState().setCurrentReciteMode(ReciteMode.LISTENING);
            updateChangeModePopWindowState();
        } else if (clickViewId == R.id.window_mode_english_chinese_audio) {
            wordFunctionHandler.getWordFunctionHandlerState().setCurrentReciteMode(ReciteMode.ENGLISH_TRANSLATION_CHINESE_HEARING);
            updateChangeModePopWindowState();
        } else if (clickViewId == R.id.window_mode_english_chinese) {
            wordFunctionHandler.getWordFunctionHandlerState().setCurrentReciteMode(ReciteMode.ENGLISH_TRANSLATION_CHINESE_NO_HEARING);
            updateChangeModePopWindowState();
        } else if (clickViewId == R.id.window_mode_chinese_english) {
            wordFunctionHandler.getWordFunctionHandlerState().setCurrentReciteMode(ReciteMode.CHINESE_TRANSLATION_ENGLISH);
            updateChangeModePopWindowState();
        } else if (clickViewId == R.id.window_mode_only_recite) {
            wordFunctionHandler.getWordFunctionHandlerState().setCurrentReciteMode(ReciteMode.ONLY_RECITE);
            updateChangeModePopWindowState();
        } else if (clickViewId == R.id.window_mode_hide_phrase) {
            if (wordFunctionHandler.getWordFunctionHandlerState().getRecitePreposition() == RecitePreposition.VISIBLE) {
                wordFunctionHandler.getWordFunctionHandlerState().setRecitePreposition(RecitePreposition.INVISIBLE);
            } else if (wordFunctionHandler.getWordFunctionHandlerState().getRecitePreposition() == RecitePreposition.INVISIBLE) {
                wordFunctionHandler.getWordFunctionHandlerState().setRecitePreposition(RecitePreposition.VISIBLE);
            }
            updateChangeModePopWindowState();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int itemId = v.getId();
        if (itemId == R.id.cv_main_recite_light &&
                !wordFunctionHandler.getWordFunctionHandlerState().isLockLight()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 获得按下时的相对位置
                lightChangeDY = lightArea.getY() - event.getRawY();
                lightChangeDX = lightArea.getX() - event.getRawX();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float newY = event.getRawY() + lightChangeDY;
                float newX = event.getRawX() + lightChangeDX;
                lightArea.setX(newX);
                lightArea.setY(newY);
            }
            // 如果当前的状态不锁定light,则改变light位置的优先级要高于滑动切换,应直接返回
            return false;
        }
        if (itemId == R.id.cv_main_recite_light &&
                wordFunctionHandler.getWordFunctionHandlerState().isEnableSwitch()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                switchMarkColorDY = event.getY();
                wordFunctionHandler.getWordFunctionHandlerState().setSelectChameleon(false);
                refreshChameleonUI();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE &&
                    wordFunctionHandler.getWordFunctionHandlerState().isSwitching()) {
                float distinct = event.getY() - switchMarkColorDY;
                if (Math.abs(distinct) < 10) return false;
                if (wordFunctionHandler.getWordFunctionHandlerState().isFunctionAreaFold()) {
                    wordFunctionHandler.getWordFunctionHandlerState().setFunctionAreaFold(false);
                    refreshMarkAreaUI();
                }
                int previousFocusSwitchPosition = wordFunctionHandler.getWordFunctionHandlerState().getPreviousFocusSwitchPosition();
                int currentFocusSwitchPosition = wordFunctionHandler.getWordFunctionHandlerState().getCurrentFocusSwitchPosition();
                int currentSelectSwitchPosition = (int) (distinct / switchMarkDegree) +
                        previousFocusSwitchPosition;
                if (currentSelectSwitchPosition < 0) currentSelectSwitchPosition = 0;
                if (currentSelectSwitchPosition > 9) currentSelectSwitchPosition = 9;
                wordFunctionHandler.getWordFunctionHandlerState().setCurrentFocusSwitchPosition(currentSelectSwitchPosition);
                reciteMarkToastAdapter.notifyItemChanged(currentFocusSwitchPosition, ReciteMarkToastAdapter.Item.SWITCH_DESELECT);
                reciteMarkToastAdapter.notifyItemChanged(currentSelectSwitchPosition, ReciteMarkToastAdapter.Item.SWITCH_SELECT);

            } else if (event.getAction() == MotionEvent.ACTION_UP &&
                    wordFunctionHandler.getWordFunctionHandlerState().isSwitching()) {
                float distinct = event.getY() - switchMarkColorDY;
                if (Math.abs(distinct) < 10) return false;
                FunctionWordVO currentFocusWord = wordFunctionHandler.getCurrentFocusWord();
                int currentFocusSwitchPosition = wordFunctionHandler.getWordFunctionHandlerState().getCurrentFocusSwitchPosition();
                MarkColor markColor = MarkColor.values()[currentFocusSwitchPosition];
                if (currentFocusWord.getMarkColorList().contains(markColor)) {
                    currentFocusWord.getMarkColorList().remove(markColor);
                } else {
                    currentFocusWord.getMarkColorList().add(markColor);
                }
                wordFunctionHandler.getWordFunctionHandlerState().setPreviousFocusSwitchPosition(currentFocusSwitchPosition);
                wordFunctionHandler.getWordFunctionHandlerState().setFunctionAreaFold(true);
                refreshMarkAreaUI();
            }
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.cv_main_recite_light
                && wordFunctionHandler.getWordFunctionHandlerState().isEnableSwitch()
                && wordFunctionHandler.getWordFunctionHandlerState().isLockLight()) {
            wordFunctionHandler.getWordFunctionHandlerState().setSwitching(true);
            // 马达震动提醒用户
            VibrationEffect waveform = VibrationEffect.createWaveform(new long[]{100}, -1);
            vibrator.vibrate(waveform);
        }
        return false;
    }

    /**
     * 新版本返回事件回调
     *
     */
    private OnBackPressedCallback getOnBackPressedCallBack() {
        return new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 优先关闭抽屉
                if (starDrawer != null && starDrawer.isDrawerOpen(GravityCompat.END)) {
                    starDrawer.closeDrawer(GravityCompat.END);
                    return;
                }
                // 二次点击
                if (System.currentTimeMillis() - exitLastTime > 2000) {
                    globalToast = Toast.makeText(MainReciteActivity.this, getString(R.string.click_to_exit), Toast.LENGTH_SHORT);
                    globalToast.setGravity(Gravity.CENTER, 0, 500);
                    globalToast.show();
                    exitLastTime = System.currentTimeMillis();
                } else {
                    Intent intent = new Intent(MainReciteActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    /**
     * 更新弹出窗口中所有按钮的状态
     */
    private void updateChangeModePopWindowState() {
        this.windowListingWrite.setBackground(null);
        this.windowEnglishChineseAudio.setBackground(null);
        this.windowEnglishChinese.setBackground(null);
        this.windowChineseEnglish.setBackground(null);
        this.windowOnlyRecite.setBackground(null);
        this.windowHidePhrase.setBackground(null);
        ReciteMode currentReciteMode = wordFunctionHandler.getWordFunctionHandlerState().getCurrentReciteMode();
        if (currentReciteMode == ReciteMode.ENGLISH_TRANSLATION_CHINESE_HEARING) {
            this.windowEnglishChineseAudio.setBackground(ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.background_green_select_border,
                    null));
        } else if (currentReciteMode == ReciteMode.ENGLISH_TRANSLATION_CHINESE_NO_HEARING) {
            this.windowEnglishChinese.setBackground(ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.background_green_select_border,
                    null));
        } else if (currentReciteMode == ReciteMode.CHINESE_TRANSLATION_ENGLISH) {
            this.windowChineseEnglish.setBackground(ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.background_green_select_border,
                    null));
        } else if (currentReciteMode == ReciteMode.LISTENING) {
            this.windowListingWrite.setBackground(ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.background_green_select_border,
                    null));
        } else if (currentReciteMode == ReciteMode.ONLY_RECITE) {
            this.windowOnlyRecite.setBackground(ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.background_green_select_border,
                    null));
        }
        if (wordFunctionHandler.getWordFunctionHandlerState().getRecitePreposition() == RecitePreposition.INVISIBLE) {
            this.windowHidePhrase.setBackground(ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.background_green_select_border,
                    null));
        }
    }

    /**
     * 按模式展示某个单词.
     * 展示单词是一种状态,随着单词的变化,页面的UI也要跟随变化.
     *
     * @param currentWord 待被展示的单词
     */
    private void reciteWord(FunctionWordVO currentWord) {
        // 更新UI相关
        updateUIHandler.post(() -> {
            // 切换单词时清除手写笔迹
            if (handwritingView != null && handwritingView.isHandwritingEnabled()) {
                handwritingView.clear();
            }
            // 如果隐藏了介词信息,必须在visible之前处理
            WordOriginEntity prepositionPhrase = currentWord.getValue().get(WordStructure.PHRASE);
            if (userRecitePreference.getRecitePreposition() == RecitePreposition.INVISIBLE)
                currentWord.getValue().remove(WordStructure.PHRASE);
            ReciteMode currentReciteMode = wordFunctionHandler.getWordFunctionHandlerState().getCurrentReciteMode();
            if (currentReciteMode == ReciteMode.LISTENING) {
                visibleWordAllMessage(currentWord);
                wordAudioHandler.playWordAudio(currentWord, this);
                resultWebViewHandler.gone();
                originWord.setText("");
            } else if (currentReciteMode == ReciteMode.ENGLISH_TRANSLATION_CHINESE_HEARING) {
                visibleWordAllMessage(currentWord);
                wordAudioHandler.playWordAudio(currentWord, this);
                resultWebViewHandler.gone();
            } else if (currentReciteMode == ReciteMode.ENGLISH_TRANSLATION_CHINESE_NO_HEARING) {
                visibleWordAllMessage(currentWord);
                resultWebViewHandler.gone();
            } else if (currentReciteMode == ReciteMode.CHINESE_TRANSLATION_ENGLISH) {
                // 先展示所有单词信息,然后将英文原文和音标进行隐藏;还要隐藏短语
                currentWord.getValue().remove(WordStructure.PHRASE);
                visibleWordAllMessage(currentWord);
                if (prepositionPhrase != null)
                    currentWord.getValue().put(WordStructure.PHRASE, prepositionPhrase);
                originWord.setText("");
            } else if (currentReciteMode == ReciteMode.ONLY_RECITE) {
                visibleWordAllMessage(currentWord);
                wordAudioHandler.playWordAudio(currentWord, this);
            }
            if (prepositionPhrase != null)
                currentWord.getValue().put(WordStructure.PHRASE, prepositionPhrase);
            wordCount.setText(String.valueOf(wordFunctionHandler.getChameleonSize()));
            currentIndex.setText(String.valueOf(wordFunctionHandler.getChameleonOrder()));
            lightHint.setText(String.format("%s/%s", wordFunctionHandler.getInnerIndex() + 1, wordFunctionHandler.functionWordSize()));
            refreshMarkAreaUI();
        });
    }

    /**
     * 刷新标记区域的UI
     */
    private void refreshMarkAreaUI() {
        reciteMarkToastAdapter.notifyItemRangeChanged(0, MarkColor.values().length, null);
    }

    /**
     * 刷新变色龙的UI
     */
    private void refreshChameleonUI() {
        if (wordFunctionHandler.getWordFunctionHandlerState().isSelectChameleon()) {
            if (globalToast != null) globalToast.cancel();
            functionChameleonBorder.setVisibility(View.VISIBLE);
            functionChameleonBorder.start();
            functionChameleonImageView.animate().scaleX(0.9f).scaleY(0.9f).setDuration(200).start();
            globalToast = Toast.makeText(this, R.string.choose_mark_color, Toast.LENGTH_SHORT);
            globalToast.setGravity(Gravity.CENTER, 0, 500);
            globalToast.show();
        } else {
            if (globalToast != null) globalToast.cancel();
            functionChameleonBorder.setVisibility(View.GONE);
            functionChameleonBorder.stop();
        }
        MarkColor markColor = Optional.ofNullable(wordFunctionHandler.getWordFunctionHandlerState().getChameleon().getValue())
                .orElse(MarkColor.GREEN);
        this.controlPreviousWord.getBackground().setTint(getResources().getColor(markColor.getMapColorID(), null));
        this.controlNextWord.getBackground().setTint(getResources().getColor(markColor.getMapColorID(), null));
    }

    /**
     * 显示当前单词的所有信息,<b>具体当前要根据状态隐藏哪些信息有调用者来处理.</b><br>
     * 该方法展示的是最全面的信息,所有隐藏信息都会被显示,但是单词没有的性质(比如没有某个中文意思)那么没有的内容不会展示.
     */
    private void visibleWordAllMessage(FunctionWordVO functionWordVO) {
        // 设置主界面的单词全部信息
        String wordOriginText = Optional.ofNullable(functionWordVO)
                .map(FunctionWordVO::getValue)
                .map(map -> map.get(WordStructure.WORD_ORIGIN))
                .map(wordOriginEntity -> wordOriginEntity.value)
                .orElse("");
        originWord.setText(wordOriginText);
        starCurrentWord.setText(wordOriginText);
        resultWebViewHandler.displayWordResult(functionWordVO);
        // 设置收藏夹信息
        starResultWebViewHandler.displayWordResult(functionWordVO);
    }

    private void bindView() {
        // 显示内容区域
        this.originWord = findViewById(R.id.tv_recite_origin);
        this.currentIndex = findViewById(R.id.tv_recite_control_index);
        this.wordCount = findViewById(R.id.tv_recite_control_count);
        this.back = findViewById(R.id.ib_main_recite_back);
        // 函数功能区
        this.lightResult = findViewById(R.id.cv_main_recite_light);
        this.lightArea = findViewById(R.id.ll_main_recite_light);
        this.lightHint = findViewById(R.id.ll_main_recite_hint);

        this.functionAreaHorizontalScrollView = findViewById(R.id.hs_main_recite_function_area);
        this.reciteMarkArea = findViewById(R.id.rc_recite_mark_area);
        this.functionMark = findViewById(R.id.ll_recite_function_mark);
        this.functionChameleon = findViewById(R.id.ll_recite_function_chameleon);
        this.functionChameleonImageView = findViewById(R.id.im_recite_function_chameleon);
        this.functionChameleonBorder = findViewById(R.id.flb_recite_function_chameleon);
        this.functionSwitch = findViewById(R.id.ll_recite_function_switch);
        this.functionSwitchImageView = findViewById(R.id.im_recite_function_switch);
        this.functionNote = findViewById(R.id.ll_recite_function_note);
        this.functionEditOrigin = findViewById(R.id.ll_recite_function_edit_origin);
        this.functionLock = findViewById(R.id.ll_recite_function_lock);
        this.functionLockImageView = findViewById(R.id.im_recite_function_lock);
        this.functionBlueTooth = findViewById(R.id.ll_recite_function_blue_tooth);
        this.functionBlueToothImageView = findViewById(R.id.im_recite_function_blue_tooth);
        this.functionBlueToothTextView = findViewById(R.id.tx_recite_function_blue_tooth);
        this.functionHandwriting = findViewById(R.id.ll_recite_function_handwriting);
        this.functionHandwritingImageView = findViewById(R.id.im_recite_function_handwriting);
        this.functionHandwritingTextView = findViewById(R.id.tx_recite_function_handwriting);
        this.handwritingView = findViewById(R.id.hw_recite_handwriting);
        this.functionGoto = findViewById(R.id.ll_recite_function_goto);
        this.functionStar = findViewById(R.id.ll_recite_function_star);
        this.functionShuffle = findViewById(R.id.ll_recite_function_shuffle);
        this.functionShuffleImageView = findViewById(R.id.im_recite_function_shuffle);
        this.functionSection = findViewById(R.id.ll_recite_function_section);
        this.functionSectionImageView = findViewById(R.id.iv_recite_function_section);
        this.functionSaveProgress = findViewById(R.id.ll_recite_function_saving);

        this.functionMode = findViewById(R.id.ll_recite_function_mode);
        this.functionChangeModePopLayout = (TableLayout) getLayoutInflater().inflate(R.layout.window_main_recite_mode, null);
        this.windowListingWrite = functionChangeModePopLayout.findViewById(R.id.window_mode_listening_write);
        this.windowEnglishChineseAudio = functionChangeModePopLayout.findViewById(R.id.window_mode_english_chinese_audio);
        this.windowEnglishChinese = functionChangeModePopLayout.findViewById(R.id.window_mode_english_chinese);
        this.windowChineseEnglish = functionChangeModePopLayout.findViewById(R.id.window_mode_chinese_english);
        this.windowOnlyRecite = functionChangeModePopLayout.findViewById(R.id.window_mode_only_recite);
        this.windowHidePhrase = functionChangeModePopLayout.findViewById(R.id.window_mode_hide_phrase);

        this.functionQuickPosition = findViewById(R.id.ll_recite_function_position);
        this.functionQuickPositionImageView = findViewById(R.id.iv_recite_function_position);
        this.functionSearchWord = findViewById(R.id.ll_recite_function_search);
        this.functionAnalysis = findViewById(R.id.ll_recite_function_analysis);
        // 控制区
        this.controlNextWord = findViewById(R.id.tv_recite_control_next);
        this.controlPreviousWord = findViewById(R.id.tv_recite_control_previous);
        this.controlFunctionAreaFold = findViewById(R.id.iv_recite_control_fold);
        this.controlPlay = findViewById(R.id.iv_recite_control_play);
        // 收藏夹区域
        this.starDrawer = findViewById(R.id.dr_main_recite_star);
        this.starCurrentWord = findViewById(R.id.tv_recite_star_current_word);
        this.starMove = findViewById(R.id.iv_recite_star_move);
        this.starList = findViewById(R.id.rc_recite_star_list);
        this.starCreateCategory = findViewById(R.id.tv_recite_star_create);
        // 其它
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        bindListener();
    }

    /**
     * 设置所有监听事件
     */
    private void bindListener() {
        this.controlFunctionAreaFold.setOnClickListener(this);
        this.controlNextWord.setOnClickListener(this);
        this.controlPreviousWord.setOnClickListener(this);
        this.lightResult.setOnClickListener(this);
        this.functionGoto.setOnClickListener(this);
        this.functionMark.setOnClickListener(this);
        this.functionChameleon.setOnClickListener(this);
        this.functionShuffle.setOnClickListener(this);
        this.functionSection.setOnClickListener(this);
        this.back.setOnClickListener(this);
        this.functionMode.setOnClickListener(this);
        this.functionQuickPosition.setOnClickListener(this);
        this.windowListingWrite.setOnClickListener(this);
        this.windowEnglishChineseAudio.setOnClickListener(this);
        this.windowEnglishChinese.setOnClickListener(this);
        this.windowChineseEnglish.setOnClickListener(this);
        this.windowOnlyRecite.setOnClickListener(this);
        this.windowHidePhrase.setOnClickListener(this);
        this.controlPlay.setOnClickListener(this);
        this.functionStar.setOnClickListener(this);
        this.starCreateCategory.setOnClickListener(this);
        this.starMove.setOnClickListener(this);
        this.functionSearchWord.setOnClickListener(this);
        this.functionSaveProgress.setOnClickListener(this);
        this.functionAnalysis.setOnClickListener(this);
        this.functionSwitch.setOnClickListener(this);
        this.functionNote.setOnClickListener(this);
        this.functionEditOrigin.setOnClickListener(this);
        this.functionLock.setOnClickListener(this);
        this.lightResult.setOnTouchListener(this);
        this.functionBlueTooth.setOnClickListener(this);
        this.functionHandwriting.setOnClickListener(this);
        this.lightResult.setOnLongClickListener(this);
        // 手写识别结果监听
        this.handwritingView.setOnRecognitionListener(recognizedText -> {
            updateUIHandler.post(() -> {
                if (recognizedText == null || recognizedText.isEmpty()) {
                    showSpellingErrorToast();
                    return;
                }
                // 获取当前单词的原文
                FunctionWordVO currentFocusWord = wordFunctionHandler.getCurrentFocusWord();
                String wordOrigin = java.util.Optional.ofNullable(currentFocusWord)
                        .map(FunctionWordVO::getValue)
                        .map(map -> map.get(com.github.lorenj.wordtint.enums.WordStructure.WORD_ORIGIN))
                        .map(wordOriginEntity -> wordOriginEntity.value)
                        .orElse("");
                // 忽略大小写和空格进行比较
                if (recognizedText.trim().equalsIgnoreCase(wordOrigin.trim())) {
                    // 匹配成功，模拟点击灯泡
                    lightResult.performClick();
                } else {
                    showSpellingErrorToast();
                }
            });
        });
    }

    /**
     * 显示拼写错误 Toast，不允许重复显示
     */
    private void showSpellingErrorToast() {
        if (globalToast != null) {
            globalToast.cancel();
        }
        globalToast = Toast.makeText(this, R.string.spelling_error, Toast.LENGTH_SHORT);
        globalToast.setGravity(Gravity.CENTER, 0, 0);
        globalToast.show();
    }


}