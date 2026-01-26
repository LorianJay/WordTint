package com.github.lorenj.wordtint.ui.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
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
 * @date Saturday, February 11, 2023
 */
public class CreateAccountFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private ImageButton backToTrace;
    private TextView title, create;
    private CheckBox readPolicy;

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
        rootView = inflater.inflate(R.layout.fragment_create_account, container, false);
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
            case R.id.fragment_create_account_read_policy:
                if (readPolicy.isChecked()) {
                    create.setClickable(true);
                    create.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.theme_color, null)));
                } else {
                    create.setClickable(false);
                    create.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_theme_color_90, null)));
                }
                break;
        }
    }

    private void initView() {
        title.setText(R.string.create_account);
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
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_create_account_to_navigation_user_privacy_policy, null, StaticFactory.getSimpleNavOptions());
            }
        };
        spannableString.setSpan(clickableSpan, 7, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        readPolicy.setText(spannableString);
        readPolicy.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void bindView() {
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.title = rootView.findViewById(R.id.toolbar_title);
        this.readPolicy = rootView.findViewById(R.id.fragment_create_account_read_policy);
        this.create = rootView.findViewById(R.id.fragment_create_account_create);

        this.backToTrace.setOnClickListener(this);
        this.create.setOnClickListener(this);
        this.readPolicy.setOnClickListener(this);
    }
}