package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.lorenj.wordtint.database.entity.WordOriginEntity;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2026/1/29 20:24
 */
@Dao
public interface WordOriginDao {
    @Query("SELECT * FROM word_origin WHERE word_id in(:wordIdList)")
    List<WordOriginEntity> findAllOriginWordByIdList(List<Integer> wordIdList);

    @Query("SELECT * FROM word_origin WHERE word_id in(:wordId)")
    List<WordOriginEntity> findAllOriginWordById(int wordId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<WordOriginEntity> entity);

    @Query("DELETE FROM word_origin WHERE word_id = :wordId and `key` in (:keyList)")
    void deleteAllByWordIdAndKey(int wordId, List<String> keyList);

    @Insert
    long insert(WordOriginEntity wordOriginEntity);

    @Query("DELETE FROM word_origin WHERE word_id = :wordId")
    void deleteAllByWordId(int wordId);
}
