package com.github.lorenj.wordtint.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.dao.WordBookDao;
import com.github.lorenj.wordtint.database.entity.WordBookEntity;
import com.github.lorenj.wordtint.database.entity.WordBookSectionEntity;
import com.github.lorenj.wordtint.ui.viewmodel.WelcomeViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

@Database(entities = {WordBookEntity.class,
        WordBookSectionEntity.class}, version = 5)
public abstract class APPDatabase extends RoomDatabase {

    private static volatile APPDatabase INSTANCE = null;

    public abstract WordBookDao wordBookDao();

    public static APPDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (APPDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            APPDatabase.class,
                            "app.db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化数据库
     *
     * @param context              上下文
     * @param initProgressCallback 进度回调函数
     */
    public static void initDatabase(Context context,
                                    WelcomeViewModel.InitProgressCallback callback) {
        APPDatabase database = getInstance(context);
        StaticFactory.getExecutorService().execute(() -> {

            database.runInTransaction(() -> {
                try {
                    SupportSQLiteDatabase db =
                            database.getOpenHelper().getWritableDatabase();

                    String[] files = Optional.ofNullable(context.getAssets().list("sql"))
                            .orElse(new String[0]);

                    int total = files.length;
                    int completed = 0;

                    for (String fileName : files) {

                        try (InputStream is = context.getAssets().open("sql/" + fileName);
                             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
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
                        }

                        completed++;
                        if (callback != null) {
                            int progress = completed * 100 / total;
                            callback.onProgress(progress);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
