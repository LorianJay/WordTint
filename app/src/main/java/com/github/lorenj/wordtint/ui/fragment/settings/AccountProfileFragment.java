package com.github.lorenj.wordtint.ui.fragment.settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.entity.dto.UserProfileVO;
import com.github.lorenj.wordtint.enums.SexType;
import com.github.lorenj.wordtint.ui.adapter.settings.UniversityListAdapter;
import com.github.lorenj.wordtint.utils.DPUtils;
import com.github.lorenj.wordtint.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 */
public class AccountProfileFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private View rootView;
    private TextView title, userName, describeInfo, sex, birthday, university, uuid;
    private ImageButton backToTrace;
    private ImageView universityButton;
    private Handler updateUIHandler = new Handler();
    private boolean changeUserInfo = false;
    // 用户信息
    private UserProfileVO userProfileVO;
    // 用户性别选择
    private int choiceSexIndex;
    // university的layoutManager
    private LinearLayoutManager universityLayoutManager;
    // 设置university的adapter
    private UniversityListAdapter universityListAdapter;
    // 展示RecyclerView
    private RecyclerView universityreRecyclerView;
    // 弹出的选择列表
    private PopupWindow universityPopupWindow;
    // 用户选择日期
    private LocalDate newBirthday = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            initView();
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_account_profile, container, false);
        bindView();
        initView();
        this.title.setText(R.string.account_profile);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.toolbar_back_to_trace) {
            Navigation.findNavController(getView()).popBackStack();
        } else if (itemId == R.id.fragment_account_sex) {
            changeUserInfo = true;
            String[] items = new String[]{"男", "女", "保密"};
            choiceSexIndex = userProfileVO.getSex().ordinal();
            new AlertDialog.Builder(getContext())
                    .setTitle("选择性别")
                    .setPositiveButton("取消", (dialog, which) -> {
                    })
                    .setPositiveButton("确定", (dialog, which) -> {
                        userProfileVO.setSex(SexType.values()[choiceSexIndex]);
                        userProfileVO.setSexString(userProfileVO.getSex().value());
                        sex.setText(userProfileVO.getSexString());
                    })
                    .setSingleChoiceItems(items, choiceSexIndex, (dialog, which) -> {
                        choiceSexIndex = which;
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        } else if (itemId == R.id.fragment_account_birthday) {
            CalendarView calendarView = new CalendarView(getContext());
            newBirthday = null;
            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                newBirthday = LocalDate.of(year, month, dayOfMonth);
            });
            new AlertDialog.Builder(getContext())
                    .setTitle("选择生日")
                    .setView(calendarView)
                    .setCancelable(false)
                    .setPositiveButton("确定", (dialog, which) -> {
                        if (newBirthday != null) {
                            String birthdayDate = newBirthday.format(DateTimeFormatter.ISO_DATE);
                            birthday.setText(birthdayDate);
                            changeUserInfo = true;
                            userProfileVO.setBirthday(newBirthday);
                        }
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                    })
                    .show();
        }

    }

    @Override
    public void onStop() {
        updateUserProfile();
        super.onStop();
    }

    @Override
    public void afterTextChanged(Editable schoolName) {
        // 修改用户学校信息
        if (universityPopupWindow == null) {
            universityPopupWindow = new PopupWindow(universityreRecyclerView, university.getWidth(), DPUtils.dp2px(120));
            universityPopupWindow.setOutsideTouchable(true);
            universityPopupWindow.setAnimationStyle(R.style.pop_window_alpha_anim_style);
        }
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!universityPopupWindow.isShowing() && university.isFocused() && inputMethodManager.isAcceptingText()) {
            universityPopupWindow.showAsDropDown(universityButton);
        }
        // 修改用户昵称
        if (userName.isFocused()) {
            changeUserInfo = true;
            userProfileVO.setNick(userName.getText().toString());
        }

        // 修改用户简介
        if (describeInfo.isFocused()) {
            changeUserInfo = true;
            userProfileVO.setDescribeInfo(describeInfo.getText().toString());
        }
    }

    private void updateUserProfile() {
    }

    /**
     * 更新用户的个人信息
     */
    private void updateProfileUI() {
        userName.setText(userProfileVO.getNick());
        describeInfo.setText(userProfileVO.getDescribeInfo());
        sex.setText(userProfileVO.getSexString());
        birthday.setText(userProfileVO.getBirthday().toString());
        university.setText(userProfileVO.getUniversity());
        uuid.setText(String.valueOf(userProfileVO.getUuid()));
    }

    private void bindView() {
        this.title = rootView.findViewById(R.id.toolbar_title);
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.userName = rootView.findViewById(R.id.fragment_account_user_name);
        this.describeInfo = rootView.findViewById(R.id.fragment_account_describe_info);
        this.sex = rootView.findViewById(R.id.fragment_account_sex);
        this.birthday = rootView.findViewById(R.id.fragment_account_birthday);
        this.university = rootView.findViewById(R.id.fragment_account_university);
        this.uuid = rootView.findViewById(R.id.fragment_account_uuid);
        this.universityButton = rootView.findViewById(R.id.fragment_account_university_button);

        this.backToTrace.setOnClickListener(this);
        this.describeInfo.setOnClickListener(this);
        this.sex.setOnClickListener(this);
        this.birthday.setOnClickListener(this);
        this.universityButton.setOnClickListener(this);
        this.university.addTextChangedListener(this);
        this.userName.addTextChangedListener(this);
        this.describeInfo.addTextChangedListener(this);
    }


    private void initView() {
        this.changeUserInfo = false;
        universityLayoutManager = new LinearLayoutManager(getContext());
        universityListAdapter = new UniversityListAdapter(getContext());
        universityreRecyclerView = new RecyclerView(getContext());
        universityreRecyclerView.setLayoutManager(universityLayoutManager);
        universityreRecyclerView.setAdapter(universityListAdapter);
        // 设置用户点击选择学校后的回调事件
        universityListAdapter.setRecycleViewItemOnClickListener(universityDTO -> {
            updateUIHandler.post(() -> {
                userProfileVO.setUniversity(universityDTO.getSchoolName());
                updateProfileUI();
                universityPopupWindow.dismiss();
                changeUserInfo = true;
            });
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }


}