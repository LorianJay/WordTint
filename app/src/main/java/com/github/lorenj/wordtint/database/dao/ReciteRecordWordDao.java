package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.lorenj.wordtint.database.entity.ReciteRecordWordEntity;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2026/2/4 21:37
 */
@Dao
public interface ReciteRecordWordDao {
    @Insert
    List<Long> batchInsertReciteRecordWord(List<ReciteRecordWordEntity> recordWordEntityList);

    @Query("select * from recite_record_word where record_id = :recordId")
    List<ReciteRecordWordEntity> findByRecordId(int recordId);

    @Query("delete from recite_record_word where record_id = :recordId")
    void deleteByRecordId(int recordId);

}
