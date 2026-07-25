package com.github.lorenj.wordtint.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.rep.UserSettingRepository;
import com.github.lorenj.wordtint.enums.UserSettingKeyEnums;
import com.github.lorenj.wordtint.ui.activity.SearchWordActivity;
import com.github.lorenj.wordtint.ui.activity.WelcomeActivity;
import com.github.lorenj.wordtint.ui.adapter.BottomViewAdapter;
import com.github.lorenj.wordtint.ui.adapter.listener.NavigationItemSelectListener;
import com.github.lorenj.wordtint.ui.fragment.AnalysisFragment;
import com.github.lorenj.wordtint.ui.fragment.BookListFragment;
import com.github.lorenj.wordtint.ui.fragment.RecordListFragment;
import com.github.lorenj.wordtint.ui.fragment.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnLongClickListener, NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView viewPageChangeNavigationView;
    private ViewPager2 viewPager;
    private MenuItem nowSelectMenuItem;
    private ArrayList<Fragment> listFragment;
    private volatile int position = 0;
    private final Fragment creditFragment = new BookListFragment();
    private final Fragment recordListFragment = new RecordListFragment();
    private final Fragment analysisFragment = new AnalysisFragment();
    private final Fragment settingFragment = new SettingFragment();
    private BottomNavigationItemView bottomRecite;
    private UserSettingRepository userSettingRepository;
    private final Handler updateUIHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 边到边适配
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        // 申请权限
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
        if (itemId == R.id.item_main_bottom_recite) {
            position = 0;
        } else if (itemId == R.id.item_main_bottom_record) {
            position = 1;
        } else if (itemId == R.id.item_main_bottom_analysis) {
            position = 2;
        } else if (itemId == R.id.item_main_bottom_setting) {
            position = 3;
        }
        viewPager.setCurrentItem(position, false);
        Fragment currentClickFragment = listFragment.get(position);
        // 如果当前点击的目标页面就是当前页面则触发回调事件
        if (this.position == position
                && currentClickFragment instanceof NavigationItemSelectListener) {
            ((NavigationItemSelectListener) currentClickFragment).onClickCurrentPage(item);
        }
        this.position = position;
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.item_main_bottom_recite) {
            Intent intent = new Intent(this, SearchWordActivity.class);
            startActivity(intent);
        }
        return false;
    }

    private void bindView() {
        this.viewPageChangeNavigationView = findViewById(R.id.btn_main);
        this.viewPager = findViewById(R.id.fragment_main_adapter_viewpager);
        this.bottomRecite = this.viewPageChangeNavigationView.findViewById(R.id.item_main_bottom_recite);

        this.bottomRecite.setOnLongClickListener(this);
    }

    /**
     * welcome页面跳转的返回
     */
    private final ActivityResultLauncher<Intent> welcomeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    initView();
                }
            }
    );

    /**
     * 初始化ViewPage,实现滑动切换的功能
     */
    private void initView() {
        StaticFactory.getExecutorService().execute(() -> {
            // 得到用户信息文件
            userSettingRepository = UserSettingRepository.getInstance(APPDatabase.getInstance(MainActivity.this).userSettingDao());
            Boolean acceptAgreement = userSettingRepository.getUserSettingValue(UserSettingKeyEnums.AGREE_USER_POLICY);
            // 如果当前还没有同意用户协议则跳转到用户协议界面
            if (!acceptAgreement) {
                updateUIHandler.post(() -> welcomeLauncher.launch(new Intent(MainActivity.this, WelcomeActivity.class)));
                return;
            }
            updateUIHandler.post(() -> {
                // 绑定相关的view
                bindView();
                listFragment = new ArrayList<>(4);
                listFragment.add(creditFragment);
                listFragment.add(recordListFragment);
                listFragment.add(analysisFragment);
                listFragment.add(settingFragment);
                BottomViewAdapter adapter = new BottomViewAdapter(MainActivity.this, listFragment);
                viewPager.setAdapter(adapter);
                viewPager.setSaveEnabled(false);
                viewPager.setUserInputEnabled(false);
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
                        // SettingFragment 沉浸式，其余页面正常显示
                        View root = findViewById(R.id.cl_main_root);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                            WindowInsetsController controller = getWindow().getInsetsController();
                            if (controller != null) {
                                if (position == 3) {
                                    controller.hide(WindowInsets.Type.statusBars());
                                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                                    root.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.background_settings_gradient));
                                } else {
                                    controller.show(WindowInsets.Type.statusBars());
                                    root.setBackgroundColor(getColor(R.color.white));
                                }
                            }
                        }
                    }
                });
                viewPager.beginFakeDrag();
                if (viewPager.fakeDragBy(100f)) {
                    viewPager.endFakeDrag();
                }
                viewPageChangeNavigationView.setOnItemSelectedListener(MainActivity.this);
            });
        });
    }
}