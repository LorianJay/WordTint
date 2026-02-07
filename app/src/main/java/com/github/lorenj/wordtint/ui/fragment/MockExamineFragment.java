package com.github.lorenj.wordtint.ui.fragment;

import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.github.lorenj.wordtint.context.factory.StaticFactory;
import com.github.lorenj.wordtint.R;

import java.util.concurrent.TimeUnit;

public class MockExamineFragment extends Fragment implements View.OnClickListener {

    private View rootView, backGround, selectGround;
    private Handler updateUIHandler;
    // 功能按钮
    private TextView titleTextView, mockPolitician, mockEnglish, mockMath, mockMajor;
    private TextView interrupt, exit, remove5s, add5s, remove30s, add30s;
    // 显示
    private TextView topTime, bottomTime, subTopTime, subBottomTime;
    // 定时任务
    private volatile boolean timeTaskRunning = false;
    // 是否暂停的变量
    private volatile boolean isRunning = false;
    // 剩余的时间
    private long remainTime = 0;
    // 已经走过的时间
    private long alreadyTime = 0;
    // 上一次的时间
    private long recordTimeStamp = 0;
    // 消失的时间记录
    private long remainFadeButtonTime = 0;
    private long recordFadeButtonTimeStamp = 0;
    /**
     * activity
     */
    private FragmentActivity requireActivity;

