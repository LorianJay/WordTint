package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.lorenj.wordtint.database.entity.ReciteRecordWordMarkEntity;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2026/2/4 21:37
 */
@Dao
public interface ReciteRecordMarkDao {
    @Insert
    void batchInsertReciteRecordWordMark(List<ReciteRecordWordMarkEntity> wordMarkEntityList);

    @Query("delete from recite_record_word_mark where record_word_id in(:recordWordIdList)")
    void deleteByRecordWordId(List<Integer> recordWordIdList);

    @Query("select * from recite_record_word_mark where record_word_id in(:recordWordIdList)")
    List<ReciteRecordWordMarkEntity> findAllByRecordWordId(List<Integer> recordWordIdList);

}
