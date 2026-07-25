package com.github.lorenj.wordtint.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.github.lorenj.wordtint.R;

public class UserAgreementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);
        // 边到边适配
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }
}