package com.github.lorenj.wordtint.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 单词标记记录日志
 *
 * @author cnsukidayo
 * @date 2026/7/26
 */
@Entity(tableName = "word_mark_log")
public class WordMarkLogEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "mark_word_id")
    public int markWordId;

    @ColumnInfo(name = "mark_color")
    public String markColor;

    @ColumnInfo(name = "timestamp")
    public long timestamp;

    @ColumnInfo(name = "stay_time")
    public long stayTime;

}
