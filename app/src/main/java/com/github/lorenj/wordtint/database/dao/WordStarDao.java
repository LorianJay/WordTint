package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.github.lorenj.wordtint.database.entity.WordStarEntity;
import com.github.lorenj.wordtint.database.entity.relation.WordStarWithWordIdEntity;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2026/1/29 20:24
 */
@Dao
public interface WordStarDao {
    @Insert
    long insert(WordStarEntity wordStarEntity);

    @Update
    int update(WordStarEntity wordStarEntity);

    @Delete
    void delete(WordStarEntity wordStarEntity);

    @Update
    void batchUpdate(List<WordStarEntity> wordStarEntityList);

    /**
     * 查询所有的收藏夹以及每个收藏夹对应的单词id列表
     */
    @Transaction
    @Query("SELECT * FROM word_star order by `order`")
    List<WordStarWithWordIdEntity> findAllStarAndWordId();

}
