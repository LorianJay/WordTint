package com.github.lorenj.wordtint.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.database.dao.WordBookDao;
import com.github.lorenj.wordtint.database.entity.WordBook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Database(entities = {WordBook.class}, version = 2)
public abstract class WordBookDatabase extends RoomDatabase {

    private static volatile WordBookDatabase INSTANCE;

    public abstract WordBookDao wordBookDao();

    public static WordBookDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (WordBookDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    WordBookDatabase.class,
                                    "app_db.db"
                            )
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    StaticFactory.getExecutorService().execute(() -> {
                                        try {
                                            InputStream is = context.getAssets().open("sql/word_book.sql");
                                            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                                            String line;
                                            StringBuilder sql = new StringBuilder();

                                            while ((line = reader.readLine()) != null) {
                                                sql.append(line);
                                                if (line.trim().endsWith(";")) {
                                                    db.execSQL(sql.toString());
                                                    sql.setLength(0);
                                                }
                                            }
                                            reader.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
