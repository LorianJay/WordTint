package com.github.lorenj.wordtint.ui;

import android.Manifest;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.lorenj.wordtint.context.AnyLanguageWordProperties;
import com.github.lorenj.wordtint.context.UserSettings;
import com.github.lorenj.wordtint.context.pathsystem.document.UserInfoPath;
import com.github.lorenj.wordtint.utils.JsonUtils;
import com.github.lorenj.wordtint.R;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private UserSettings userSettings;

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
        changeFragment();
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Fragment fragment = getSupportFragmentManager()
                .getPrimaryNavigationFragment()
                .getChildFragmentManager()
                .getPrimaryNavigationFragment();
        if (fragment instanceof Window.Callback) {
            boolean result = ((Window.Callback) fragment).dispatchTouchEvent(event);
            return result || super.dispatchTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
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

    private void changeFragment() {
        if (!userSettings.isAcceptUserAgreement()) {
            // 还没有同意用户协议跳转到用户协议界面
            Navigation.findNavController(this.findViewById(R.id.fragment_main_adapter_viewpager)).navigate(R.id.action_navigation_main_to_navigation_welcome);
        }
    }
}