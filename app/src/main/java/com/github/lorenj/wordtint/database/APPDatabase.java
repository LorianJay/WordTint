package com.github.lorenj.wordtint.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.github.lorenj.wordtint.database.dao.ReciteRecordDao;
import com.github.lorenj.wordtint.database.dao.ReciteRecordMarkDao;
import com.github.lorenj.wordtint.database.dao.ReciteRecordWordDao;
import com.github.lorenj.wordtint.database.dao.UserSettingDao;
import com.github.lorenj.wordtint.database.dao.WordBookDao;
import com.github.lorenj.wordtint.database.dao.WordBookSectionDao;
import com.github.lorenj.wordtint.database.dao.WordMarkLogDao;
import com.github.lorenj.wordtint.database.dao.WordNoteDao;
import com.github.lorenj.wordtint.database.dao.WordOriginDao;
import com.github.lorenj.wordtint.database.dao.WordSearchDao;
import com.github.lorenj.wordtint.database.dao.WordStarDao;
import com.github.lorenj.wordtint.database.dao.WordStarWordIdDao;
import com.github.lorenj.wordtint.database.entity.ReciteRecordEntity;
import com.github.lorenj.wordtint.database.entity.ReciteRecordWordEntity;
import com.github.lorenj.wordtint.database.entity.ReciteRecordWordMarkEntity;
import com.github.lorenj.wordtint.database.entity.UserSettingEntity;
import com.github.lorenj.wordtint.database.entity.WordBookEntity;
import com.github.lorenj.wordtint.database.entity.WordBookSectionEntity;
import com.github.lorenj.wordtint.database.entity.WordBookSectionWordIdEntity;
import com.github.lorenj.wordtint.database.entity.WordMarkLogEntity;
import com.github.lorenj.wordtint.database.entity.WordNoteEntity;
import com.github.lorenj.wordtint.database.entity.WordOriginEntity;
import com.github.lorenj.wordtint.database.entity.WordSearchEntity;
import com.github.lorenj.wordtint.database.entity.WordStarEntity;
import com.github.lorenj.wordtint.database.entity.WordStarWordIdEntity;
import com.github.lorenj.wordtint.ui.adapter.WelcomeViewModel;
import com.github.lorenj.wordtint.utils.ZipUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

@Database(entities = {
        WordBookEntity.class, WordBookSectionEntity.class, WordBookSectionWordIdEntity.class,
        UserSettingEntity.class, WordOriginEntity.class, WordStarEntity.class,
        WordStarWordIdEntity.class, WordSearchEntity.class, ReciteRecordEntity.class,
        ReciteRecordWordEntity.class, ReciteRecordWordMarkEntity.class, WordNoteEntity.class,
        WordMarkLogEntity.class}, version = 8)
public abstract class APPDatabase extends RoomDatabase {

    private static int total = 0, completed = 0;

    private static volatile APPDatabase INSTANCE = null;

    public abstract WordBookDao wordBookDao();

    public abstract WordBookSectionDao wordBookSectionDao();

    public abstract UserSettingDao userSettingDao();

    public abstract WordOriginDao wordOriginDao();

    public abstract WordStarDao wordStarDao();

    public abstract WordStarWordIdDao wordStarWordIdDao();

    public abstract WordSearchDao wordSearchDao();

    public abstract ReciteRecordDao reciteRecordDao();

    public abstract ReciteRecordWordDao reciteRecordWordDao();

    public abstract ReciteRecordMarkDao reciteRecordMarkDao();

    public abstract WordNoteDao wordNoteDao();

    public abstract WordMarkLogDao wordMarkLogDao();

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
        database.runInTransaction(() -> {
            try {
                SupportSQLiteDatabase db =
                        database.getOpenHelper().getWritableDatabase();

                String[] files = Optional.ofNullable(context.getAssets().list("sql"))
                        .orElse(new String[0]);

                total = files.length + 1;
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
                db.execSQL("UPDATE word_origin SET value = REPLACE(value, '\\n', char(10))");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // 解压音频文件
        File outputDir = new File(context.getFilesDir(), "");
        try {
            ZipUtils.unzipFromAssets(context, "audio/audio.zip", outputDir);
            completed++;
            int progress = completed * 100 / total;
            callback.onProgress(progress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
