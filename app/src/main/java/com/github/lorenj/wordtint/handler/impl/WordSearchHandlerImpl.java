package com.github.lorenj.wordtint.handler.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.lorenj.wordtint.entity.dto.DataPage;
import com.github.lorenj.wordtint.entity.dto.SearchWordParam;
import com.github.lorenj.wordtint.entity.local.WordDTOLocal;
import com.github.lorenj.wordtint.handler.WordSearchHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cnsukidayo
 * @date 2024/7/21 10:49
 */
public class WordSearchHandlerImpl extends SQLiteOpenHelper implements WordSearchHandler {

    private final Map<Long, WordDTOLocal> dict;

    public WordSearchHandlerImpl(Context context, Map<Long, WordDTOLocal> dict) {
        super(context, "search.db", null, 3);
        this.dict = dict;
    }

    @Override
    public DataPage<WordDTOLocal> searchWord(SearchWordParam searchWordParam) {
        DataPage<WordDTOLocal> result = new DataPage<>();
        List<WordDTOLocal> data = new ArrayList<>();
        String searchSql = "SELECT * FROM \"search\" WHERE word like ? LIMIT ?,?";
        String countSql = "SELECT count(1) FROM \"search\" WHERE word like ?";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] searchSqlArgs = new String[]{
                "%" + searchWordParam.getWord() + "%",
                String.valueOf((searchWordParam.getCurrent() - 1) * searchWordParam.getSize()),
                String.valueOf((searchWordParam.getCurrent() * searchWordParam.getSize()))};
        String[] countArgs = new String[]{"%" + searchWordParam.getWord() + "%"};
        // 查询所有单词
        Cursor searchSqlCursor = sqLiteDatabase.rawQuery(searchSql, searchSqlArgs);
        int idIndex = searchSqlCursor.getColumnIndex("id");
        while (searchSqlCursor.moveToNext()) {
            data.add(dict.get((long) searchSqlCursor.getInt(idIndex)));
        }
        // 查询当前单词的数量
        Cursor countCursor = sqLiteDatabase.rawQuery(countSql, countArgs);
        countCursor.moveToNext();
        int count = countCursor.getInt(0);
        if (count - searchWordParam.getCurrent() * 20 <= 0) {
            result.setLast(true);
        }
        result.setFirst(searchWordParam.getCurrent() == 1);
        sqLiteDatabase.close();
        result.setContent(data);
        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE search (\"word\" TEXT NOT NULL COLLATE NOCASE,\"id\" INTEGER NOT NULL COLLATE RTRIM, PRIMARY KEY (\"word\"));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
