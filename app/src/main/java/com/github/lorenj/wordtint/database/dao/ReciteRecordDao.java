package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.lorenj.wordtint.database.entity.ReciteRecordEntity;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2026/2/4 21:37
 */
@Dao
public interface ReciteRecordDao {
    @Insert
    long insertReciteRecord(ReciteRecordEntity reciteRecordEntity);

    @Query("select count(1) from recite_record")
    int countReciteRecord();

    @Query("SELECT * FROM recite_record LIMIT :arg0,:arg1")
    List<ReciteRecordEntity> findReciteRecordWithLimit(int arg0, int arg1);

    @Delete
    void delete(ReciteRecordEntity reciteRecordEntity);

}
