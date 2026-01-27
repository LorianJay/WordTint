package com.github.lorenj.wordtint.ui.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.UserSettings;
import com.github.lorenj.wordtint.context.pathsystem.document.UserInfoPath;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.utils.FileUtils;
import com.github.lorenj.wordtint.utils.JsonUtils;

import java.io.IOException;
import java.io.InputStream;

import io.noties.markwon.Markwon;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener, KeyEvent.Callback {
    private Button disAgree, accept;
    private TextView welcomeMessage;
    private AssetManager assetManager;
    String message = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        this.assetManager = this.getAssets();
        bindView();
        initView();
    }

    private void bindView() {
        disAgree = findViewById(R.id.btn_welcome_disagree_agreement);
        accept = findViewById(R.id.btn_welcome_accept_agreement);
        this.welcomeMessage = findViewById(R.id.txt_welcome_message);
        disAgree.setOnClickListener(this);
        accept.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.btn_welcome_disagree_agreement) {
            exitAPP();
        } else if (vId == R.id.btn_welcome_accept_agreement) {
            try {
                UserSettings userSettings = JsonUtils.readJson(UserInfoPath.USER_SETTINGS.getPath(), UserSettings.class);
                userSettings.setAcceptUserAgreement(true);
                JsonUtils.writeJson(UserInfoPath.USER_SETTINGS.getPath(), userSettings);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            exitAPP();
        }
        return true;
    }

    private void initView() {
        // 读取欢迎markdown文件
        try (InputStream welcomeInputStream = assetManager.open("systemFile/welcomeMessage.md");) {
            message = FileUtils.readAll(welcomeInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Markwon markwon = StaticFactory.getGlobalMarkwon(this);
        markwon.setMarkdown(welcomeMessage, message);
    }

    private void exitAPP() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}