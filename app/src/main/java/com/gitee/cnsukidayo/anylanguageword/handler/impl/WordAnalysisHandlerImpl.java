package com.gitee.cnsukidayo.anylanguageword.handler.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gitee.cnsukidayo.anylanguageword.entity.local.AddWordAnalysisParamLocal;
import com.gitee.cnsukidayo.anylanguageword.entity.local.WordAnalysisLocal;
import com.gitee.cnsukidayo.anylanguageword.entity.local.WordFlagRankLocal;
import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;
import com.gitee.cnsukidayo.anylanguageword.handler.WordAnalysisHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.cnsukidayo.wword.model.dto.support.DataPage;
import io.github.cnsukidayo.wword.model.params.PageQueryParam;

/**
 * @author cnsukidayo
 * @date 2024/7/21 10:49
 */
public class WordAnalysisHandlerImpl extends SQLiteOpenHelper implements WordAnalysisHandler {

    public WordAnalysisHandlerImpl(Context context) {
        super(context, "word_analysis.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE  word_analysis (\n" +
                "  \"id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "  \"word_id\" INTEGER NOT NULL,\n" +
                "  \"word_flag\" text NOT NULL,\n" +
                "  \"create_timestamp\" text NOT NULL\n" +
                ");\n");
        db.execSQL("CREATE INDEX \"word_id_index\"\n" +
                "ON \"word_analysis\" (\n" +
                "  \"word_id\" ASC\n" +
                ");");
        db.execSQL("CREATE INDEX \"create_timestamp_index\"\n" +
                "ON \"word_analysis\" (\n" +
                "  \"create_timestamp\" DESC\n" +
                ");");
        db.execSQL("CREATE INDEX \"word_flag_index\"\n" +
                "ON \"word_analysis\" (\n" +
                "  \"word_flag\" ASC\n" +
                ");");
    }

    @Override
    public WordAnalysisLocal queryWordAnalysis(int id) {
        WordAnalysisLocal result = new WordAnalysisLocal();
        Map<FlagColor, WordAnalysisLocal.FlagColorMapInfo> map = new HashMap<>();
        SQLiteDatabase reader = this.getReadableDatabase();
        String lastRecordSql = "SELECT * from word_analysis WHERE word_id = ? and word_flag = ?  ORDER BY create_timestamp DESC LIMIT 1";
        String totalSql = "SELECT COUNT(*)  FROM word_analysis WHERE word_id = ? and word_flag = ?";
        for (FlagColor flagColor : FlagColor.values()) {
            if (flagColor == FlagColor.GREEN || flagColor == FlagColor.BROWN) {
                continue;
            }
            WordAnalysisLocal.FlagColorMapInfo flagColorMapInfo = new WordAnalysisLocal.FlagColorMapInfo();
            // 查询最后一次标记的时间点
            Cursor lastCursor = reader.rawQuery(lastRecordSql, new String[]{
                    String.valueOf(id),
                    flagColor.name()});
            int createTimestampIndex = lastCursor.getColumnIndex("create_timestamp");
            //  如果没有最后一次标记的时间点,则跳过当前
            if (!lastCursor.moveToNext()) {
                continue;
            }
            flagColorMapInfo.setRecentCreateTimestamp(Long.parseLong(lastCursor.getString(createTimestampIndex)));
            // 查询总数
            Cursor totalCursor = reader.rawQuery(totalSql, new String[]{
                    String.valueOf(id),
                    flagColor.name()});
            totalCursor.moveToNext();
            flagColorMapInfo.setTotal(totalCursor.getInt(0));
            map.put(flagColor, flagColorMapInfo);
        }
        reader.close();
        result.setMapMessage(map);
        return result;
    }

    @Override
    public void insertWordAnalysis(AddWordAnalysisParamLocal addWordAnalysisParamLocal) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String insertSql = "INSERT INTO word_analysis(word_id,word_flag,create_timestamp) VALUES(?,?,?);";
        for (FlagColor flagColor : addWordAnalysisParamLocal.getWordFlag()) {
            writableDatabase.execSQL(insertSql, new Object[]{
                    addWordAnalysisParamLocal.getId(),
                    flagColor.name(),
                    addWordAnalysisParamLocal.getCreateTimestamp()
            });
        }
        writableDatabase.close();
    }

    @Override
    public DataPage<WordFlagRankLocal> pageQueryFlagRankByFlagColor(FlagColor flagColor, PageQueryParam pageQueryParam) {
        DataPage<WordFlagRankLocal> result = new DataPage<>();
        List<WordFlagRankLocal> data = new ArrayList<>();
        String searchSql = "select * from (SELECT COUNT(*) as count,word_id FROM \"word_analysis\" WHERE word_flag = ? GROUP BY word_id) ORDER BY count DESC LIMIT ?,?";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        // 查询所有单词
        Cursor searchSqlCursor = sqLiteDatabase.rawQuery(searchSql, new String[]{
                flagColor.name(),
                String.valueOf((pageQueryParam.getCurrent() - 1) * pageQueryParam.getSize()),
                String.valueOf((pageQueryParam.getCurrent() * pageQueryParam.getSize()))
        });
        int countIndex = searchSqlCursor.getColumnIndex("count");
        int wordIdIndex = searchSqlCursor.getColumnIndex("word_id");
        while (searchSqlCursor.moveToNext()) {
            WordFlagRankLocal wordFlagRankLocal = new WordFlagRankLocal();
            wordFlagRankLocal.setCount(searchSqlCursor.getInt(countIndex));
            wordFlagRankLocal.setWordId(searchSqlCursor.getInt(wordIdIndex));
            data.add(wordFlagRankLocal);
        }
        int count = this.countFlagRankByFlagColor(flagColor);
        if (count - pageQueryParam.getCurrent() * 20 <= 0) {
            result.setLast(true);
        }
        result.setFirst(pageQueryParam.getCurrent() == 1);
        sqLiteDatabase.close();
        result.setContent(data);
        return result;
    }

    @Override
    public ArrayList<Long> queryFlagRankByFlagColor(FlagColor flagColor) {
        ArrayList<Long> result = new ArrayList<>();
        String searchSql = "SELECT * FROM \"word_analysis\" WHERE word_flag = ? GROUP BY word_id ORDER BY create_timestamp ASC";
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
    public int countFlagRankByFlagColor(FlagColor flagColor) {
        String countSql = "SELECT COUNT(*) FROM(SELECT word_id FROM word_analysis WHERE word_flag = ? GROUP BY word_id);";
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
