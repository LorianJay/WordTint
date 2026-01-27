package com.github.lorenj.wordtint.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.AnyLanguageWordProperties;
import com.github.lorenj.wordtint.context.UserSettings;
import com.github.lorenj.wordtint.context.pathsystem.document.UserInfoPath;
import com.github.lorenj.wordtint.ui.activity.WelcomeActivity;
import com.github.lorenj.wordtint.ui.adapter.BottomViewAdapter;
import com.github.lorenj.wordtint.ui.adapter.listener.NavigationItemSelectListener;
import com.github.lorenj.wordtint.ui.fragment.CreditFragment;
import com.github.lorenj.wordtint.ui.fragment.HistoryFragment;
import com.github.lorenj.wordtint.ui.fragment.RankFragment;
import com.github.lorenj.wordtint.utils.JsonUtils;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnLongClickListener, NavigationBarView.OnItemSelectedListener {

    private UserSettings userSettings;
    private BottomNavigationView viewPageChangeNavigationView;
    private ViewPager2 viewPager;
    private MenuItem nowSelectMenuItem;
    private ArrayList<Fragment> listFragment;
    private volatile int position = 0;
    private final Fragment creditFragment = new CreditFragment(), rankFragment = new RankFragment(), analysisFragment = new HistoryFragment();
    private BottomNavigationItemView bottomRecite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 状态栏反色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        // 初始化外部存储路径
        AnyLanguageWordProperties.setExternalFilesDir(getExternalFilesDir(""));
        // 申请权限
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        // 得到用户信息文件
        try {
            userSettings = JsonUtils.readJson(UserInfoPath.USER_SETTINGS.getPath(), UserSettings.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 如果当前还没有同意用户协议则跳转到用户协议界面
        userAssertCheck();
        bindView();
        initView();
    }

    /**
     * 点击不同底部栏的按钮时调用
     *
     * @param item The selected item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        int position = -1;
        if (itemId == R.id.fragment_main_bottom_recite) {
            position = 0;
        } else if (itemId == R.id.fragment_main_bottom_hearing) {
            position = 1;
        } else if (itemId == R.id.fragment_main_bottom_analysis) {
            position = 2;
        }
        viewPager.setCurrentItem(position, false);
        // 如果当前点击的目标页面就是当前页面则触发回调事件
        if (this.position == position) {
            ((NavigationItemSelectListener) listFragment.get(position)).onClickCurrentPage(item);
        }
        this.position = position;
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.fragment_main_bottom_recite) {
            // todo 跳转到搜索页面
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Fragment fragment = getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager().getPrimaryNavigationFragment();
        if (fragment instanceof KeyEvent.Callback) {
            return ((KeyEvent.Callback) fragment).onKeyUp(keyCode, event);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment fragment = getSupportFragmentManager()
                .getPrimaryNavigationFragment()
                .getChildFragmentManager()
                .getPrimaryNavigationFragment();
        if (fragment instanceof KeyEvent.Callback) {
            return ((KeyEvent.Callback) fragment).onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 方便每个fragment设置自已的键盘弹起规则
     *
     * @param mode {@link WindowManager.LayoutParams#softInputMode
     *             WindowManager.LayoutParams.softInputMode
     */
    public void setFragmentWindowSoftInputMode(int mode) {
        getWindow().setSoftInputMode(mode);
    }

    private void bindView() {
        this.viewPageChangeNavigationView = findViewById(R.id.btn_main);
        this.viewPager = findViewById(R.id.fragment_main_adapter_viewpager);
        this.bottomRecite = this.viewPageChangeNavigationView.findViewById(R.id.fragment_main_bottom_recite);

        this.bottomRecite.setOnLongClickListener(this);
    }


    /**
     * 初始化ViewPage,实现滑动切换的功能
     */
    private void initView() {
        this.listFragment = new ArrayList<>(4);
        listFragment.add(creditFragment);
        listFragment.add(rankFragment);
        listFragment.add(analysisFragment);
        BottomViewAdapter adapter = new BottomViewAdapter(this, listFragment);
        viewPager.setAdapter(adapter);
        viewPager.setSaveEnabled(false);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (nowSelectMenuItem != null) {
                    nowSelectMenuItem.setChecked(false);
                } else {
                    viewPageChangeNavigationView.getMenu().getItem(0).setChecked(false);
                }
                nowSelectMenuItem = viewPageChangeNavigationView.getMenu().getItem(position);
                nowSelectMenuItem.setChecked(true);
            }
        });
        viewPager.beginFakeDrag();
        if (viewPager.fakeDragBy(100f)) {
            viewPager.endFakeDrag();
        }
        this.viewPageChangeNavigationView.setOnItemSelectedListener(this);
    }

    private void userAssertCheck() {
        // 还没有同意用户协议跳转到用户协议界面
        if (!userSettings.isAcceptUserAgreement()) {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }
    }
}