package com.github.lorenj.wordtint.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.WordStructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordOriginEditDialog {

    public interface OnOriginEditListener {
        void onSave(FunctionWordVO word, Map<String, String> newCustomValues);
    }

    public static void show(Context context, FunctionWordVO word,
                            Map<String, String> originalValues,
                            OnOriginEditListener listener) {
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_edit_word_origin, null);

        TextView wordOriginText = dialogView.findViewById(R.id.tv_edit_origin_word);
        LinearLayout originListContainer = dialogView.findViewById(R.id.ll_edit_origin_list);

        // 显示单词原文
        String origin = word.getValue().get(WordStructure.WORD_ORIGIN);
        wordOriginText.setText(origin != null ? origin : "");

        // 按 WordStructure.order 排序，展示所有词性条目（含空值）
        WordStructure[] allStructures = WordStructure.values();
        Arrays.sort(allStructures, Comparator.comparingInt(s -> s.getOrder()));

        List<OriginItem> items = new ArrayList<>();
        List<EditText> editTexts = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(context);

        for (WordStructure structure : allStructures) {
            if (structure == WordStructure.WORD_ORIGIN) continue;
            String keyName = structure.name();
            String label = context.getString(structure.getKeyHint());
            String currentValue = word.getValue().get(structure);
            items.add(new OriginItem(keyName, label, currentValue));

            View itemView = inflater.inflate(R.layout.item_edit_word_origin, originListContainer, false);
            TextView labelText = itemView.findViewById(R.id.tv_origin_label);
            EditText valueEditText = itemView.findViewById(R.id.et_origin_value);
            labelText.setText(label);
            valueEditText.setText(currentValue != null ? currentValue : "");
            editTexts.add(valueEditText);
            originListContainer.addView(itemView);
        }

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
            Map<String, String> newCustomValues = new HashMap<>();
            for (int i = 0; i < items.size(); i++) {
                String newValue = editTexts.get(i).getText().toString().trim();
                String effectiveOldValue = items.get(i).currentValue;
                String defaultOriginal = originalValues.get(items.get(i).key);
                // 仅保存与对话框打开时不同的字段
                if (!newValue.equals(effectiveOldValue != null ? effectiveOldValue : "")) {
                    if (newValue.equals(defaultOriginal != null ? defaultOriginal : "")) {
                        // 还原为默认词义 — 用 null 清除 custom_value
                        newCustomValues.put(items.get(i).key, null);
                    } else {
                        newCustomValues.put(items.get(i).key, newValue.isEmpty() ? null : newValue);
                    }
                }
            }
            listener.onSave(word, newCustomValues);
            dialog.dismiss();
        });

        // 还原为默认词义 — 用原始值填充输入框，不关闭对话框
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
            for (int i = 0; i < items.size(); i++) {
                String defaultValue = originalValues.get(items.get(i).key);
                editTexts.get(i).setText(defaultValue != null ? defaultValue : "");
            }
        });
    }

    private static class OriginItem {
        final String key;
        final String label;
        final String currentValue;

        OriginItem(String key, String label, String currentValue) {
            this.key = key;
            this.label = label;
            this.currentValue = currentValue;
        }
    }
}
