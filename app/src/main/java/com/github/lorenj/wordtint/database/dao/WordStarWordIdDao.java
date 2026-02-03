package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.github.lorenj.wordtint.database.entity.WordStarWordIdEntity;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2026/1/29 20:24
 */
@Dao
public interface WordStarWordIdDao {
    @Insert
    long insert(WordStarWordIdEntity wordStarWordIdEntity);

    @Delete
    void delete(WordStarWordIdEntity wordStarWordIdEntity);

    @Query("DELETE FROM word_star_word_id WHERE star_id = :starId")
    void deleteWordIdByStarId(int starId);

    @Update
    void batchUpdate(List<WordStarWordIdEntity> wordStarWordIdEntityList);
}
