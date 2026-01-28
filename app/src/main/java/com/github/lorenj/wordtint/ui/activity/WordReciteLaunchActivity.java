package com.github.lorenj.wordtint.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.pathsystem.document.UserInfoPath;
import com.github.lorenj.wordtint.entity.UserCreditStyle;
import com.github.lorenj.wordtint.enums.CreditFilter;
import com.github.lorenj.wordtint.enums.CreditFormat;
import com.github.lorenj.wordtint.enums.CreditOrder;
import com.github.lorenj.wordtint.enums.CreditState;
import com.github.lorenj.wordtint.ui.fragment.BookListFragment;
import com.github.lorenj.wordtint.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WordReciteLaunchActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton toolBarBack;
    private TextView start, saveSettings, restoreDefault, selectWordCount;
    private AlertDialog loadingDialog = null;
    private UserCreditStyle userCreditStyle;
    private final List<LinearLayout> settingsLinearLayouts = new ArrayList<>(4);
    private LinearLayout modeParent, orderParent, filterParent;
    private CheckBox ignore;

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
            if (userCreditStyle.getCreditFormat() == CreditFormat.CLASSIC) {
                Intent intent = new Intent(this, MainReciteActivity.class);
                intent.putExtras(getIntent());
                startActivity(intent);
            } else {

            }
        } else if (itemId == R.id.fragment_word_credit_launch_save_settings) {
            try {
                JsonUtils.writeJson(UserInfoPath.USER_CREDIT_STYLE.getPath(), userCreditStyle);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (itemId == R.id.fragment_word_credit_launch_restore_default) {
            userCreditStyle.setCreditState(CreditState.ENGLISH_TRANSLATION_CHINESE_HEARING);
            userCreditStyle.setCreditOrder(CreditOrder.ORDERLY);
            userCreditStyle.setCreditFilter(CreditFilter.WORD);
            userCreditStyle.setCreditFormat(CreditFormat.CLASSIC);
        } else if (itemId == R.id.fragment_word_credit_launch_ignore) {
            userCreditStyle.setIgnore(ignore.isChecked());
        } else if (v instanceof TextView) {
            ViewParent parent = v.getParent();
            if (parent == settingsLinearLayouts.get(0)) {
                userCreditStyle.setCreditState(CreditState.values()[settingsLinearLayouts.get(0).indexOfChild(v)]);
            } else if (parent == settingsLinearLayouts.get(1)) {
                userCreditStyle.setCreditOrder(CreditOrder.values()[settingsLinearLayouts.get(1).indexOfChild(v)]);
            } else if (parent == settingsLinearLayouts.get(2)) {
                userCreditStyle.setCreditFilter(CreditFilter.values()[settingsLinearLayouts.get(2).indexOfChild(v)]);
            } else if (parent == settingsLinearLayouts.get(3)) {
                userCreditStyle.setCreditFormat(CreditFormat.values()[settingsLinearLayouts.get(3).indexOfChild(v)]);
            }
        }
        updateCreditStyle(userCreditStyle);
    }


    private void bindView() {
        this.toolBarBack = findViewById(R.id.ib_toolbar_back);
        this.start = findViewById(R.id.tv_word_recite_start);
        this.saveSettings = findViewById(R.id.fragment_word_credit_launch_save_settings);
        this.selectWordCount = findViewById(R.id.fragment_word_credit_launch_word_count);
        this.restoreDefault = findViewById(R.id.fragment_word_credit_launch_restore_default);
        this.ignore = findViewById(R.id.fragment_word_credit_launch_ignore);
        settingsLinearLayouts.add(findViewById(R.id.fragment_word_credit_launch_mode));
        settingsLinearLayouts.add(findViewById(R.id.fragment_word_credit_launch_order));
        settingsLinearLayouts.add(findViewById(R.id.fragment_word_credit_launch_filter));
        settingsLinearLayouts.add(findViewById(R.id.fragment_word_credit_launch_format));
        this.modeParent = findViewById(R.id.fragment_word_credit_launch_mode_parent);
        this.orderParent = findViewById(R.id.fragment_word_credit_launch_order_parent);
        this.filterParent = findViewById(R.id.fragment_word_credit_launch_filter_parent);
        for (int i = 0; i < settingsLinearLayouts.size(); i++) {
            for (int j = 0; j < settingsLinearLayouts.get(i).getChildCount(); j++) {
                settingsLinearLayouts.get(i).getChildAt(j).setOnClickListener(this);
            }
        }
        // 得到背诵风格
        Bundle bundle = getIntent().getExtras();
        this.userCreditStyle = (UserCreditStyle) Optional.ofNullable(bundle)
                .map(b -> b.getSerializable(BookListFragment.USER_CREDIT_STYLE_WRAPPER))
                .orElse(null);

        this.saveSettings.setOnClickListener(this);
        this.restoreDefault.setOnClickListener(this);
        this.toolBarBack.setOnClickListener(this);
        this.start.setOnClickListener(this);
        this.ignore.setOnClickListener(this);
    }

    private void initView() {
        loadingDialog = new AlertDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_loading, null))
                .setCancelable(false).show();
        // 设置选词量
        Bundle bundle = getIntent().getExtras();
        int selectWordCount = Optional.ofNullable(bundle)
                .map(p -> p.getInt(BookListFragment.SELECT_WORD_COUNT))
                .orElse(0);
        this.selectWordCount.setText(String.valueOf(selectWordCount));
        if (this.userCreditStyle.isReview()) {
            this.start.setForegroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple75, null)));
            this.start.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.purple75, null)));
        }
        updateCreditStyle(userCreditStyle);
        loadingDialog.dismiss();
    }


    private void updateCreditStyle(UserCreditStyle userCreditStyle) {
        ignore.setChecked(userCreditStyle.isIgnore());
        for (int i = 0; i < settingsLinearLayouts.size(); i++) {
            settingsLinearLayouts.get(i).getChildAt(0).setBackgroundResource(R.drawable.background_solid_radius_start_6dp);
            settingsLinearLayouts.get(i).getChildAt(0).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_theme_color, null)));
            for (int j = 1; j < settingsLinearLayouts.get(i).getChildCount(); j++) {
                settingsLinearLayouts.get(i).getChildAt(j).setBackgroundResource(0);
            }
        }
        changeState(userCreditStyle.getCreditState().ordinal(), 0);
        changeState(userCreditStyle.getCreditOrder().ordinal(), 1);
        changeState(userCreditStyle.getCreditFilter().ordinal(), 2);
        changeState(userCreditStyle.getCreditFormat().ordinal(), 3);
        if (userCreditStyle.getCreditFormat() == CreditFormat.ASSOCIATION) {
            modeParent.setVisibility(View.GONE);
            orderParent.setVisibility(View.GONE);
            filterParent.setVisibility(View.GONE);
        } else {
            modeParent.setVisibility(View.VISIBLE);
            orderParent.setVisibility(View.VISIBLE);
            filterParent.setVisibility(View.VISIBLE);
        }
    }

    private void changeState(int ordinal, int position) {
        if (ordinal == 0) {
            settingsLinearLayouts.get(position).getChildAt(ordinal).setBackgroundResource(R.drawable.background_solid_radius_start_6dp);
        } else if (ordinal == settingsLinearLayouts.get(position).getChildCount() - 1) {
            settingsLinearLayouts.get(position).getChildAt(ordinal).setBackgroundResource(R.drawable.background_solid_radius_end_6dp);
        } else {
            settingsLinearLayouts.get(position).getChildAt(ordinal).setBackgroundColor(getResources().getColor(R.color.theme_color, null));
        }
        settingsLinearLayouts.get(position).getChildAt(ordinal).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.theme_color, null)));
    }

}