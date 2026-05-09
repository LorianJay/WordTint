package com.github.lorenj.wordtint.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AdvancedSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView executeSql;
    /**
     * 文件获取
     */
    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        executeSql(uri);
                    }
                }
            }
    );
    private final Handler updateUIHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_setting);
        bindView();
    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        if (itemId == R.id.tv_advanced_setting_execute_sql) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            filePickerLauncher.launch(intent);
        }
    }

    private void executeSql(Uri uri) {
        APPDatabase appDatabase = APPDatabase.getInstance(this);
        StaticFactory.getExecutorService()
                .execute(() -> appDatabase.runInTransaction(() -> {
                    SupportSQLiteDatabase db =
                            appDatabase.getOpenHelper().getWritableDatabase();
                    try (InputStream inputStream = getContentResolver().openInputStream(uri);
                         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                        StringBuilder sql = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            line = line.trim();

                            if (line.isEmpty() || line.startsWith("--")) {
                                continue;
                            }

                            sql.append(line);

                            if (line.endsWith(";")) {
                                db.execSQL(sql.toString());
                                sql.setLength(0);
                            }
                        }
                    } catch (IOException e) {
                        updateUIHandler.post(() -> Toast.makeText(this, getString(R.string.execute_fail), Toast.LENGTH_SHORT).show());
                        Log.e("AdvancedSettingActivity-executeSql", "", e);
                    }
                    updateUIHandler.post(() -> Toast.makeText(this, getString(R.string.execute_succeed), Toast.LENGTH_SHORT).show());
                }));

    }

    private void bindView() {
        this.executeSql = findViewById(R.id.tv_advanced_setting_execute_sql);

        this.executeSql.setOnClickListener(this);
    }

}