package com.github.lorenj.wordtint.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.AnyLanguageWordProperties;
import com.github.lorenj.wordtint.context.pathsystem.document.WordContextPath;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.entity.UserCreditStyle;
import com.github.lorenj.wordtint.entity.dto.WordCategoryDTO;
import com.github.lorenj.wordtint.entity.dto.WordCategoryDetailVO;
import com.github.lorenj.wordtint.entity.local.AddWordAnalysisParamLocal;
import com.github.lorenj.wordtint.entity.local.AddWordReViewParamLocal;
import com.github.lorenj.wordtint.entity.local.DivideDTOLocal;
import com.github.lorenj.wordtint.entity.local.FunctionWordDTOLocal;
import com.github.lorenj.wordtint.entity.local.HistoryDTOLocal;
import com.github.lorenj.wordtint.entity.local.ProjectorDTOLocal;
import com.github.lorenj.wordtint.entity.local.WordDTOLocal;
import com.github.lorenj.wordtint.enums.CreditFilter;
import com.github.lorenj.wordtint.enums.CreditOrder;
import com.github.lorenj.wordtint.enums.CreditState;
import com.github.lorenj.wordtint.enums.FlagColor;
import com.github.lorenj.wordtint.enums.WordFunctionState;
import com.github.lorenj.wordtint.enums.structure.EnglishStructure;
import com.github.lorenj.wordtint.handler.WordAnalysisHandler;
import com.github.lorenj.wordtint.handler.WordFunctionHandler;
import com.github.lorenj.wordtint.handler.WordSupplementReviewHandler;
import com.github.lorenj.wordtint.handler.impl.WordAnalysisHandlerImpl;
import com.github.lorenj.wordtint.handler.impl.WordFunctionHandlerImpl;
import com.github.lorenj.wordtint.handler.impl.WordSupplementReviewHandlerImpl;
import com.github.lorenj.wordtint.ui.MainActivity;
import com.github.lorenj.wordtint.ui.adapter.SimpleItemTouchHelperCallback;
import com.github.lorenj.wordtint.ui.adapter.StarChineseAnswerRecyclerViewAdapter;
import com.github.lorenj.wordtint.ui.adapter.StartSingleCategoryAdapter;
import com.github.lorenj.wordtint.ui.adapter.wordsearch.ChineseAnswerHandler;
import com.github.lorenj.wordtint.ui.fragment.CreditFragment;
import com.github.lorenj.wordtint.ui.fragment.WordCreditFragment;
import com.github.lorenj.wordtint.utils.AnimationUtil;
import com.github.lorenj.wordtint.utils.DPUtils;
import com.github.lorenj.wordtint.utils.FileUtils;
import com.github.lorenj.wordtint.utils.JsonUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MainReciteActivity extends AppCompatActivity implements View.OnClickListener,
        KeyEvent.Callback,
        View.OnTouchListener,
        View.OnLongClickListener,
        Window.Callback {

    private View parentView, projectorParent;

    private ImageButton popMoreFunction;
    private HorizontalScrollView moreFunctionHorizontalScrollView;
    private boolean moreFunctionOpen = true, openFlagChange, changingChameleon;
    private Handler updateUIHandler;
    private WordFunctionHandler wordFunctionHandler;
    private DrawerLayout startDrawer;
    private RecyclerView chineseAnswerDrawer, starSingleCategory;
    private ChineseAnswerHandler chineseAnswerHandler;
    private StarChineseAnswerRecyclerViewAdapter chineseAnswerAdapterDrawer;
    private StartSingleCategoryAdapter startSingleCategoryAdapter;
    /**
     * 所有单词信息详细信息<br>
     * Key:单词的id<br>
     * Value:单词详细信息
     */
    private Map<Long, WordDTOLocal> dict;
    // 单词音频播放器
    private final MediaPlayer mediaPlayer = new MediaPlayer();

    /*
    以下是所有功能按钮的变量声明
     */
    private ImageButton popBackStack, playWord;
    private TextView sourceWord, nextWord, previousWord;
    private TextView currentIndexTextView, wordCount, chameleonCount, projectorHint;
    private TextView sourceWordDrawer, phraseHintDrawer, phraseAnswerDrawer, addNewStartCategory;
    private AlertDialog loadingDialog = null;
    private LinearLayout jumpNextWord, flagChangeArea, clickFlag, chameleonMode, swingSwitch, lockAnswer, blueTooth, viewFlagArea, shuffle, section, projector, changeMode, quickPosition, start, searchWord, saveProgress, wordAnalysis;
    private LinearLayout getAnswerParentLayout;
    private CardView popWindowChangeModeLayout, getAnswer;
    private ImageView clickFlagImageView, chameleonImageView, swingSwitchImageView, lockAnswerImageView, blueToothImageView, shuffleImageView, sectionImageView, quickPositionImageView, starRefresh;
    private TextView listeningWriteMode, englishTranslationChineseModeHearing, englishTranslationChineseModeNoHearing, chineseTranslationEnglish, onlyCreditMode, hideProNoun;
    private TextView countDownTop, countDownBottom, countDownInterrupt, countDownExit;
    private long exitLastTime = 0;
    /**
     * 旗帜
     */
    private ImageButton greenFlag, redFlag, orangeFlag, yellowFlag, blueFlag, cyanFlag, purpleFlag, pinkFlag, grayFlag, blackFlag, brownFlag;
    private List<ImageButton> selectList;
    private ImageButton currentSelectFlagButton;

    /**
     * 用户的背词风格
     */
    private UserCreditStyle userCreditStyle;
    /**
     * 单词分析的功能
     */
    private WordAnalysisHandler wordAnalysisHandler;
    private WordSupplementReviewHandler wordSupplementReviewHandler;
    private final Set<FlagColor> recordFlagColor = new HashSet<>(10);

    /**
     * 滑动显示答案组件时的坐标<br>
     * 以及是否长按点击了显示答案按钮
     */
    private float answerDY;
    /**
     * 滑动改变答案按钮位置
     */
    private float answerChangeDY, answerChangeDX, answerParentLayoutY = 0, answerParentLayoutX = 0;
    private boolean longClickAnswer = false, enableSelectFunction = false, lockAnswerLocation = true, enableBlueTooth = true;
    /**
     * 切换的度
     */
    private final float answerDegree = 62.5f;
    /**
     * 常量
     */
    public static final String ANALYSIS_WORD = "ANALYSIS_WORD";

    /**
     * 马达
     */
    private Vibrator vibrator;
    /**
     * activity
     */
    private FragmentActivity requireActivity;

    /**
     * 蓝牙相关
     */
    private float blueToothDownX, blueToothDownY;
    private int blueToothMoveIndex = -1;
    private int preBlueToothMoveIndex = 0;

    /**
     * 定时任务
     */
    private volatile boolean timeTaskRunning = true;
    private volatile boolean projectorFinish;
    private int projectorFade = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recite);
        bindView();
        initView();
    }

    private void initView() {
        this.chineseAnswerHandler = new ChineseAnswerHandler(this, findViewById(R.id.fragment_word_credit_chinese_answer));
        // 弹出Dialog不要阻塞UI线程,通过一个新的线程去请求所有单词信息.
        loadingDialog = new AlertDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_loading, null))
                .setCancelable(false)
                .show();
        // 锁定startDrawable的关闭
        startDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // 必须在这里设置LayoutManager
        this.chineseAnswerDrawer.setLayoutManager(new LinearLayoutManager(this));
        this.starSingleCategory.setLayoutManager(new LinearLayoutManager(this));
        // 读取状态
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        this.userCreditStyle = (UserCreditStyle) Optional.ofNullable(bundle)
                .map(b -> b.getSerializable(CreditFragment.USER_CREDIT_STYLE_WRAPPER))
                .orElse(null);
        // 读取所有单词信息,通过Bundle得到当前用户选中的单词分类,这里暂时以样本单词进行测试.
        // 异步执行
        StaticFactory.getExecutorService().submit(() -> {
            // 根据自定义风格背诵,获取当前要加载的所有单词
            List<FunctionWordDTOLocal> allFunctionWordList = new ArrayList<>(30);
            // 初始化单词操作
            HashSet<DivideDTOLocal> divideList = bundle.getSerializable(CreditFragment.CHILD_DIVIDE_SET, HashSet.class);
            HashSet<HistoryDTOLocal> historyDTOSet = bundle.getSerializable(CreditFragment.HISTORY_WORD_SET, HashSet.class);
            ArrayList<Long> reViewList = bundle.getSerializable(CreditFragment.REVIEW_WORD_List, ArrayList.class);
            // 删除bundle内容
            bundle.remove(CreditFragment.CHILD_DIVIDE_SET);
            bundle.remove(CreditFragment.HISTORY_WORD_SET);
            bundle.remove(CreditFragment.REVIEW_WORD_List);
            // 新的背词
            if (divideList != null) {
                // 初始化
                List<Long> initWordList = divideList.stream()
                        .map(DivideDTOLocal::getWordIdList)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
                for (int i = 0; i < initWordList.size(); i++) {
                    FunctionWordDTOLocal functionWordDTOLocal = new FunctionWordDTOLocal();
                    functionWordDTOLocal.setId(initWordList.get(i));
                    HashSet<FlagColor> wordsFlagList = new HashSet<>();
                    wordsFlagList.add(FlagColor.GREEN);
                    wordsFlagList.add(FlagColor.BROWN);
                    functionWordDTOLocal.setWordsFlagList(wordsFlagList);
                    allFunctionWordList.add(functionWordDTOLocal);
                }
            }
            // 历史记录
            if (historyDTOSet != null) {
                for (HistoryDTOLocal historyDTOLocal : historyDTOSet) {
                    allFunctionWordList.addAll(historyDTOLocal.getSerializeWordList());
                }
            }
            // 单词回顾
            if (reViewList != null) {
                for (int i = 0; i < reViewList.size(); i++) {
                    FunctionWordDTOLocal functionWordDTOLocal = new FunctionWordDTOLocal();
                    functionWordDTOLocal.setId(reViewList.get(i));
                    HashSet<FlagColor> wordsFlagList = new HashSet<>();
                    wordsFlagList.add(FlagColor.GREEN);
                    wordsFlagList.add(FlagColor.BROWN);
                    functionWordDTOLocal.setWordsFlagList(wordsFlagList);
                    allFunctionWordList.add(functionWordDTOLocal);
                }
            }
            // 读取字典
            dict = StaticFactory.getAllWordDict();
            if (userCreditStyle.getCreditOrder() == CreditOrder.DISORDER) {
                Collections.shuffle(allFunctionWordList);
            } else if (userCreditStyle.getCreditOrder() == CreditOrder.LEXICOGRAPHIC) {
            }
            if (userCreditStyle.getCreditFilter() == CreditFilter.PHRASE) {
            }
            // 单词分析功能
            this.wordAnalysisHandler = new WordAnalysisHandlerImpl(this);
            this.wordSupplementReviewHandler = new WordSupplementReviewHandlerImpl(this);

            this.wordFunctionHandler = new WordFunctionHandlerImpl(allFunctionWordList, dict);
            if (userCreditStyle != null) {
                this.wordFunctionHandler.setCurrentCreditState(userCreditStyle.getCreditState());
            }
            // 设置收藏夹列表中中文意思显示的adapter
            this.chineseAnswerAdapterDrawer = new StarChineseAnswerRecyclerViewAdapter(this, 2L);
            this.startSingleCategoryAdapter = new StartSingleCategoryAdapter(this);
            // 绑定ItemTouchHelper,实现单个列表的编辑删除等功能
            ItemTouchHelper touchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(startSingleCategoryAdapter));
            startSingleCategoryAdapter.setStartDragListener(touchHelper::startDrag);
            startSingleCategoryAdapter.setStartFunctionHandler(wordFunctionHandler);
            // 读取用户收藏夹信息
            try {
                wordFunctionHandler.batchAddCategory(JsonUtils.readJsonArray(WordContextPath.WORD_STAR.getPath(), WordCategoryDetailVO.class));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 启动定时任务
            StaticFactory.getExecutorService().execute(timedTasks());
            // 更新UI
            updateUIHandler.post(() -> {
                this.chineseAnswerDrawer.setAdapter(chineseAnswerAdapterDrawer);
                this.starSingleCategory.setAdapter(startSingleCategoryAdapter);
                touchHelper.attachToRecyclerView(starSingleCategory);
                reciteWord(wordFunctionHandler.getCurrentStructureWordMap(), wordFunctionHandler.getWordByIndex(0));
                wordCount.setText(String.valueOf(wordFunctionHandler.size()));
                flagChangeArea.setVisibility(View.GONE);
                closeFlagChangeAreaFlush();
                updateChangeModePopWindowState();
                loadingDialog.dismiss();
            });
        });

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (startDrawer.isDrawerOpen(GravityCompat.END)) {
                startDrawer.closeDrawer(GravityCompat.END);
                return true;
            }
            if (System.currentTimeMillis() - exitLastTime > 2000) {
                Toast toast = Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                exitLastTime = System.currentTimeMillis();
            } else {
                Navigation.findNavController(popBackStack).popBackStack();
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 屏蔽蓝牙未开启的点击
        if (event.getSource() == 769 && !enableBlueTooth) {
            return true;
        }
        if (enableBlueTooth && event.getSource() == 769
                && event.getAction() == KeyEvent.ACTION_DOWN
                && (keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            getAnswer.performClick();
            return true;
        }
        return false;
    }
    /*
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 屏蔽蓝牙未开启的点击
        if (event.getSource() == InputDevice.SOURCE_MOUSE && !enableBlueTooth) {
            return true;
        }
        if (event.getSource() == InputDevice.SOURCE_MOUSE && enableBlueTooth) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    blueToothDownX = event.getRawX();
                    blueToothDownY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    // 修改样式
                    float distinctX = event.getRawX() - blueToothDownX;
                    float distinctY = event.getRawY() - blueToothDownY;
                    if (distinctX == 0 && distinctY == 0) {
                        if (Math.abs(blueToothDownX - 91) < 5 && Math.abs(blueToothDownY - 2322) < 5) {
                            popMoreFunction.performClick();
                        } else if (openFlagChange && blueToothMoveIndex >= 0 && blueToothMoveIndex <= 8) {
                            // 如果当前正展开了旗帜
                            this.currentSelectFlagButton.setBackground(this.getDrawable(R.drawable.style_image_padding));
                            this.currentSelectFlagButton.performClick();
                            clickFlag.performClick();
                        } else {
                            playWord.performClick();
                        }
                    } else if (distinctX == 0 && distinctY > 0) {
                        blueToothMoveIndex--;
                        if (!openFlagChange) clickFlag.performClick();
                    } else if (distinctX == 0 && distinctY < 0) {
                        blueToothMoveIndex++;
                        if (!openFlagChange) clickFlag.performClick();
                    } else if (distinctY == 0 && distinctX > 0) {
                        previousWord.performClick();
                    } else if (distinctY == 0 && distinctX < 0) {
                        nextWord.performClick();
                    }
                    // 如果当前展开了旗帜
                    if (openFlagChange) {
                        if (blueToothMoveIndex < 0) {
                            blueToothMoveIndex = preBlueToothMoveIndex;
                        } else if (blueToothMoveIndex > 8) {
                            blueToothMoveIndex = 8;
                        }
                        selectList.forEach(imageButton -> imageButton.setBackground(this.getDrawable(R.drawable.style_image_padding)));
                        this.currentSelectFlagButton = selectList.get(preBlueToothMoveIndex = blueToothMoveIndex);
                        this.currentSelectFlagButton.setBackground(this.getDrawable(R.drawable.style_image_padding_selective));
                    }
                    break;
            }
            return true;
        }
        return false;

        return false;
    }
     */

    @Override
    public void onResume() {
        super.onResume();
        updateUIHandler.post(() -> {
            this.answerParentLayoutY = getAnswerParentLayout.getY();
            this.answerParentLayoutX = getAnswerParentLayout.getX();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.timeTaskRunning = false;
    }

    @Override
    public void onClick(View v) {
        Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        int clickViewId = v.getId();
        if (clickViewId == R.id.fragment_word_credit_pop_more_function) {
            if (moreFunctionOpen) {
                if (AnimationUtil.with().moveToViewBottom(moreFunctionHorizontalScrollView, 500)) {
                    popMoreFunction.setImageResource(R.drawable.open_previous);
                    moreFunctionOpen = !moreFunctionOpen;
                }
            } else {
                if (AnimationUtil.with().bottomMoveToViewLocation(moreFunctionHorizontalScrollView, 500)) {
                    popMoreFunction.setImageResource(R.drawable.open_after);
                    moreFunctionOpen = !moreFunctionOpen;
                }
            }
        } else if (clickViewId == R.id.fragment_word_credit_next_word) {
            emptyUI();
            reciteWord(wordFunctionHandler.getCurrentStructureWordMap(), wordFunctionHandler.jumpNextWord());
        } else if (clickViewId == R.id.fragment_word_credit_previous_word) {
            emptyUI();
            reciteWord(wordFunctionHandler.getCurrentStructureWordMap(), wordFunctionHandler.jumpPreviousWord());
        } else if (clickViewId == R.id.fragment_word_card_view_get_answer) {
            visibleWordAllMessage(wordFunctionHandler.getCurrentStructureWordMap());
        } else if (clickViewId == R.id.fragment_word_credit_play_word) {
            reciteWord(wordFunctionHandler.getCurrentStructureWordMap(), wordFunctionHandler.getCurrentStructureWordMap());
            // 如果当前是听音频模式,需要手动播放音频
            if (wordFunctionHandler.getCurrentCreditState() == CreditState.ENGLISH_TRANSLATION_CHINESE_NO_HEARING) {
                playWordAudio(wordFunctionHandler.getCurrentStructureWordMap());
            }
        } else if (clickViewId == R.id.fragment_word_credit_jump_next) {
            final EditText inputEditText = new EditText(this);
            inputEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
            new AlertDialog.Builder(this).setTitle("跳转单词").setMessage("输入要跳转到第几个单词,你应当输入1到" + wordFunctionHandler.getChameleonSize() + "之间的值.").setView(inputEditText).setCancelable(false).setPositiveButton("确定", (dialog, which) -> {
                String value = inputEditText.getText().toString();
                int i;
                try {
                    i = Integer.parseInt(value);
                    if (i <= 0 || i > wordFunctionHandler.getChameleonSize()) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast exceptionToast = Toast.makeText(this, "输入错误,请输入1~" + wordFunctionHandler.getChameleonSize() + "之间的值", Toast.LENGTH_LONG);
                    exceptionToast.setGravity(Gravity.CENTER, 0, 500);
                    exceptionToast.show();
                    return;
                }
                // 异步跳转单词,可能查找时间较长
                StaticFactory.getExecutorService().submit(() ->
                        reciteWord(wordFunctionHandler.getCurrentStructureWordMap(),
                                wordFunctionHandler.jumpToWord(i - 1)));
            }).setNegativeButton("取消", (dialog, which) -> {
            }).show();
        } else if (clickViewId == R.id.fragment_word_credit_click_flag) {
            if (openFlagChange) {
                if (AnimationUtil.with().moveToViewEnd(flagChangeArea, 500)) {
                    closeFlagChangeAreaFlush();
                    openFlagChange = !openFlagChange;
                }
            } else {
                if (AnimationUtil.with().endMoveToViewLocation(flagChangeArea, 500)) {
                    openFlagChangeAreaFlush();
                    openFlagChange = !openFlagChange;
                }
            }
        } else if (clickViewId == R.id.fragment_word_credit_click_chameleon_mode) {
            // 如果当前处于按色打乱模式则无法使用变色龙功能,使用区间重背功能可以使用变色龙功能
            if (wordFunctionHandler.getWordFunctionState() == WordFunctionState.SHUFFLE) {
                toast = Toast.makeText(this, wordFunctionHandler.getWordFunctionState().getInfo(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                return;
            }
            // 不要管太多,当点击变色龙模式的时候就展开旗帜区域就可以了
            if (!openFlagChange) {
                if (AnimationUtil.with().endMoveToViewLocation(flagChangeArea, 500)) {
                    openFlagChangeAreaFlush();
                    openFlagChange = !openFlagChange;
                }
            }
            toast.cancel();
            toast = Toast.makeText(this, R.string.please_click_flag_change_chameleon, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 500);
            toast.show();
            changingChameleon = true;
        } else if (clickViewId == R.id.fragment_word_credit_click_quick_position) {
            // 如果不是普通模式,则禁止使用快速定位功能
            if (wordFunctionHandler.getWordFunctionState() != WordFunctionState.NONE) {
                toast = Toast.makeText(this, wordFunctionHandler.getWordFunctionState().getInfo(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                return;
            }
            final EditText inputEditText = new EditText(this);
            inputEditText.setKeyListener(DigitsKeyListener.getInstance("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ "));
            inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            new AlertDialog.Builder(this).setTitle("定位单词").setView(inputEditText).setCancelable(false).setPositiveButton("确定", (dialog, which) -> {
                String origin = inputEditText.getText().toString().trim();
                int index = wordFunctionHandler.getIndexByWordOrigin(origin);
                // 未查询到单词
                // 如果查询到的单词不在当前变色龙列表内,则不跳转
                if (index == -1) {
                    Toast exceptionToast = Toast.makeText(this, "单词未找到", Toast.LENGTH_SHORT);
                    exceptionToast.setGravity(Gravity.CENTER, 0, 500);
                    exceptionToast.show();
                    return;
                }
                // 异步跳转单词,可能查找时间较长
                StaticFactory.getExecutorService().submit(() ->
                        reciteWord(wordFunctionHandler.getCurrentStructureWordMap(),
                                wordFunctionHandler.jumpToWordWithOutFlag(index)));
            }).setNegativeButton("取消", (dialog, which) -> {
            }).show();
        } else if (clickViewId == R.id.fragment_word_credit_click_swing_switch) {
            enableSelectFunction = !enableSelectFunction;
            if (enableSelectFunction) {
                this.swingSwitchImageView.getDrawable().setTint(getResources().getColor(R.color.gold, null));
            } else {
                this.swingSwitchImageView.getDrawable().setTintList(null);
            }
        } else if (clickViewId == R.id.fragment_word_credit_click_lock_answer) {
            lockAnswerLocation = !lockAnswerLocation;
            if (lockAnswerLocation) {
                this.lockAnswerImageView.getDrawable().setTint(getResources().getColor(android.R.color.holo_red_dark, null));
            } else {
                getAnswerParentLayout.setX(answerParentLayoutX);
                getAnswerParentLayout.setY(answerParentLayoutY);
                this.lockAnswerImageView.getDrawable().setTintList(null);
            }
        } else if (clickViewId == R.id.fragment_word_credit_click_blue_tooth) {
            enableBlueTooth = !enableBlueTooth;
            if (enableBlueTooth) {
                this.blueToothImageView.getDrawable().setTint(getResources().getColor(android.R.color.holo_blue_dark, null));
            } else {
                this.blueToothImageView.getDrawable().setTint(getResources().getColor(R.color.dark_gray, null));
            }
        } else if (clickViewId == R.id.fragment_word_credit_click_shuffle) {
            // 如果当前不是普通状态和按色打乱状态,代表当前在执行别的状态,需要先锁定按色打乱的功能
            if (wordFunctionHandler.getWordFunctionState() == WordFunctionState.RANGE) {
                toast = Toast.makeText(this, wordFunctionHandler.getWordFunctionState().getInfo(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                return;
            }
            WordDTOLocal previous = wordFunctionHandler.getCurrentStructureWordMap();
            if (wordFunctionHandler.getWordFunctionState() == WordFunctionState.NONE) {
                this.chameleonImageView.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_prohibit_foreground, null));
                this.sectionImageView.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_prohibit_foreground, null));
                this.quickPositionImageView.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_prohibit_foreground, null));
                this.shuffleImageView.getDrawable().setTint(getResources().getColor(wordFunctionHandler.getChameleon().getMapColorID(), null));
                wordFunctionHandler.shuffle();
                reciteWord(previous, wordFunctionHandler.jumpToWord(0));
            } else {
                this.chameleonImageView.setForeground(null);
                this.sectionImageView.setForeground(null);
                this.quickPositionImageView.setForeground(null);
                this.shuffleImageView.getDrawable().setTintList(null);
                wordFunctionHandler.restoreWordList();
                reciteWord(previous, wordFunctionHandler.getCurrentStructureWordMap());
            }
        } else if (clickViewId == R.id.fragment_word_credit_click_section) {
            if (wordFunctionHandler.getWordFunctionState() == WordFunctionState.SHUFFLE) {
                toast = Toast.makeText(this, wordFunctionHandler.getWordFunctionState().getInfo(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                return;
            }
            WordDTOLocal previous = wordFunctionHandler.getCurrentStructureWordMap();
            if (wordFunctionHandler.getWordFunctionState() == WordFunctionState.NONE) {
                View rangeRandomWordInputView = getLayoutInflater().inflate(R.layout.fragment_word_credit_dialog_section, null);
                EditText minValue = rangeRandomWordInputView.findViewById(R.id.fragment_word_credit_dialog_section_min_value);
                EditText maxValue = rangeRandomWordInputView.findViewById(R.id.fragment_word_credit_dialog_section_max_value);
                new AlertDialog.Builder(this).setTitle("区间随机:")
                        .setMessage("选择要单独随机的区间:[1," + wordFunctionHandler.getChameleonSize() + "].注意这里是闭区间")
                        .setView(rangeRandomWordInputView)
                        .setCancelable(false)
                        .setPositiveButton("确定", (dialog, which) -> {
                            int minRange, maxRange;
                            try {
                                minRange = Integer.parseInt(minValue.getText().toString()) - 1;
                                maxRange = Integer.parseInt(maxValue.getText().toString()) - 1;
                                if (minRange < 0 || maxRange > wordFunctionHandler.getChameleonSize() || minRange > maxRange) {
                                    throw new IllegalArgumentException("输入参数不合法!");
                                }
                            } catch (IllegalArgumentException e) {
                                Toast errorToast = Toast.makeText(this, "输入错误,请输入1~" + wordFunctionHandler.getChameleonSize() + "之间的值", Toast.LENGTH_LONG);
                                errorToast.setGravity(Gravity.CENTER, 0, 500);
                                errorToast.show();
                                return;
                            }
                            // 确定执行区间随机时执行的内容
                            wordFunctionHandler.shuffleRange(minRange, maxRange);
                            reciteWord(previous, wordFunctionHandler.jumpToWord(wordFunctionHandler.getCurrentIndex()));
                            this.shuffleImageView.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_prohibit_foreground, null));
                            this.quickPositionImageView.setForeground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_prohibit_foreground, null));
                            this.sectionImageView.getDrawable().setTint(getResources().getColor(R.color.theme_color, null));
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                        })
                        .show();
            } else {
                this.shuffleImageView.setForeground(null);
                this.quickPositionImageView.setForeground(null);
                this.sectionImageView.getDrawable().setTintList(null);
                this.wordFunctionHandler.restoreWordList();
                reciteWord(previous, wordFunctionHandler.getCurrentStructureWordMap());
            }
        } else if (clickViewId == R.id.fragment_word_credit_projector_parent) {
            countDownInterrupt.setVisibility(View.VISIBLE);
            countDownExit.setVisibility(View.VISIBLE);
        } else if (clickViewId == R.id.fragment_word_credit_click_projector) {
            ProjectorDTOLocal existProjector = wordFunctionHandler.calculateCountdown();
            if (existProjector != null) {
                parentView.setVisibility(View.GONE);
                projectorParent.setVisibility(View.VISIBLE);
                requireActivity.getWindow().setStatusBarColor(Color.BLACK);
                requireActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                projectorFade = 0;
                existProjector.setRunning(true);
                wordFunctionHandler.startProjector(existProjector);
                return;
            }
            View projectorInputView = getLayoutInflater().inflate(R.layout.fragment_word_credit_dialog_projector, null);
            EditText minuteValue = projectorInputView.findViewById(R.id.fragment_word_credit_dialog_projector_minute);
            EditText wordCountValue = projectorInputView.findViewById(R.id.fragment_word_credit_dialog_projector_word_count);
            EditText startIndexValue = projectorInputView.findViewById(R.id.fragment_word_credit_dialog_projector_start_index);
            new AlertDialog.Builder(this).setTitle(this.getResources().getString(R.string.projector))
                    .setView(projectorInputView)
                    .setCancelable(false)
                    .setPositiveButton("确定", (dialog, which) -> {
                        int minute, wordCount, startIndex;
                        try {
                            minute = Integer.parseInt(minuteValue.getText().toString());
                            wordCount = Integer.parseInt(wordCountValue.getText().toString());
                            startIndex = Integer.parseInt(startIndexValue.getText().toString());
                            if (minute < 0 || wordCount > wordFunctionHandler.getChameleonSize() ||
                                    startIndex < 1 || startIndex > wordFunctionHandler.getChameleonSize()) {
                                throw new IllegalArgumentException("输入参数不合法!");
                            }
                        } catch (IllegalArgumentException e) {
                            Toast errorToast = Toast.makeText(this, "输入参数不合法", Toast.LENGTH_LONG);
                            errorToast.setGravity(Gravity.CENTER, 0, 500);
                            errorToast.show();
                            return;
                        }
                        ProjectorDTOLocal projectorDTOLocal = new ProjectorDTOLocal();
                        projectorDTOLocal.setMinute(minute);
                        projectorDTOLocal.setWordCount(wordCount);
                        projectorDTOLocal.setRunning(true);
                        projectorDTOLocal.setRound(1);
                        projectorDTOLocal.setStartIndex(startIndex);
                        wordFunctionHandler.startProjector(projectorDTOLocal);
                        parentView.setVisibility(View.GONE);
                        projectorParent.setVisibility(View.VISIBLE);
                        requireActivity.getWindow().setStatusBarColor(Color.BLACK);
                        requireActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        projectorFade = 0;
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                    })
                    .show();
        } else if (clickViewId == R.id.fragment_word_credit_projector_countdown_exit) {
            ProjectorDTOLocal calculateCountdown = wordFunctionHandler.calculateCountdown();
            parentView.setVisibility(View.VISIBLE);
            projectorParent.setVisibility(View.GONE);
            requireActivity.getWindow().setStatusBarColor(Color.WHITE);
            requireActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            calculateCountdown.setRunning(false);
            projectorFinish = false;
            // 跳转单词
            int jumpIndex = (calculateCountdown.getRound() - 1) * calculateCountdown.getWordCount() + calculateCountdown.getStartIndex() - 1;
            if (jumpIndex < wordFunctionHandler.getChameleonSize()) {
                reciteWord(wordFunctionHandler.getCurrentStructureWordMap(), wordFunctionHandler.jumpToWord(jumpIndex));
                projectorHint.setText(String.valueOf(calculateCountdown.getRound() * calculateCountdown.getWordCount() +
                        calculateCountdown.getStartIndex()));
            }
            calculateCountdown.setRound(calculateCountdown.getRound() + 1);
            requireActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else if (clickViewId == R.id.fragment_word_credit_projector_countdown_interrupt) {
            ProjectorDTOLocal projectorDTOLocal = wordFunctionHandler.calculateCountdown();
            projectorDTOLocal.setRunning(!projectorDTOLocal.isRunning());
            countDownTop.setVisibility(View.GONE);
            countDownBottom.setVisibility(View.GONE);
            if (projectorDTOLocal.isRunning()) {
                countDownInterrupt.setText(getResources().getText(R.string.interrupt));
            } else {
                countDownInterrupt.setText(getResources().getText(R.string.continuee));
            }
        } else if (clickViewId == R.id.ib_toolbar_back) {
            new AlertDialog.Builder(this)
                    .setMessage("确认返回主页")
                    .setCancelable(false)
                    .setPositiveButton("确定", (dialog, which) -> {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    })
                    .setNegativeButton("取消", (dialog, which) -> {

                    })
                    .show();
        } else if (clickViewId == R.id.fragment_word_credit_click_start) {
            startDrawer.openDrawer(GravityCompat.END);
        } else if (clickViewId == R.id.fragment_word_credit_start_add) {
            // 添加一个新的收藏夹
            View addNewCategory = getLayoutInflater().inflate(R.layout.fragment_word_credit_start_edit_new_dialog, null);
            EditText categoryTile = addNewCategory.findViewById(R.id.fragment_word_credit_start_new_title);
            EditText categoryDescribe = addNewCategory.findViewById(R.id.fragment_word_credit_start_new_describe);
            new AlertDialog.Builder(this)
                    .setView(addNewCategory)
                    .setCancelable(true)
                    .setPositiveButton("确定", (dialog, which) -> {
                        WordCategoryDTO wordCategoryDTO = new WordCategoryDetailVO();
                        wordCategoryDTO.setTitle(categoryTile.getText().toString());
                        wordCategoryDTO.setTitle(categoryDescribe.getText().toString());
                        updateUIHandler.post(() -> startSingleCategoryAdapter.addItem(wordCategoryDTO));
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                    })
                    .show();
        } else if (clickViewId == R.id.fragment_word_credit_drawer_refresh) {
            // 刷新收藏夹信息
            StaticFactory.getExecutorService().submit(() -> {
                this.startSingleCategoryAdapter = new StartSingleCategoryAdapter(this);
                ItemTouchHelper touchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(startSingleCategoryAdapter));
                startSingleCategoryAdapter.setStartDragListener(touchHelper::startDrag);
                startSingleCategoryAdapter.setStartFunctionHandler(wordFunctionHandler);
                try {
                    wordFunctionHandler.replaceAddCategory(JsonUtils.readJsonArray(WordContextPath.WORD_STAR.getPath(), WordCategoryDetailVO.class));
                } catch (IOException ignored) {
                }
                updateUIHandler.post(() -> {
                    starSingleCategory.setAdapter(startSingleCategoryAdapter);
                    touchHelper.attachToRecyclerView(starSingleCategory);
                });
            });
        } else if (clickViewId == R.id.fragment_word_credit_click_change_mode) {
            /*
            PopupWindow changeModePopupWindow = new PopupWindow(popWindowChangeModeLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            changeModePopupWindow.setOutsideTouchable(true);
            changeModePopupWindow.setFocusable(true);
            changeModePopupWindow.setAnimationStyle(R.style.pop_window_anim_style);
            changeModePopupWindow.setOnDismissListener(() -> ((ViewGroup) getParent()).removeView(popWindowChangeModeLayout));
            // PopWindow展示在某个组件的上方,这里的changeMode代表要展示在那个组件上方,popWindowChangeModeLayout代表要展示哪个组件.
            popWindowChangeModeLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int[] location = new int[2];
            changeMode.getLocationInSurface(location);
            changeModePopupWindow.showAtLocation(changeMode, Gravity.NO_GRAVITY,
                    (location[0] + changeMode.getWidth() / 2) - popWindowChangeModeLayout.getMeasuredWidth() / 2,
                    location[1] - popWindowChangeModeLayout.getMeasuredHeight());

             */

        } else if (clickViewId == R.id.fragment_word_credit_search_word) {
            // todo 跳转单词搜索界面
        } else if (clickViewId == R.id.fragment_word_credit_click_analysis_word) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(WordCreditFragment.ANALYSIS_WORD, wordFunctionHandler.getCurrentStructureWordMap());
            // 跳转当前单词的分析界面
        } else if (clickViewId == R.id.fragment_word_credit_click_save_progress) {
            // 保存当前的进度
            StaticFactory.getExecutorService().submit(() -> {
                HistoryDTOLocal historyDTOLocal = new HistoryDTOLocal();
                historyDTOLocal.setSerializeWordList(wordFunctionHandler.getAllFunctionWordList());
                historyDTOLocal.setName(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss")));
                historyDTOLocal.setOrder(System.currentTimeMillis());
                Gson gson = StaticFactory.getGson();
                String writer = gson.toJson(historyDTOLocal);
                String outputPath = "%shistory-%s.json";
                try {
                    FileUtils.writeWithExternalExist(String.format(outputPath, WordContextPath.WORD_HISTORY.getPath(),
                            UUID.randomUUID().toString().replace("-", "")), writer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            toast = Toast.makeText(this, R.string.save_success, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 500);
            toast.show();
        } else if (clickViewId == R.id.fragment_word_credit_pop_listening_write_mode) {
            wordFunctionHandler.setCurrentCreditState(CreditState.LISTENING);
            updateChangeModePopWindowState();
        } else if (clickViewId == R.id.fragment_word_credit_pop_english_translation_chinese_hearing) {
            wordFunctionHandler.setCurrentCreditState(CreditState.ENGLISH_TRANSLATION_CHINESE_HEARING);
            updateChangeModePopWindowState();
        } else if (clickViewId == R.id.fragment_word_credit_pop_english_translation_chinese_no_hearing) {
            wordFunctionHandler.setCurrentCreditState(CreditState.ENGLISH_TRANSLATION_CHINESE_NO_HEARING);
            updateChangeModePopWindowState();
        } else if (clickViewId == R.id.fragment_word_credit_pop_chinese_translation_english) {
            wordFunctionHandler.setCurrentCreditState(CreditState.CHINESE_TRANSLATION_ENGLISH);
            updateChangeModePopWindowState();
        } else if (clickViewId == R.id.fragment_word_credit_pop_only_credit) {
            wordFunctionHandler.setCurrentCreditState(CreditState.CREDIT);
            updateChangeModePopWindowState();
        } else if (clickViewId == R.id.fragment_word_credit_pop_hide_pronoun) {
            wordFunctionHandler.setHidePronoun(!wordFunctionHandler.isHidePronoun());
            updateChangeModePopWindowState();
        } else if (clickViewId == R.id.fragment_word_credit_button_flag_green) {
            if (changingChameleon) {
                changingChameleon = false;
                wordFunctionHandler.setChameleon(FlagColor.GREEN);
                this.nextWord.getForeground().setTint(getResources().getColor(R.color.theme_color, null));
                this.previousWord.getForeground().setTint(getResources().getColor(R.color.theme_color, null));
                wordCount.setText(String.valueOf(wordFunctionHandler.getChameleonSize()));
                currentIndexTextView.setText(String.valueOf(wordFunctionHandler.getChameleonOrder()));
                return;
            }
            if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.GREEN)) {
                findViewById(R.id.fragment_word_credit_view_flag_green).setAlpha(0.0f);
            } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.GREEN)) {
                findViewById(R.id.fragment_word_credit_view_flag_green).setAlpha(1.0f);
            }
        } else if (clickViewId == R.id.fragment_word_credit_button_flag_red) {
            if (changingChameleon) {
                changingChameleon = false;
                wordFunctionHandler.setChameleon(FlagColor.RED);
                this.nextWord.getForeground().setTint(getResources().getColor(android.R.color.holo_red_dark, null));
                this.previousWord.getForeground().setTint(getResources().getColor(android.R.color.holo_red_dark, null));
                wordCount.setText(String.valueOf(wordFunctionHandler.getChameleonSize()));
                currentIndexTextView.setText(String.valueOf(wordFunctionHandler.getChameleonOrder()));
                return;
            }
            if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.RED)) {
                findViewById(R.id.fragment_word_credit_view_flag_red).setAlpha(0.0f);
                recordFlagColor.remove(FlagColor.RED);
            } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.RED)) {
                findViewById(R.id.fragment_word_credit_view_flag_red).setAlpha(1.0f);
                recordFlagColor.add(FlagColor.RED);
            }
        } else if (clickViewId == R.id.fragment_word_credit_button_flag_orange) {
            if (changingChameleon) {
                changingChameleon = false;
                wordFunctionHandler.setChameleon(FlagColor.ORANGE);
                this.nextWord.getForeground().setTint(getResources().getColor(android.R.color.holo_orange_dark, null));
                this.previousWord.getForeground().setTint(getResources().getColor(android.R.color.holo_orange_dark, null));
                wordCount.setText(String.valueOf(wordFunctionHandler.getChameleonSize()));
                currentIndexTextView.setText(String.valueOf(wordFunctionHandler.getChameleonOrder()));
                return;
            }
            if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.ORANGE)) {
                findViewById(R.id.fragment_word_credit_view_flag_orange).setAlpha(0.0f);
                recordFlagColor.remove(FlagColor.ORANGE);
            } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.ORANGE)) {
                findViewById(R.id.fragment_word_credit_view_flag_orange).setAlpha(1.0f);
                recordFlagColor.add(FlagColor.ORANGE);
            }
        } else if (clickViewId == R.id.fragment_word_credit_button_flag_yellow) {
            if (changingChameleon) {
                changingChameleon = false;
                wordFunctionHandler.setChameleon(FlagColor.YELLOW);
                this.nextWord.getForeground().setTint(getResources().getColor(R.color.holo_yellow_dark, null));
                this.previousWord.getForeground().setTint(getResources().getColor(R.color.holo_yellow_dark, null));
                wordCount.setText(String.valueOf(wordFunctionHandler.getChameleonSize()));
                currentIndexTextView.setText(String.valueOf(wordFunctionHandler.getChameleonOrder()));
                return;
            }
            if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.YELLOW)) {
                findViewById(R.id.fragment_word_credit_view_flag_yellow).setAlpha(0.0f);
                recordFlagColor.remove(FlagColor.YELLOW);
            } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.YELLOW)) {
                findViewById(R.id.fragment_word_credit_view_flag_yellow).setAlpha(1.0f);
                recordFlagColor.add(FlagColor.YELLOW);
            }
        } else if (clickViewId == R.id.fragment_word_credit_button_flag_blue) {
            if (changingChameleon) {
                changingChameleon = false;
                wordFunctionHandler.setChameleon(FlagColor.BLUE);
                this.nextWord.getForeground().setTint(getResources().getColor(android.R.color.holo_blue_dark, null));
                this.previousWord.getForeground().setTint(getResources().getColor(android.R.color.holo_blue_dark, null));
                wordCount.setText(String.valueOf(wordFunctionHandler.getChameleonSize()));
                currentIndexTextView.setText(String.valueOf(wordFunctionHandler.getChameleonOrder()));
                return;
            }
            if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.BLUE)) {
                findViewById(R.id.fragment_word_credit_view_flag_blue).setAlpha(0.0f);
                recordFlagColor.remove(FlagColor.BLUE);
            } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.BLUE)) {
                findViewById(R.id.fragment_word_credit_view_flag_blue).setAlpha(1.0f);
                recordFlagColor.add(FlagColor.BLUE);
            }
        } else if (clickViewId == R.id.fragment_word_credit_button_flag_cyan) {
            if (changingChameleon) {
                changingChameleon = false;
                wordFunctionHandler.setChameleon(FlagColor.CYAN);
                this.nextWord.getForeground().setTint(getResources().getColor(R.color.holo_cyan_dark, null));
                this.previousWord.getForeground().setTint(getResources().getColor(R.color.holo_cyan_dark, null));
                wordCount.setText(String.valueOf(wordFunctionHandler.getChameleonSize()));
                currentIndexTextView.setText(String.valueOf(wordFunctionHandler.getChameleonOrder()));
                return;
            }
            if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.CYAN)) {
                findViewById(R.id.fragment_word_credit_view_flag_cyan).setAlpha(0.0f);
                recordFlagColor.remove(FlagColor.CYAN);
            } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.CYAN)) {
                findViewById(R.id.fragment_word_credit_view_flag_cyan).setAlpha(1.0f);
                recordFlagColor.add(FlagColor.CYAN);
            }
        } else if (clickViewId == R.id.fragment_word_credit_button_flag_purple) {
            if (changingChameleon) {
                changingChameleon = false;
                wordFunctionHandler.setChameleon(FlagColor.PURPLE);
                this.nextWord.getForeground().setTint(getResources().getColor(android.R.color.holo_purple, null));
                this.previousWord.getForeground().setTint(getResources().getColor(android.R.color.holo_purple, null));
                wordCount.setText(String.valueOf(wordFunctionHandler.getChameleonSize()));
                currentIndexTextView.setText(String.valueOf(wordFunctionHandler.getChameleonOrder()));
                return;
            }
            if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.PURPLE)) {
                findViewById(R.id.fragment_word_credit_view_flag_purple).setAlpha(0.0f);
                recordFlagColor.remove(FlagColor.PURPLE);
            } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.PURPLE)) {
                findViewById(R.id.fragment_word_credit_view_flag_purple).setAlpha(1.0f);
                recordFlagColor.add(FlagColor.PURPLE);
            }
        } else if (clickViewId == R.id.fragment_word_credit_button_flag_pink) {
            if (changingChameleon) {
                changingChameleon = false;
                wordFunctionHandler.setChameleon(FlagColor.PINK);
                this.nextWord.getForeground().setTint(getResources().getColor(R.color.holo_pink_dark, null));
                this.previousWord.getForeground().setTint(getResources().getColor(R.color.holo_pink_dark, null));
                wordCount.setText(String.valueOf(wordFunctionHandler.getChameleonSize()));
                currentIndexTextView.setText(String.valueOf(wordFunctionHandler.getChameleonOrder()));
                return;
            }
            if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.PINK)) {
                findViewById(R.id.fragment_word_credit_view_flag_pink).setAlpha(0.0f);
                recordFlagColor.remove(FlagColor.PINK);
            } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.PINK)) {
                findViewById(R.id.fragment_word_credit_view_flag_pink).setAlpha(1.0f);
                recordFlagColor.add(FlagColor.PINK);
            }
        } else if (clickViewId == R.id.fragment_word_credit_button_flag_gray) {
            if (changingChameleon) {
                changingChameleon = false;
                wordFunctionHandler.setChameleon(FlagColor.GRAY);
                this.nextWord.getForeground().setTint(getResources().getColor(R.color.dark_gray, null));
                this.previousWord.getForeground().setTint(getResources().getColor(R.color.dark_gray, null));
                wordCount.setText(String.valueOf(wordFunctionHandler.getChameleonSize()));
                currentIndexTextView.setText(String.valueOf(wordFunctionHandler.getChameleonOrder()));
                return;
            }
            if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.GRAY)) {
                findViewById(R.id.fragment_word_credit_view_flag_gray).setAlpha(0.0f);
                recordFlagColor.remove(FlagColor.GRAY);
            } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.GRAY)) {
                findViewById(R.id.fragment_word_credit_view_flag_gray).setAlpha(1.0f);
                recordFlagColor.add(FlagColor.GRAY);
            }
        } else if (clickViewId == R.id.fragment_word_credit_button_flag_black) {
            if (changingChameleon) {
                changingChameleon = false;
                wordFunctionHandler.setChameleon(FlagColor.BLACK);
                this.nextWord.getForeground().setTint(getResources().getColor(android.R.color.black, null));
                this.previousWord.getForeground().setTint(getResources().getColor(android.R.color.black, null));
                wordCount.setText(String.valueOf(wordFunctionHandler.getChameleonSize()));
                currentIndexTextView.setText(String.valueOf(wordFunctionHandler.getChameleonOrder()));
                return;
            }
            if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.BLACK)) {
                findViewById(R.id.fragment_word_credit_view_flag_black).setAlpha(0.0f);
                recordFlagColor.remove(FlagColor.BLACK);
            } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.BLACK)) {
                findViewById(R.id.fragment_word_credit_view_flag_black).setAlpha(1.0f);
                recordFlagColor.add(FlagColor.BLACK);
            }
        } else if (clickViewId == R.id.fragment_word_credit_button_flag_brown) {
            if (changingChameleon) {
                changingChameleon = false;
                wordFunctionHandler.setChameleon(FlagColor.BROWN);
                this.nextWord.getForeground().setTint(getResources().getColor(R.color.halo_brown_dark, null));
                wordCount.setText(String.valueOf(wordFunctionHandler.getChameleonSize()));
                currentIndexTextView.setText(String.valueOf(wordFunctionHandler.getChameleonOrder()));
            }
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int itemId = v.getId();
        if (itemId == R.id.fragment_word_card_view_get_answer && !lockAnswerLocation) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 获得按下时的相对位置
                answerChangeDY = getAnswerParentLayout.getY() - event.getRawY();
                answerChangeDX = getAnswerParentLayout.getX() - event.getRawX();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float newY = event.getRawY() + answerChangeDY;
                float newX = event.getRawX() + answerChangeDX;
                getAnswerParentLayout.setX(newX);
                getAnswerParentLayout.setY(newY);
            }
        }
        if (itemId == R.id.fragment_word_card_view_get_answer && enableSelectFunction) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                answerDY = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE && longClickAnswer) {
                float distinct = event.getY() - answerDY;
                if (Math.abs(distinct) > 10) {
                    // 展开右侧的旗帜列表
                    if (!openFlagChange) {
                        if (AnimationUtil.with().endMoveToViewLocation(flagChangeArea, 500)) {
                            openFlagChangeAreaFlush();
                            openFlagChange = !openFlagChange;
                        }
                    }
                    int userMove = (int) (distinct / answerDegree) + 4;
                    if (userMove < 0) {
                        userMove = 0;
                    } else if (userMove > 8) {
                        userMove = 8;
                    }
                    // 修改样式
                    selectList.forEach(imageButton -> imageButton.setBackground(this.getDrawable(R.drawable.style_image_padding)));
                    this.currentSelectFlagButton = selectList.get(userMove);
                    this.currentSelectFlagButton.setBackground(this.getDrawable(R.drawable.style_image_padding_selective));
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP && longClickAnswer) {
                longClickAnswer = false;
                this.currentSelectFlagButton.setBackground(this.getDrawable(R.drawable.style_image_padding));
                this.currentSelectFlagButton.performClick();
            }
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.fragment_word_card_view_get_answer && enableSelectFunction) {
            longClickAnswer = true;
            // 马达震动提醒用户
            VibrationEffect waveform = VibrationEffect.createWaveform(new long[]{100}, -1);
            vibrator.vibrate(waveform);
        } else if (itemId == R.id.fragment_word_credit_click_projector) {
            wordFunctionHandler.startProjector(null);
            projectorHint.setText("");
        }
        return false;
    }


    /**
     * 隐藏所有暂时不必要出现的UI
     */
    private void emptyUI() {
        // 隐藏单词的附加显示内容(注意不包含形容词、副词等内容的隐藏)
        sourceWord.setText("");
    }

    /**
     * 更新弹出窗口中所有按钮的状态
     */
    private void updateChangeModePopWindowState() {
        this.listeningWriteMode.setBackground(null);
        this.englishTranslationChineseModeHearing.setBackground(null);
        this.englishTranslationChineseModeNoHearing.setBackground(null);
        this.chineseTranslationEnglish.setBackground(null);
        this.onlyCreditMode.setBackground(null);
        this.hideProNoun.setBackground(null);
        CreditState currentCreditState = wordFunctionHandler.getCurrentCreditState();
        if (currentCreditState == CreditState.ENGLISH_TRANSLATION_CHINESE_HEARING) {
            this.englishTranslationChineseModeHearing.setBackground(ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.fragment_word_credit_pop_window_change_mode,
                    null));
        } else if (currentCreditState == CreditState.ENGLISH_TRANSLATION_CHINESE_NO_HEARING) {
            this.englishTranslationChineseModeNoHearing.setBackground(ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.fragment_word_credit_pop_window_change_mode,
                    null));
        } else if (currentCreditState == CreditState.CHINESE_TRANSLATION_ENGLISH) {
            this.chineseTranslationEnglish.setBackground(ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.fragment_word_credit_pop_window_change_mode,
                    null));
        } else if (currentCreditState == CreditState.LISTENING) {
            this.listeningWriteMode.setBackground(ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.fragment_word_credit_pop_window_change_mode,
                    null));
        } else if (currentCreditState == CreditState.CREDIT) {
            this.onlyCreditMode.setBackground(ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.fragment_word_credit_pop_window_change_mode,
                    null));
        }
        if (wordFunctionHandler.isHidePronoun()) {
            this.hideProNoun.setBackground(ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.fragment_word_credit_pop_window_change_mode,
                    null));
        }
    }

    /**
     * 按模式展示某个单词.
     * 展示单词是一种状态,随着单词的变化,页面的UI也要跟随变化.
     *
     * @param previousWord 上一个单词(当前正在背诵的单词)
     * @param wordDTOLocal 待被展示的单词
     */
    private void reciteWord(WordDTOLocal previousWord, WordDTOLocal wordDTOLocal) {
        // 异步写入单词背诵记录
        StaticFactory.getExecutorService().submit(() -> {
            // 先清除增量,只要当前看过该单词就算清除
            AddWordReViewParamLocal addWordReViewParamLocal = new AddWordReViewParamLocal();
            addWordReViewParamLocal.setId(Math.toIntExact(previousWord.getId()));
            if (userCreditStyle.isReview()) {
                wordSupplementReviewHandler.deleteWordReView(addWordReViewParamLocal.getId());
            }
            // 正常全量增加
            AddWordAnalysisParamLocal addWordAnalysisParamLocal = new AddWordAnalysisParamLocal();
            addWordAnalysisParamLocal.setId(Math.toIntExact(previousWord.getId()));
            addWordAnalysisParamLocal.setWordFlag(recordFlagColor);
            addWordAnalysisParamLocal.setCreateTimestamp(System.currentTimeMillis());
            wordAnalysisHandler.insertWordAnalysis(addWordAnalysisParamLocal);
            // 当然也可以再把当前单词增加到末尾
            addWordReViewParamLocal.setWordFlag(recordFlagColor);
            wordSupplementReviewHandler.insertWordReView(addWordReViewParamLocal);
            // 每次记录之后都要清除一下
            recordFlagColor.clear();
        });
        // 更新UI相关
        updateUIHandler.post(() -> {
            switch (wordFunctionHandler.getCurrentCreditState()) {
                case LISTENING:
                    // 听写模式只播放音频
                    chineseAnswerHandler.gone();
                    playWordAudio(wordDTOLocal);
                    break;
                case ENGLISH_TRANSLATION_CHINESE_HEARING:
                    // 先展示单词的所有信息,然后将单词的中文意思进行隐藏,再将单词额外信息进行隐藏(必须在UI线程中隐藏)
                    visibleWordAllMessage(wordDTOLocal);
                    chineseAnswerHandler.gone();
                    playWordAudio(wordDTOLocal);
                    break;
                case ENGLISH_TRANSLATION_CHINESE_NO_HEARING:
                    // 先展示单词的所有信息,然后将单词的中文意思进行隐藏,再将单词额外信息进行隐藏(必须在UI线程中隐藏)
                    visibleWordAllMessage(wordDTOLocal);
                    chineseAnswerHandler.gone();
                    break;
                case CHINESE_TRANSLATION_ENGLISH:
                    // 先展示所有单词信息,然后将英文原文和音标进行隐藏;还要隐藏短语
                    // 这里设置模式是为了防止播放音频
                    wordFunctionHandler.setCurrentCreditState(CreditState.CREDIT);
                    String phaseValue = wordDTOLocal.getValue().get(EnglishStructure.PHRASE);
                    wordDTOLocal.getValue().remove(EnglishStructure.PHRASE);
                    visibleWordAllMessage(wordDTOLocal);
                    wordDTOLocal.getValue().put(EnglishStructure.PHRASE, phaseValue);
                    sourceWord.setText("");
                    wordFunctionHandler.setCurrentCreditState(CreditState.CHINESE_TRANSLATION_ENGLISH);
                    break;
                case CREDIT:
                    // 不隐藏任何信息
                    visibleWordAllMessage(wordDTOLocal);
                    playWordAudio(wordDTOLocal);
                    break;
            }
            /*
            不管是什么状态,如果当前旗帜是打开的,那么都需要刷新旗帜(颜色标记)的状态.
            不管是什么状态,都需要显示当前背诵的位置和总的单词个数.
            不管什么状态,都需要更新drawer里面单词的内容
             */
            chineseAnswerAdapterDrawer.addItem(wordDTOLocal);
            // 设置右侧展开列表单词的原文
            Optional.ofNullable(wordDTOLocal.getValue().get(EnglishStructure.WORD_ORIGIN))
                    .ifPresent(wordDTOS -> sourceWordDrawer
                            .setText(wordDTOS));
            // 设置短语
            String phraseTranslation = Optional.ofNullable(wordDTOLocal.getValue().get(EnglishStructure.PHRASE_TRANSLATION))
                    .orElse("");
            String phraseTxt = wordDTOLocal.getValue().get(EnglishStructure.PHRASE);
            if (phraseTxt != null) {
                phraseAnswerDrawer.setText(getString(R.string.phrase_with_translation, phraseTxt, phraseTranslation));
                phraseHintDrawer.setVisibility(View.VISIBLE);
                phraseAnswerDrawer.setVisibility(View.VISIBLE);
            } else {
                phraseHintDrawer.setVisibility(View.GONE);
                phraseAnswerDrawer.setVisibility(View.GONE);
            }

            wordCount.setText(String.valueOf(wordFunctionHandler.getChameleonSize()));
            currentIndexTextView.setText(String.valueOf(wordFunctionHandler.getChameleonOrder()));
            chameleonCount.setText(String.format("%s/%s", wordFunctionHandler.getCurrentIndex() + 1, wordFunctionHandler.size()));
            if (openFlagChange) {
                openFlagChangeAreaFlush();
            } else {
                closeFlagChangeAreaFlush();
            }
        });
    }


    /**
     * 关闭旗帜改变区域显示时调用
     */
    private void closeFlagChangeAreaFlush() {
        clickFlagImageView.setImageResource(R.drawable.flag_close);
        viewFlagArea.setPadding(0, 0, 5, 0);
        // 最开始除了绿色旗帜外,所有旗帜的对应的View全部置为不显示
        for (int i = 0; i < viewFlagArea.getChildCount(); i++) {
            viewFlagArea.getChildAt(i).setVisibility(View.GONE);
            ((LinearLayout.LayoutParams) viewFlagArea.getChildAt(i).getLayoutParams()).setMargins(0, 0, 0, 0);
        }
        for (FlagColor flagColor : wordFunctionHandler.getCurrentWordFlagColor()) {
            viewFlagArea.getChildAt(flagColor.ordinal()).setVisibility(View.VISIBLE);
            viewFlagArea.getChildAt(flagColor.ordinal()).setAlpha(1.0f);
        }
        // 将所有选择框复原
        selectList.forEach(imageButton -> imageButton.setBackground(ContextCompat.getDrawable(this, R.drawable.style_image_padding)));
        blueToothMoveIndex = -1;
    }

    /**
     * 打开旗帜改变区域显示时调用
     */
    private void openFlagChangeAreaFlush() {
        clickFlagImageView.setImageResource(R.drawable.flag_open);
        viewFlagArea.setPadding(5, 0, 0, 0);
        // 最开始除了绿色旗帜外,所有旗帜的对应的View全部置为不显示
        for (int i = 0; i < viewFlagArea.getChildCount(); i++) {
            View child = viewFlagArea.getChildAt(i);
            child.setVisibility(View.VISIBLE);
            child.setAlpha(0.0f);
            // 10dp转px的方法
            ((LinearLayout.LayoutParams) child.getLayoutParams()).setMargins(0, DPUtils.dp2px(10), 0, DPUtils.dp2px(10));
        }
        for (FlagColor flagColor : wordFunctionHandler.getCurrentWordFlagColor()) {
            viewFlagArea.getChildAt(flagColor.ordinal()).setAlpha(1.0f);
        }
    }

    /**
     * 显示当前单词的所有信息,<b>具体当前要根据状态隐藏哪些信息有调用者来处理.</b><br>
     * 该方法展示的是最全面的信息,所有隐藏信息都会被显示,但是单词没有的性质(比如没有某个中文意思)那么没有的内容不会展示.
     */
    private void visibleWordAllMessage(WordDTOLocal currentWord) {
        // 如果当前是中译英则播放单词,并且移除短语
        if (wordFunctionHandler.getCurrentCreditState() == CreditState.CHINESE_TRANSLATION_ENGLISH) {
            playWordAudio(currentWord);
        }
        // 如果是省略短语则直接移除短语
        String phaseValue = currentWord.getValue().get(EnglishStructure.PHRASE);
        if (wordFunctionHandler.isHidePronoun()) {
            currentWord.getValue().remove(EnglishStructure.PHRASE);
        }
        // 设置单词原文
        Optional.ofNullable(currentWord.getValue().get(EnglishStructure.WORD_ORIGIN))
                .ifPresent(wordDTOS -> sourceWord
                        .setText(wordDTOS));
        chineseAnswerHandler.showWordChineseMessage(currentWord);
        currentWord.getValue().put(EnglishStructure.PHRASE, phaseValue);
    }

    /**
     * 放映机模式使用的定时任务
     *
     * @return
     */
    private Runnable timedTasks() {
        return () -> {
            while (timeTaskRunning) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ProjectorDTOLocal calculateCountdown = wordFunctionHandler.calculateCountdown();
                if (calculateCountdown == null) {
                    continue;
                }
                projectorFade = ++projectorFade % 41;
                updateUIHandler.post(() -> {
                    // 如果已经倒计时结束,播放通知音效
                    if (projectorFinish && projectorFade == 0) {
                        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone ringtone = RingtoneManager.getRingtone(this, notificationUri);
                        if (!ringtone.isPlaying()) {
                            ringtone.play();
                        }
                        countDownTop.setVisibility(View.GONE);
                        countDownBottom.setVisibility(View.GONE);
                    }
                    // 任务已经完成,不执行后续代码
                    if (projectorFinish) {
                        return;
                    }
                    if (projectorFade == 25) {
                        countDownInterrupt.setVisibility(View.GONE);
                        countDownExit.setVisibility(View.GONE);
                    }
                    if (!calculateCountdown.isRunning()) {
                        return;
                    }
                    long minute = (calculateCountdown.getRemainingTime() / 1000) / 60;
                    long second = (calculateCountdown.getRemainingTime() / 1000) % 60;
                    if ((minute % 2 == 0 && second <= 30) ||
                            (minute % 2 == 1 && second >= 30)) {
                        countDownTop.setVisibility(View.VISIBLE);
                        countDownBottom.setVisibility(View.GONE);
                    } else {
                        countDownTop.setVisibility(View.GONE);
                        countDownBottom.setVisibility(View.VISIBLE);
                    }
                    countDownTop.setText(String.format("%02d:%02d", minute, second));
                    countDownBottom.setText(String.format("%02d:%02d", minute, second));
                    // 倒计时结束,播放通知
                    projectorFinish = calculateCountdown.getRemainingTime() == 0;
                });

            }
        };
    }

    /**
     * 播放一个单词的音频信息
     *
     * @param tobePlayWord 待播放音频的单词
     */
    private void playWordAudio(WordDTOLocal tobePlayWord) {
        File file = new File(AnyLanguageWordProperties.getExternalFilesDir(),
                WordContextPath.WORD_AUDIO.getPath() + tobePlayWord.getAudioPath());
        if (!file.exists()) {
            return;
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void bindView() {
        this.updateUIHandler = new Handler();
        this.parentView = findViewById(R.id.fragment_word_credit_parent);
        this.popMoreFunction = findViewById(R.id.fragment_word_credit_pop_more_function);
        this.moreFunctionHorizontalScrollView = findViewById(R.id.fragment_word_credit_more_function_horizontal_scroll_view);
        this.nextWord = findViewById(R.id.fragment_word_credit_next_word);
        this.sourceWord = findViewById(R.id.text_view_source_word);
        this.previousWord = findViewById(R.id.fragment_word_credit_previous_word);
        this.getAnswer = findViewById(R.id.fragment_word_card_view_get_answer);
        this.getAnswerParentLayout = findViewById(R.id.fragment_word_linear_layout_get_answer);
        this.currentIndexTextView = findViewById(R.id.fragment_word_credit_current_index);
        this.wordCount = findViewById(R.id.fragment_word_credit_word_count);
        this.jumpNextWord = findViewById(R.id.fragment_word_credit_jump_next);
        this.flagChangeArea = findViewById(R.id.fragment_word_credit_change_flag_area);
        this.clickFlag = findViewById(R.id.fragment_word_credit_click_flag);
        this.clickFlagImageView = findViewById(R.id.fragment_word_credit_imageview_click_flag);
        this.viewFlagArea = findViewById(R.id.fragment_word_credit_view_flag_area);
        this.chameleonMode = findViewById(R.id.fragment_word_credit_click_chameleon_mode);
        this.swingSwitch = findViewById(R.id.fragment_word_credit_click_swing_switch);
        this.swingSwitchImageView = findViewById(R.id.fragment_word_credit_imageview_swing_switch);
        this.lockAnswer = findViewById(R.id.fragment_word_credit_click_lock_answer);
        this.lockAnswerImageView = findViewById(R.id.fragment_word_credit_imageview_lock_answer);
        this.blueTooth = findViewById(R.id.fragment_word_credit_click_blue_tooth);
        this.blueToothImageView = findViewById(R.id.fragment_word_credit_imageview_blue_tooth);
        this.shuffle = findViewById(R.id.fragment_word_credit_click_shuffle);
        this.chameleonImageView = findViewById(R.id.fragment_word_credit_imageview_chameleon);
        this.sectionImageView = findViewById(R.id.fragment_word_credit_imageview_section);
        this.quickPositionImageView = findViewById(R.id.fragment_word_credit_imageview_quick_position);
        this.shuffleImageView = findViewById(R.id.fragment_word_credit_imageview_shuffle);
        this.section = findViewById(R.id.fragment_word_credit_click_section);
        this.projector = findViewById(R.id.fragment_word_credit_click_projector);
        this.projectorHint = findViewById(R.id.fragment_word_credit_projector_hint);
        this.popBackStack = findViewById(R.id.ib_toolbar_back);
        this.changeMode = findViewById(R.id.fragment_word_credit_click_change_mode);
        this.quickPosition = findViewById(R.id.fragment_word_credit_click_quick_position);
        this.popWindowChangeModeLayout = (CardView) getLayoutInflater().inflate(R.layout.fragment_word_credit_popwindow_change_mode, null);
        this.listeningWriteMode = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_listening_write_mode);
        this.englishTranslationChineseModeHearing = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_english_translation_chinese_hearing);
        this.englishTranslationChineseModeNoHearing = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_english_translation_chinese_no_hearing);
        this.chineseTranslationEnglish = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_chinese_translation_english);
        this.onlyCreditMode = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_only_credit);
        this.hideProNoun = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_hide_pronoun);
        this.playWord = findViewById(R.id.fragment_word_credit_play_word);
        this.startDrawer = findViewById(R.id.fragment_word_credit_start_drawer);
        this.start = findViewById(R.id.fragment_word_credit_click_start);
        this.starSingleCategory = findViewById(R.id.fragment_word_credit_start_category_recycler);
        this.addNewStartCategory = findViewById(R.id.fragment_word_credit_start_add);
        this.searchWord = findViewById(R.id.fragment_word_credit_search_word);
        this.saveProgress = findViewById(R.id.fragment_word_credit_click_save_progress);
        this.chameleonCount = findViewById(R.id.fragment_word_credit_chameleon_word_count);
        this.wordAnalysis = findViewById(R.id.fragment_word_credit_click_analysis_word);
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        this.projectorParent = findViewById(R.id.fragment_word_credit_projector_parent);
        this.countDownTop = findViewById(R.id.fragment_word_credit_projector_countdown_top);
        this.countDownBottom = findViewById(R.id.fragment_word_credit_projector_countdown_bottom);
        this.countDownInterrupt = findViewById(R.id.fragment_word_credit_projector_countdown_interrupt);
        this.countDownExit = findViewById(R.id.fragment_word_credit_projector_countdown_exit);

        this.sourceWordDrawer = findViewById(R.id.fragment_word_credit_drawer_word_origin);
        this.starRefresh = findViewById(R.id.fragment_word_credit_drawer_refresh);
        this.chineseAnswerDrawer = findViewById(R.id.fragment_word_credit_drawer_chinese_answer);
        this.phraseHintDrawer = findViewById(R.id.fragment_word_credit_drawer_phrase_hint);
        this.phraseAnswerDrawer = findViewById(R.id.fragment_word_credit_drawer_phrase_answer);

        this.greenFlag = findViewById(R.id.fragment_word_credit_button_flag_green);
        this.redFlag = findViewById(R.id.fragment_word_credit_button_flag_red);
        this.orangeFlag = findViewById(R.id.fragment_word_credit_button_flag_orange);
        this.yellowFlag = findViewById(R.id.fragment_word_credit_button_flag_yellow);
        this.blueFlag = findViewById(R.id.fragment_word_credit_button_flag_blue);
        this.cyanFlag = findViewById(R.id.fragment_word_credit_button_flag_cyan);
        this.purpleFlag = findViewById(R.id.fragment_word_credit_button_flag_purple);
        this.pinkFlag = findViewById(R.id.fragment_word_credit_button_flag_pink);
        this.grayFlag = findViewById(R.id.fragment_word_credit_button_flag_gray);
        this.blackFlag = findViewById(R.id.fragment_word_credit_button_flag_black);
        this.brownFlag = findViewById(R.id.fragment_word_credit_button_flag_brown);
        this.selectList = new ArrayList<>(9);
        this.selectList.add(redFlag);
        this.selectList.add(orangeFlag);
        this.selectList.add(yellowFlag);
        this.selectList.add(blueFlag);
        this.selectList.add(cyanFlag);
        this.selectList.add(purpleFlag);
        this.selectList.add(pinkFlag);
        this.selectList.add(grayFlag);
        this.selectList.add(blackFlag);
        bindListener();
    }

    /**
     * 设置所有监听事件
     */
    private void bindListener() {
        this.popMoreFunction.setOnClickListener(this);
        this.nextWord.setOnClickListener(this);
        this.previousWord.setOnClickListener(this);
        this.getAnswer.setOnClickListener(this);
        this.jumpNextWord.setOnClickListener(this);
        this.clickFlag.setOnClickListener(this);
        this.chameleonMode.setOnClickListener(this);
        this.shuffle.setOnClickListener(this);
        this.section.setOnClickListener(this);
        this.popBackStack.setOnClickListener(this);
        this.changeMode.setOnClickListener(this);
        this.quickPosition.setOnClickListener(this);
        this.listeningWriteMode.setOnClickListener(this);
        this.englishTranslationChineseModeHearing.setOnClickListener(this);
        this.englishTranslationChineseModeNoHearing.setOnClickListener(this);
        this.chineseTranslationEnglish.setOnClickListener(this);
        this.onlyCreditMode.setOnClickListener(this);
        this.hideProNoun.setOnClickListener(this);
        this.playWord.setOnClickListener(this);
        this.start.setOnClickListener(this);
        this.addNewStartCategory.setOnClickListener(this);
        this.searchWord.setOnClickListener(this);
        this.saveProgress.setOnClickListener(this);
        this.wordAnalysis.setOnClickListener(this);
        this.starRefresh.setOnClickListener(this);
        this.swingSwitch.setOnClickListener(this);
        this.lockAnswer.setOnClickListener(this);
        this.getAnswer.setOnTouchListener(this);
        this.blueTooth.setOnClickListener(this);
        this.getAnswer.setOnLongClickListener(this);
        this.projector.setOnClickListener(this);
        this.projector.setOnLongClickListener(this);
        this.countDownInterrupt.setOnClickListener(this);
        this.countDownExit.setOnClickListener(this);
        this.projectorParent.setOnClickListener(this);

        this.greenFlag.setOnClickListener(this);
        this.redFlag.setOnClickListener(this);
        this.orangeFlag.setOnClickListener(this);
        this.yellowFlag.setOnClickListener(this);
        this.blueFlag.setOnClickListener(this);
        this.cyanFlag.setOnClickListener(this);
        this.purpleFlag.setOnClickListener(this);
        this.pinkFlag.setOnClickListener(this);
        this.grayFlag.setOnClickListener(this);
        this.blackFlag.setOnClickListener(this);
        this.brownFlag.setOnClickListener(this);
    }


}