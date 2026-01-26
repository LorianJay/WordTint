package com.github.lorenj.wordtint.ui.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.R;

/**
 * @author sukidayo
 * @date Friday, February 10, 2023
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private CheckBox readPolicy;
    private TextView login, createAccount, title, accountInput, passwordInput;
    private ImageButton backToTrace;
    private final Handler updateUIHandler = new Handler();

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
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
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
            case R.id.fragment_login_read_policy:
                if (readPolicy.isChecked()) {
                    login.setClickable(true);
                    login.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.theme_color, null)));
                } else {
                    login.setClickable(false);
                    login.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_theme_color_90, null)));
                }
                break;
        }
    }

    private void initView() {
        title.setText(R.string.login_account);
        SpannableString spannableString = new SpannableString(readPolicy.getText());
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.theme_color, null));
        spannableString.setSpan(foregroundColorSpan, 7, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {

            }

            @Override
            public void onClick(@NonNull View widget) {
                readPolicy.setChecked(!readPolicy.isChecked());
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_login_to_navigation_user_privacy_policy, null, StaticFactory.getSimpleNavOptions());
            }
        };
        spannableString.setSpan(clickableSpan, 7, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        readPolicy.setText(spannableString);
        readPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        login.setClickable(false);
    }

    private void bindView() {
        this.readPolicy = rootView.findViewById(R.id.fragment_login_read_policy);
        this.login = rootView.findViewById(R.id.fragment_login_login);
        this.createAccount = rootView.findViewById(R.id.fragment_login_create_account);
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.title = rootView.findViewById(R.id.toolbar_title);
        this.accountInput = rootView.findViewById(R.id.fragment_login_account_input);
        this.passwordInput = rootView.findViewById(R.id.fragment_login_password_input);

        this.readPolicy.setOnClickListener(this);
        this.login.setOnClickListener(this);
        this.createAccount.setOnClickListener(this);
        this.backToTrace.setOnClickListener(this);
    }

}