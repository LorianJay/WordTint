package com.github.lorenj.wordtint.database.entity.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.github.lorenj.wordtint.database.entity.WordStarEntity;
import com.github.lorenj.wordtint.database.entity.WordStarWordIdEntity;

import java.util.List;

public class WordStarWithWordIdEntity {
    @Embedded
    public WordStarEntity wordStarEntity;

    @Relation(
        parentColumn = "id",
        entityColumn = "star_id"
    )
    public List<WordStarWordIdEntity> wordStarWordIdEntityList;

    public WordStarEntity getWordStarEntity() {
        return wordStarEntity;
    }

    public void setWordStarEntity(WordStarEntity wordStarEntity) {
        this.wordStarEntity = wordStarEntity;
    }

    public List<WordStarWordIdEntity> getWordStarWordIdEntityList() {
        return wordStarWordIdEntityList;
    }

    public void setWordStarWordIdEntityList(List<WordStarWordIdEntity> wordStarWordIdEntityList) {
        this.wordStarWordIdEntityList = wordStarWordIdEntityList;
    }
}
