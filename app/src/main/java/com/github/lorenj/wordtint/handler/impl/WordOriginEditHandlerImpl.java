package com.github.lorenj.wordtint.handler.impl;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.factory.StaticFactory;
import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.entity.WordOriginEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.WordOriginEditHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordOriginEditHandlerImpl implements WordOriginEditHandler {

    /**
     * 上下文
     */
    private final Context context;

    public WordOriginEditHandlerImpl(Context context) {
        this.context = context;
    }

    @Override
    public void edit(FunctionWordVO functionWordVO, Runnable confirm) {
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_edit_word_origin, null);
        LayoutInflater inflater = LayoutInflater.from(context);

        TextView wordOriginText = dialogView.findViewById(R.id.tv_edit_origin_word);
        LinearLayout originListLayout = dialogView.findViewById(R.id.ll_edit_origin_list);

        WordOriginEntity wordOriginTextEntity = functionWordVO.getValue().get(WordStructure.WORD_ORIGIN);
        String originText = Optional.ofNullable(wordOriginTextEntity)
                .map(tmp -> tmp.value)
                .orElse("");
        wordOriginText.setText(originText);
        // 排序
        List<WordStructure> displayStruct = Arrays.stream(WordStructure.values())
                .filter(wordStructure -> wordStructure != WordStructure.WORD_ORIGIN)
                .sorted(Comparator.comparingInt(WordStructure::getOrder))
                .collect(Collectors.toList());
        // 所有的编辑框
        Map<WordStructure, EditText> editTextMap = new HashMap<>();
        displayStruct.forEach(wordStructure -> {
            View itemView = inflater.inflate(R.layout.item_edit_word_origin, originListLayout, false);
            TextView labelText = itemView.findViewById(R.id.tv_origin_label);
            EditText valueEditText = itemView.findViewById(R.id.et_origin_value);

            String label = context.getString(wordStructure.getKeyHint());
            WordOriginEntity wordOriginEntity = functionWordVO.getValue().get(wordStructure);
            // 优先显示自定义文本
            String displayValue = Optional.ofNullable(wordOriginEntity)
                    .flatMap(tmp -> Optional.ofNullable(tmp.customValue))
                    .or(() -> Optional.ofNullable(wordOriginEntity).map(tmp -> tmp.value))
                    .orElse("");
            labelText.setText(label);
            valueEditText.setText(displayValue);
            editTextMap.put(wordStructure, valueEditText);
            originListLayout.addView(itemView);
        });

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.edit_origin)
                .setView(dialogView)
                .setPositiveButton(R.string.save, null)
                .setNeutralButton(R.string.restore_default_origin, null)
                .setNegativeButton(R.string.cancel, null)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        dialog.show();

        // 保存按钮
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            Map<WordStructure, WordOriginEntity> wordStructureMap = functionWordVO.getValue();
            // 将所有修改的单词更新到functionWordVO
            displayStruct.stream()
                    .filter(wordStructure -> Optional.ofNullable(editTextMap.get(wordStructure))
                            .map(EditText::getText)
                            .map(CharSequence::toString)
                            .map(String::trim)
                            .filter(text -> !TextUtils.isEmpty(text))
                            .isPresent())
                    .forEach(wordStructure -> {
                        WordOriginEntity innerWordOriginEntity = Optional.ofNullable(wordStructureMap.get(wordStructure))
                                .orElseGet(() -> {
                                    WordOriginEntity result = new WordOriginEntity();
                                    result.wordId = functionWordVO.getWordId();
                                    result.key = wordStructure.name();
                                    wordStructureMap.put(wordStructure, result);
                                    return result;
                                });
                        innerWordOriginEntity.customValue = Optional.ofNullable(editTextMap.get(wordStructure))
                                .map(EditText::getText)
                                .map(CharSequence::toString)
                                .map(String::trim)
                                .orElse("");
                    });
            // 遍历出所有需要删除的key
            List<WordStructure> needDelete = displayStruct.stream()
                    .filter(wordStructure -> Optional.ofNullable(editTextMap.get(wordStructure))
                            .map(EditText::getText)
                            .map(CharSequence::toString)
                            .map(String::trim)
                            .filter(TextUtils::isEmpty)
                            .isPresent())
                    .collect(Collectors.toList());
            needDelete.forEach(wordStructureMap::remove);
            // 更新所有的单词
            StaticFactory.getExecutorService().execute(() -> {
                ArrayList<WordOriginEntity> currentAllWordOrigin = new ArrayList<>(wordStructureMap.values());
                APPDatabase.getInstance(context)
                        .wordOriginDao()
                        .upsertAll(currentAllWordOrigin);
                APPDatabase.getInstance(context)
                        .wordOriginDao()
                        .deleteAllByWordIdAndKey(functionWordVO.getWordId(), needDelete.stream()
                                .map(Enum::name)
                                .collect(Collectors.toList()));
                confirm.run();
            });
            dialog.dismiss();
        });

        // 还原为默认词义 — 用原始值填充输入框
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
            Map<WordStructure, WordOriginEntity> wordStructureMap = functionWordVO.getValue();
            displayStruct.forEach(wordStructure -> {
                WordOriginEntity wordOriginEntity = wordStructureMap.get(wordStructure);
                String displayValue = Optional.ofNullable(wordOriginEntity)
                        .filter(tmp -> !TextUtils.isEmpty(tmp.value))
                        .map(tmp -> tmp.value)
                        .orElse("");
                Optional.ofNullable(editTextMap.get(wordStructure))
                        .ifPresent(editText -> editText.setText(displayValue));
            });
        });
    }
}
