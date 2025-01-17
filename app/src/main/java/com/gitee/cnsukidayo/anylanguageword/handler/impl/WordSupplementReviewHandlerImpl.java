package com.gitee.cnsukidayo.anylanguageword.handler.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gitee.cnsukidayo.anylanguageword.entity.local.AddWordReViewParamLocal;
import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;
import com.gitee.cnsukidayo.anylanguageword.handler.WordSupplementReviewHandler;

import java.util.ArrayList;

/**
 * @author cnsukidayo
 * @date 2024/7/21 10:49
 */
public class WordSupplementReviewHandlerImpl extends SQLiteOpenHelper implements WordSupplementReviewHandler {

    public WordSupplementReviewHandlerImpl(Context context) {
        super(context, "word_review.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE \"main\".\"word_review\" (\n" +
                "  \"id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "  \"word_id\" INTEGER NOT NULL,\n" +
                "  \"word_flag\" text NOT NULL\n" +
                ");");
        db.execSQL("CREATE INDEX \"word_id_index\"\n" +
                "ON \"word_review\" (\n" +
                "  \"word_id\" ASC\n" +
                ");");
        db.execSQL("CREATE INDEX \"word_flag_index\"\n" +
                "ON \"word_review\" (\n" +
                "  \"word_flag\" ASC\n" +
                ");");
    }


    @Override
    public void insertWordReView(AddWordReViewParamLocal addWordAnalysisParamLocal) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        // 先搜索目标单词是否存在
        String existWord = "SELECT * FROM word_review WHERE word_id = ?";
        Cursor existCursor = writableDatabase.rawQuery(existWord, new String[]{String.valueOf(addWordAnalysisParamLocal.getId())});
        if (existCursor.moveToNext()) {
            return;
        }
        // 插入单词
        String insertSql = "INSERT INTO word_review(word_id,word_flag) VALUES(?,?);";
        for (FlagColor flagColor : addWordAnalysisParamLocal.getWordFlag()) {
            writableDatabase.execSQL(insertSql, new Object[]{
                    addWordAnalysisParamLocal.getId(),
                    flagColor.name()
            });
        }
        writableDatabase.close();
    }

    @Override
    public void deleteWordReView(int wordId) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String deleteWord = "SELECT * FROM word_review WHERE word_id = ?";
        writableDatabase.rawQuery(deleteWord, new String[]{String.valueOf(wordId)});
        writableDatabase.close();
    }

    @Override
    public ArrayList<Long> querySupplementByFlagColor(FlagColor flagColor) {
        ArrayList<Long> result = new ArrayList<>();
        String searchSql = "SELECT * FROM word_review WHERE word_flag = ? GROUP BY word_id";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        // 查询所有单词
        Cursor searchSqlCursor = sqLiteDatabase.rawQuery(searchSql, new String[]{flagColor.name()});
        int wordIdIndex = searchSqlCursor.getColumnIndex("word_id");
        while (searchSqlCursor.moveToNext()) {
            result.add(searchSqlCursor.getLong(wordIdIndex));
        }
        sqLiteDatabase.close();
        return result;
    }

    @Override
    public int countSupplementFlagColor(FlagColor flagColor) {
        String countSql = "SELECT COUNT(*) FROM(SELECT word_id FROM word_review WHERE word_flag = ? GROUP BY word_id);";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        // 查询当前单词的数量
        Cursor countCursor = sqLiteDatabase.rawQuery(countSql, new String[]{flagColor.name()});
        countCursor.moveToNext();
        int count = countCursor.getInt(0);
        sqLiteDatabase.close();
        return count;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
