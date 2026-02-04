package com.github.lorenj.wordtint.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author cnsukidayo
 * @date 2026/2/4 21:30
 */
@Entity(tableName = "recite_record")
public class ReciteRecordEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "create_time")
    public long createTime;

    @ColumnInfo(name = "word_count")
    public int wordCount;

    @ColumnInfo(name = "recite_mode")
    public String reciteMode;

    @ColumnInfo(name = "recite_order")
    public String reciteOrder;

    @ColumnInfo(name = "recite_filer")
    public String reciteFiler;

    @ColumnInfo(name = "hide_preposition")
    public boolean hidePreposition;

}
