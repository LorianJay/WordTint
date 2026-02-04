package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
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
}
