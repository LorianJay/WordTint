package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
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

    @Query("UPDATE word_origin SET custom_value = :customValue WHERE word_id = :wordId AND key = :key")
    int updateCustomValue(int wordId, String key, String customValue);

    @Query("UPDATE word_origin SET custom_value = NULL WHERE word_id = :wordId")
    void resetCustomValue(int wordId);

    @Insert
    long insert(WordOriginEntity entity);
}
