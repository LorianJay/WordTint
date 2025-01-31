package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.pathsystem.document.UserInfoPath;
import com.gitee.cnsukidayo.anylanguageword.entity.UserCreditStyle;
import com.gitee.cnsukidayo.anylanguageword.entity.waper.UserCreditStyleWrapper;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditFilter;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditFormat;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditOrder;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditState;
import com.gitee.cnsukidayo.anylanguageword.context.support.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 背诵单词的中间界面,或者叫启动界面,该界面用户可以定制背诵的各种风格
 *
 * @author sukidayo
 * @date Frida1y, February 10, 2023
 */
public class WordCreditLaunchFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private ImageButton backToTrace;
    private TextView title, start, saveSettings, restoreDefault, selectWordCount;
    private AlertDialog loadingDialog = null;
    private UserCreditStyle userCreditStyle;
    private final Handler updateUIHandler = new Handler();
    private final List<LinearLayout> settingsLinearLayouts = new ArrayList<>(4);
    private LinearLayout modeParent, orderParent, filterParent;
    private CheckBox ignore;

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
        rootView = inflater.inflate(R.layout.fragment_word_credit_launch, container, false);
        loadingDialog = new AlertDialog.Builder(getContext()).setView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null)).setCancelable(false).show();
        bindView();
        initView();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_to_trace:
                Navigation.findNavController(getView()).popBackStack();
                break;
            case R.id.fragment_word_credit_launch_start:
                Bundle bundle = new Bundle();
                bundle.putAll(getArguments());
                if (userCreditStyle.getCreditFormat() == CreditFormat.CLASSIC) {
                    Navigation.findNavController(getView()).navigate(R.id.action_navigation_word_credit_launch_to_navigation_word_credit, bundle, StaticFactory.getSimpleNavOptions());
                } else {
                    Navigation.findNavController(getView()).navigate(R.id.action_navigation_word_credit_launch_to_navigation_search_word, bundle, StaticFactory.getSimpleNavOptions());
                }
                break;
            case R.id.fragment_word_credit_launch_save_settings:
                try {
                    JsonUtils.writeJson(UserInfoPath.USER_CREDIT_STYLE.getPath(), userCreditStyle);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.fragment_word_credit_launch_restore_default:
                userCreditStyle.setCreditState(CreditState.ENGLISH_TRANSLATION_CHINESE_HEARING);
                userCreditStyle.setCreditOrder(CreditOrder.ORDERLY);
                userCreditStyle.setCreditFilter(CreditFilter.WORD);
                userCreditStyle.setCreditFormat(CreditFormat.CLASSIC);
                break;
            case R.id.fragment_word_credit_launch_ignore:
                userCreditStyle.setIgnore(ignore.isChecked());
                break;
            default:
                if (v instanceof TextView) {
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
                break;
        }
        updateCreditStyle(userCreditStyle);
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

    private void bindView() {
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.title = rootView.findViewById(R.id.toolbar_title);
        this.start = rootView.findViewById(R.id.fragment_word_credit_launch_start);
        this.saveSettings = rootView.findViewById(R.id.fragment_word_credit_launch_save_settings);
        this.selectWordCount = rootView.findViewById(R.id.fragment_word_credit_launch_word_count);
        this.restoreDefault = rootView.findViewById(R.id.fragment_word_credit_launch_restore_default);
        this.ignore = rootView.findViewById(R.id.fragment_word_credit_launch_ignore);
        settingsLinearLayouts.add(rootView.findViewById(R.id.fragment_word_credit_launch_mode));
        settingsLinearLayouts.add(rootView.findViewById(R.id.fragment_word_credit_launch_order));
        settingsLinearLayouts.add(rootView.findViewById(R.id.fragment_word_credit_launch_filter));
        settingsLinearLayouts.add(rootView.findViewById(R.id.fragment_word_credit_launch_format));
        this.modeParent = rootView.findViewById(R.id.fragment_word_credit_launch_mode_parent);
        this.orderParent = rootView.findViewById(R.id.fragment_word_credit_launch_order_parent);
        this.filterParent = rootView.findViewById(R.id.fragment_word_credit_launch_filter_parent);
        for (int i = 0; i < settingsLinearLayouts.size(); i++) {
            for (int j = 0; j < settingsLinearLayouts.get(i).getChildCount(); j++) {
                settingsLinearLayouts.get(i).getChildAt(j).setOnClickListener(this);
            }
        }
        UserCreditStyleWrapper userCreditStyleWrapper = getArguments().getParcelable(CreditFragment.USER_CREDIT_STYLE_WRAPPER);
        this.userCreditStyle = userCreditStyleWrapper.getUserCreditStyle();
        this.saveSettings.setOnClickListener(this);
        this.restoreDefault.setOnClickListener(this);
        this.backToTrace.setOnClickListener(this);
        this.start.setOnClickListener(this);
        this.ignore.setOnClickListener(this);
    }

    private void initView() {
        this.title.setText(R.string.style);
        // 设置选词量
        int selectWordCount = getArguments().getInt(CreditFragment.SELECT_WORD_COUNT);
        this.selectWordCount.setText(String.valueOf(selectWordCount));
        if (this.userCreditStyle.isReview()) {
            this.start.setForegroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple75, null)));
            this.start.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.purple75, null)));
        }
        updateCreditStyle(userCreditStyle);
        loadingDialog.dismiss();
    }
}