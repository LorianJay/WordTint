package com.github.lorenj.wordtint.handler.impl;

import com.github.lorenj.wordtint.entity.Word;
import com.github.lorenj.wordtint.handler.AssociationModeHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author cnsukidayo
 * @date 2023/2/11 19:02
 */
public class AssociationModeHandlerImpl implements AssociationModeHandler {

    private final Map<String, Word> wordMap;
    private final LinkedList<Word> rememberLinked;
    private Optional<Word> currentWord = Optional.empty();

    public AssociationModeHandlerImpl(List<Word> allWords) {
        rememberLinked = new LinkedList<>();
        wordMap = new HashMap<>(allWords.size());
        for (int i = 0; i < allWords.size(); i++) {
            wordMap.put(allWords.get(i).getWordOrigin(), allWords.get(i));
        }
    }

    @Override
    public Optional<Word> checkWord(String wordOrigin) {
        return currentWord = Optional.ofNullable(wordMap.remove(wordOrigin));
    }

    @Override
    public Optional<Word> getCurrentWord() {
        return currentWord;
    }


}
