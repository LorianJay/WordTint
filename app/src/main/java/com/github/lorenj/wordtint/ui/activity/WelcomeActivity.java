package com.github.lorenj.wordtint.ui.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.rep.UserSettingRepository;
import com.github.lorenj.wordtint.enums.UserSettingKeyEnums;
import com.github.lorenj.wordtint.ui.viewmodel.WelcomeViewModel;
import com.github.lorenj.wordtint.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;

import io.noties.markwon.Markwon;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener, KeyEvent.Callback {
    private TextView disAgree, accept;
    private TextView welcomeMessage;
    private AssetManager assetManager;
    private LinearLayout agreementArea, initProgressArea;
    private WelcomeViewModel welcomeViewModel;
    private ProgressBar initProgressBar;
    String message = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        this.assetManager = this.getAssets();
        bindView();
        initView();
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.btn_welcome_disagree_agreement) {
            exitAPP();
        } else if (vId == R.id.btn_welcome_accept_agreement) {
            agreementArea.setVisibility(View.GONE);
            initProgressArea.setVisibility(View.VISIBLE);
            // 开始初始化数据库
            welcomeViewModel.getInitState().observe(this, initState -> {
                initProgressBar.setProgress(initState.progress);
                if (initState.progress == 100) {
                    setResult(RESULT_OK);
                    finish();
                }
            });
            // 异步执行
            StaticFactory.getExecutorService().execute(() -> {
                welcomeViewModel.initDatabase(WelcomeActivity.this);
                UserSettingRepository userSettingRepository = UserSettingRepository
                        .getInstance(APPDatabase.getInstance(WelcomeActivity.this).userSettingDao());
                userSettingRepository.update(UserSettingKeyEnums.AGREE_USER_POLICY, true);
            });
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

    private void bindView() {
        disAgree = findViewById(R.id.btn_welcome_disagree_agreement);
        accept = findViewById(R.id.btn_welcome_accept_agreement);
        welcomeMessage = findViewById(R.id.txt_welcome_message);
        initProgressBar = findViewById(R.id.pb_init_progress);
        agreementArea = findViewById(R.id.ll_agreement);
        initProgressArea = findViewById(R.id.ll_init_progress);

        disAgree.setOnClickListener(this);
        accept.setOnClickListener(this);
        welcomeViewModel = new ViewModelProvider(this).get(WelcomeViewModel.class);
    }

    private void exitAPP() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 初始化状态模型
     */
    public static class InitState {

        public final int progress;

        public InitState(int progress) {
            this.progress = progress;
        }

    }
}