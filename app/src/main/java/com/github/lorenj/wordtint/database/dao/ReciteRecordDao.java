package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.github.lorenj.wordtint.database.entity.ReciteRecordEntity;
import com.github.lorenj.wordtint.database.entity.ReciteRecordWordEntity;
import com.github.lorenj.wordtint.database.entity.ReciteRecordWordMarkEntity;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2026/2/4 21:37
 */
@Dao
public interface ReciteRecordDao {
    @Insert
    long insertReciteRecord(ReciteRecordEntity reciteRecordEntity);

    @Insert
    List<Long> batchInsertReciteRecordWord(List<ReciteRecordWordEntity> recordWordEntityList);

    @Insert
    void batchInsertReciteRecordWordMark(List<ReciteRecordWordMarkEntity> wordMarkEntityList);

}