    public MockExamineFragment() {
    }


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
        rootView = inflater.inflate(R.layout.fragment_mock_examine, container, false);
        updateUIHandler = new Handler();
        bindView();
        initView();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        // 非模拟
        if (itemId == R.id.tv_mock_examine_interrupt) {
            isRunning = !isRunning;
            if (isRunning) {
                recordTimeStamp = System.currentTimeMillis();
            }
            return;
        } else if (itemId == R.id.cl_mock_examine_background) {
            invisibleButton(View.VISIBLE);
            remainFadeButtonTime = 1000 * 5;
            return;
        } else if (itemId == R.id.tv_mock_examine_exit) {
            timeTaskRunning = false;
            isRunning = false;
            selectGround.setVisibility(View.VISIBLE);
            backGround.setVisibility(View.GONE);
            requireActivity.getWindow().setStatusBarColor(Color.WHITE);
            requireActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            return;
        } else if (itemId == R.id.tv_mock_examine_remove_5) {
            remainTime += 1000 * 5;
            alreadyTime -= 1000 * 5;
            return;
        } else if (itemId == R.id.tv_mock_examine_add_5) {
            remainTime -= 1000 * 5;
            alreadyTime += 1000 * 5;
            return;
        } else if (itemId == R.id.tv_mock_examine_remove_30) {
            remainTime += 1000 * 30;
            alreadyTime -= 1000 * 30;
            return;
        } else if (itemId == R.id.tv_mock_examine_add_30) {
            remainTime -= 1000 * 30;
            alreadyTime += 1000 * 30;
            return;
        }
        if (itemId == R.id.fragment_mock_examine_politician ||
                itemId == R.id.tv_mock_examine_math) {
            alreadyTime = 1000 * 60 * 60 * 8;
        } else if (itemId == R.id.tv_mock_examine_english ||
                itemId == R.id.tv_mock_examine_major) {
            alreadyTime = 1000 * 60 * 60 * 14;
        }
        // 启动定时任务,开始模拟
        timeTaskRunning = true;
        isRunning = true;
        StaticFactory.getExecutorService().execute(timedTasks());
        requireActivity.getWindow().setStatusBarColor(Color.BLACK);
        requireActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        remainTime = 1000 * 60 * 60 * 3;
        recordTimeStamp = System.currentTimeMillis();
        remainFadeButtonTime = 1000 * 5;
        selectGround.setVisibility(View.GONE);
        backGround.setVisibility(View.VISIBLE);
    }

    /**
     * 计时任务
     */
    private Runnable timedTasks() {
        return () -> {
            while (timeTaskRunning) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 计算按钮消失的时间
                long currentTime = System.currentTimeMillis();
                remainFadeButtonTime -= (currentTime - recordFadeButtonTimeStamp);
                recordFadeButtonTimeStamp = currentTime;
                // 计算考试剩余的时间
                calculateRemainTime();
                updateUIHandler.post(() -> {
                    // 如果按钮需要消失了
                    if (remainFadeButtonTime < 0) {
                        invisibleButton(View.GONE);
                        if(!isRunning){
                            topTime.setVisibility(View.GONE);
                            subTopTime.setVisibility(View.GONE);
                            bottomTime.setVisibility(View.GONE);
                            subBottomTime.setVisibility(View.GONE);
                            return;
                        }
                    }
                    // 如果已经倒计时结束,播放通知音效
                    if (remainTime == 0) {
                        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone ringtone = RingtoneManager.getRingtone(getContext(), notificationUri);
                        if (!ringtone.isPlaying()) {
                            ringtone.play();
                        }
                    }
                    // 剩余时间
                    long hourRemain = remainTime / 1000 / 60 / 60;
                    long minuteRemain = remainTime / 1000 / 60 % 60;
                    long secondRemain = remainTime / 1000 % 60 ;
                    // 已经走过的时间
                    long hourAlready = alreadyTime / 1000 / 60 / 60;
                    long minuteAlready = alreadyTime / 1000 / 60 % 60;
                    long secondAlready = alreadyTime / 1000 % 60;

                    if ((minuteRemain % 2 == 0 && secondRemain <= 30) ||
                            (minuteRemain % 2 == 1 && secondRemain >= 30)) {
                        topTime.setVisibility(View.VISIBLE);
                        subTopTime.setVisibility(View.VISIBLE);
                        bottomTime.setVisibility(View.GONE);
                        subBottomTime.setVisibility(View.GONE);
                    } else {
                        topTime.setVisibility(View.GONE);
                        subTopTime.setVisibility(View.GONE);
                        bottomTime.setVisibility(View.VISIBLE);
                        subBottomTime.setVisibility(View.VISIBLE);
                    }
                    topTime.setText(String.format("%02d:%02d:%02d", hourAlready , minuteAlready, secondAlready));
                    bottomTime.setText(String.format("%02d:%02d:%02d", hourAlready , minuteAlready, secondAlready));
                    subTopTime.setText(String.format("%02d:%02d:%02d", hourRemain, minuteRemain, secondRemain));
                    subBottomTime.setText(String.format("%02d:%02d:%02d", hourRemain, minuteRemain, secondRemain));
                });

            }
        };
    }

    /**
     * 计算剩余时间
     */
    public void calculateRemainTime() {
        if (!isRunning) return;
        long currentTimeMillis = System.currentTimeMillis();
        remainTime -= currentTimeMillis - recordTimeStamp;
        alreadyTime += currentTimeMillis - recordTimeStamp;
        recordTimeStamp = currentTimeMillis;
        if (remainTime <= 0) remainTime = 0;
    }

    /**
     * 隐藏所有按钮
     */
    private void invisibleButton(final int visible) {
        this.interrupt.setVisibility(visible);
        this.exit.setVisibility(visible);
        this.remove5s.setVisibility(visible);
        this.add5s.setVisibility(visible);
        this.remove30s.setVisibility(visible);
        this.add30s.setVisibility(visible);
    }

    private void initView() {
        // 先隐藏标题信息
        this.titleTextView.setText("模拟考试");
        this.requireActivity = requireActivity();

        this.mockPolitician.setOnClickListener(this);
        this.mockEnglish.setOnClickListener(this);
        this.mockMath.setOnClickListener(this);
        this.mockMajor.setOnClickListener(this);
        this.backGround.setOnClickListener(this);
        this.interrupt.setOnClickListener(this);
        this.exit.setOnClickListener(this);
        this.remove5s.setOnClickListener(this);
        this.add5s.setOnClickListener(this);
        this.remove30s.setOnClickListener(this);
        this.add30s.setOnClickListener(this);
    }

    private void bindView() {
        this.titleTextView = rootView.findViewById(R.id.toolbar_title);
        this.selectGround = rootView.findViewById(R.id.fragment_mock_examine_select);
        this.mockPolitician = rootView.findViewById(R.id.tv_mock_examine_politician);
        this.mockEnglish = rootView.findViewById(R.id.tv_mock_examine_english);
        this.mockMath = rootView.findViewById(R.id.tv_mock_examine_math);
        this.mockMajor = rootView.findViewById(R.id.tv_mock_examine_major);
        this.topTime = rootView.findViewById(R.id.tv_mock_examine_top_time);
        this.subTopTime = rootView.findViewById(R.id.tv_mock_examine_sub_top_time);
        this.bottomTime = rootView.findViewById(R.id.tv_mock_examine_bottom_time);
        this.subBottomTime = rootView.findViewById(R.id.tv_mock_examine_sub_bottom_time);
        this.backGround = rootView.findViewById(R.id.cl_mock_examine_background);
        this.interrupt = rootView.findViewById(R.id.tv_mock_examine_interrupt);
        this.exit = rootView.findViewById(R.id.tv_mock_examine_exit);
        this.remove5s = rootView.findViewById(R.id.tv_mock_examine_remove_5);
        this.add5s = rootView.findViewById(R.id.tv_mock_examine_add_5);
        this.remove30s = rootView.findViewById(R.id.tv_mock_examine_remove_30);
        this.add30s = rootView.findViewById(R.id.tv_mock_examine_add_30);
    }


}