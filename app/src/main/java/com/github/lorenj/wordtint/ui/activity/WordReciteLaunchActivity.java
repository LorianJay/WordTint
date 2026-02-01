package com.github.lorenj.wordtint.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.rep.UserSettingRepository;
import com.github.lorenj.wordtint.database.vo.UserRecitePreference;
import com.github.lorenj.wordtint.enums.ReciteFilter;
import com.github.lorenj.wordtint.enums.ReciteMode;
import com.github.lorenj.wordtint.enums.ReciteOrder;
import com.github.lorenj.wordtint.enums.ReciteStyle;
import com.github.lorenj.wordtint.enums.UserSettingKeyEnums;
import com.github.lorenj.wordtint.ui.fragment.BookListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class WordReciteLaunchActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * UI相关
     */
    private ImageButton toolBarBack;
    private TextView start, saveSettings, restoreDefault, tvSelectWordCount;
    private final List<LinearLayout> settingsLinearLayouts = new ArrayList<>(4);
    private LinearLayout modeParent, orderParent, filterParent;
    private CheckBox ignore;
    private final Handler updateUIHandler = new Handler(Looper.getMainLooper());
    private Toast toast;
    /**
     * 用户设置仓库
     */
    private UserSettingRepository userSettingRepository;
    /**
     * 背诵偏好
     */
    private UserRecitePreference userRecitePreference;
    /**
     * 背诵偏好
     */
    public static String USER_RECITE_PREFERENCE = "USER_RECITE_PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_recite_launch);
        bindView();
        initView();
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.ib_toolbar_back) {
            finish();
        } else if (itemId == R.id.tv_word_recite_start) {
            if (userRecitePreference.getReciteStyle() == ReciteStyle.CLASSIC) {
                Intent intent = new Intent(this, MainReciteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(USER_RECITE_PREFERENCE, userRecitePreference);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {

            }
        } else if (itemId == R.id.fragment_word_credit_launch_save_settings) {
            // 保存用户的当前设置
            StaticFactory.getExecutorService().execute(() -> {
                userSettingRepository.update(UserSettingKeyEnums.RECITE_MODE, userRecitePreference.getReciteMode());
                userSettingRepository.update(UserSettingKeyEnums.RECITE_ORDER, userRecitePreference.getReciteOrder());
                userSettingRepository.update(UserSettingKeyEnums.RECITE_FILTER, userRecitePreference.getReciteFilter());
                userSettingRepository.update(UserSettingKeyEnums.RECITE_STYLE, userRecitePreference.getReciteStyle());
                userSettingRepository.update(UserSettingKeyEnums.SKIP_PREFERENCE, userRecitePreference.isIgnore());
            });
            toast.show();
        } else if (itemId == R.id.fragment_word_credit_launch_restore_default) {
            userRecitePreference.setReciteMode((ReciteMode) UserSettingKeyEnums.RECITE_MODE.defaultValue);
            userRecitePreference.setReciteOrder((ReciteOrder) UserSettingKeyEnums.RECITE_ORDER.defaultValue);
            userRecitePreference.setReciteFilter((ReciteFilter) UserSettingKeyEnums.RECITE_FILTER.defaultValue);
            userRecitePreference.setReciteStyle((ReciteStyle) UserSettingKeyEnums.RECITE_STYLE.defaultValue);
            updateUIByPreference(userRecitePreference);
        } else if (itemId == R.id.fragment_word_credit_launch_ignore) {
            userRecitePreference.setIgnore(ignore.isChecked());
            updateUIByPreference(userRecitePreference);
        } else if (v instanceof TextView) {
            ViewParent parent = v.getParent();
            if (parent == settingsLinearLayouts.get(0)) {
                userRecitePreference.setReciteMode(ReciteMode.values()[settingsLinearLayouts.get(0).indexOfChild(v)]);
            } else if (parent == settingsLinearLayouts.get(1)) {
                userRecitePreference.setReciteOrder(ReciteOrder.values()[settingsLinearLayouts.get(1).indexOfChild(v)]);
            } else if (parent == settingsLinearLayouts.get(2)) {
                userRecitePreference.setReciteFilter(ReciteFilter.values()[settingsLinearLayouts.get(2).indexOfChild(v)]);
            } else if (parent == settingsLinearLayouts.get(3)) {
                userRecitePreference.setReciteStyle(ReciteStyle.values()[settingsLinearLayouts.get(3).indexOfChild(v)]);
            }
            updateUIByPreference(userRecitePreference);
        }
    }

    private void bindView() {
        this.toolBarBack = findViewById(R.id.ib_toolbar_back);
        this.start = findViewById(R.id.tv_word_recite_start);
        this.saveSettings = findViewById(R.id.fragment_word_credit_launch_save_settings);
        this.tvSelectWordCount = findViewById(R.id.fragment_word_credit_launch_word_count);
        this.restoreDefault = findViewById(R.id.fragment_word_credit_launch_restore_default);
        this.ignore = findViewById(R.id.fragment_word_credit_launch_ignore);
        settingsLinearLayouts.add(findViewById(R.id.fragment_word_credit_launch_mode));
        settingsLinearLayouts.add(findViewById(R.id.fragment_word_credit_launch_order));
        settingsLinearLayouts.add(findViewById(R.id.fragment_word_credit_launch_filter));
        settingsLinearLayouts.add(findViewById(R.id.fragment_word_credit_launch_format));
        this.modeParent = findViewById(R.id.fragment_word_credit_launch_mode_parent);
        this.orderParent = findViewById(R.id.fragment_word_credit_launch_order_parent);
        this.filterParent = findViewById(R.id.fragment_word_credit_launch_filter_parent);
        // 批量设置所有能够被点击按钮的监听事件
        for (int i = 0; i < settingsLinearLayouts.size(); i++) {
            for (int j = 0; j < settingsLinearLayouts.get(i).getChildCount(); j++) {
                settingsLinearLayouts.get(i).getChildAt(j).setOnClickListener(this);
            }
        }
        this.saveSettings.setOnClickListener(this);
        this.restoreDefault.setOnClickListener(this);
        this.toolBarBack.setOnClickListener(this);
        this.start.setOnClickListener(this);
        this.ignore.setOnClickListener(this);
        // 数据库
        userSettingRepository = UserSettingRepository.getInstance(APPDatabase.getInstance(this).userSettingDao());
    }

    private void initView() {
        AlertDialog loadingDialog = new AlertDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_loading, null))
                .setCancelable(false)
                .show();
        toast = Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        // 批量查询所有的背诵偏好
        StaticFactory.getExecutorService().execute(() -> {
            Bundle bundle = getIntent().getExtras();
            // 设置选中的背诵列表
            List<Integer> allSectionIdList = Optional.ofNullable(bundle)
                    .map((Function<Bundle, List<Integer>>) args -> args.getIntegerArrayList(BookListFragment.SELECT_SECTION_LIST))
                    .orElse(new ArrayList<>());
            // todo 如果是历史记录
            userRecitePreference = new UserRecitePreference(
                    userSettingRepository.getUserSettingValue(UserSettingKeyEnums.RECITE_MODE),
                    userSettingRepository.getUserSettingValue(UserSettingKeyEnums.RECITE_ORDER),
                    userSettingRepository.getUserSettingValue(UserSettingKeyEnums.RECITE_FILTER),
                    userSettingRepository.getUserSettingValue(UserSettingKeyEnums.RECITE_STYLE),
                    false,
                    allSectionIdList);
            // 设置选词量
            int selectWordCount = Optional.ofNullable(bundle)
                    .map(p -> p.getInt(BookListFragment.SELECT_WORD_COUNT))
                    .orElse(0);
            updateUIHandler.post(() -> {
                tvSelectWordCount.setText(String.valueOf(selectWordCount));
                updateUIByPreference(userRecitePreference);
                //if (this.userCreditStyle.isReview()) {
                //    this.start.setForegroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple75, null)));
                //    this.start.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.purple75, null)));
                //}
                loadingDialog.dismiss();
            });
        });
    }


    /**
     * 根据当前的背诵偏好更新UI
     *
     * @param userRecitePreference 背诵偏好设置
     */
    private void updateUIByPreference(UserRecitePreference userRecitePreference) {
        ignore.setChecked(userRecitePreference.isIgnore());
        for (int i = 0; i < settingsLinearLayouts.size(); i++) {
            settingsLinearLayouts.get(i).getChildAt(0).setBackgroundResource(R.drawable.background_solid_radius_start_6dp);
            settingsLinearLayouts.get(i).getChildAt(0).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_theme_color, null)));
            for (int j = 1; j < settingsLinearLayouts.get(i).getChildCount(); j++) {
                settingsLinearLayouts.get(i).getChildAt(j).setBackgroundResource(0);
            }
        }
        updateUnitUI(userRecitePreference.getReciteMode().ordinal(), 0);
        updateUnitUI(userRecitePreference.getReciteOrder().ordinal(), 1);
        updateUnitUI(userRecitePreference.getReciteFilter().ordinal(), 2);
        updateUnitUI(userRecitePreference.getReciteStyle().ordinal(), 3);
        if (userRecitePreference.getReciteStyle() == ReciteStyle.ASSOCIATION) {
            modeParent.setVisibility(View.GONE);
            orderParent.setVisibility(View.GONE);
            filterParent.setVisibility(View.GONE);
        } else {
            modeParent.setVisibility(View.VISIBLE);
            orderParent.setVisibility(View.VISIBLE);
            filterParent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新最小单元,其目标是位于第一个和最后一个元素要加圆角边框修饰
     *
     * @param ordinal
     * @param position
     */
    private void updateUnitUI(int ordinal, int position) {
        if (ordinal == 0) {
            settingsLinearLayouts.get(position).getChildAt(ordinal).setBackgroundResource(R.drawable.background_solid_radius_start_6dp);
        } else if (ordinal == settingsLinearLayouts.get(position).getChildCount() - 1) {
            settingsLinearLayouts.get(position).getChildAt(ordinal).setBackgroundResource(R.drawable.background_solid_radius_end_6dp);
        } else {
            settingsLinearLayouts.get(position).getChildAt(ordinal).setBackgroundColor(getResources().getColor(R.color.theme_color, null));
        }
        settingsLinearLayouts.get(position).getChildAt(ordinal).getBackground().setTint(getResources().getColor(R.color.theme_color, null));
    }

}