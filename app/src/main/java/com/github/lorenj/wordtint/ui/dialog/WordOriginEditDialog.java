package com.github.lorenj.wordtint.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.WordStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordOriginEditDialog {

    public interface OnOriginEditListener {
        void onSave(FunctionWordVO word, Map<String, String> newCustomValues);
        void onRestore(FunctionWordVO word);
    }

    public static void show(Context context, FunctionWordVO word,
                            OnOriginEditListener listener) {
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_edit_word_origin, null);

        TextView wordOriginText = dialogView.findViewById(R.id.tv_edit_origin_word);
        RecyclerView originList = dialogView.findViewById(R.id.rv_edit_origin_list);
        Button restoreButton = dialogView.findViewById(R.id.btn_restore_default);

        // 显示单词原文
        String origin = word.getValue().get(WordStructure.WORD_ORIGIN);
        wordOriginText.setText(origin != null ? origin : "");

        // 构建词义条目列表
        List<OriginItem> items = new ArrayList<>();
        for (Map.Entry<WordStructure, String> entry : word.getValue().entrySet()) {
            if (entry.getKey() == WordStructure.WORD_ORIGIN) continue;
            String label = context.getString(entry.getKey().getKeyHint());
            items.add(new OriginItem(entry.getKey(), label, entry.getValue()));
        }

        OriginItemAdapter adapter = new OriginItemAdapter(items);
        originList.setLayoutManager(new LinearLayoutManager(context));
        originList.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.edit_origin)
                .setView(dialogView)
                .setPositiveButton(R.string.save, null) // 先设为 null，在 show 后手动处理
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.show();

        // 手动设置保存按钮点击，防止自动关闭
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            Map<String, String> newCustomValues = new HashMap<>();
            for (int i = 0; i < originList.getChildCount(); i++) {
                RecyclerView.ViewHolder holder = originList.getChildViewHolder(originList.getChildAt(i));
                if (holder instanceof OriginItemAdapter.ViewHolder) {
                    OriginItemAdapter.ViewHolder vh = (OriginItemAdapter.ViewHolder) holder;
                    String newValue = vh.valueEditText.getText().toString().trim();
                    String originalValue = items.get(i).currentValue;
                    if (!newValue.equals(originalValue != null ? originalValue : "")) {
                        newCustomValues.put(items.get(i).key.name(), newValue.isEmpty() ? null : newValue);
                    }
                }
            }
            listener.onSave(word, newCustomValues);
            dialog.dismiss();
        });

        // 还原按钮
        restoreButton.setOnClickListener(v -> {
            listener.onRestore(word);
            dialog.dismiss();
        });
    }

    private static class OriginItem {
        final WordStructure key;
        final String label;
        final String currentValue;

        OriginItem(WordStructure key, String label, String currentValue) {
            this.key = key;
            this.label = label;
            this.currentValue = currentValue;
        }
    }

    private static class OriginItemAdapter extends RecyclerView.Adapter<OriginItemAdapter.ViewHolder> {

        private final List<OriginItem> items;

        OriginItemAdapter(List<OriginItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_edit_word_origin, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OriginItem item = items.get(position);
            holder.labelText.setText(item.label);
            holder.valueEditText.setText(item.currentValue);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView labelText;
            final EditText valueEditText;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                labelText = itemView.findViewById(R.id.tv_origin_label);
                valueEditText = itemView.findViewById(R.id.et_origin_value);
            }
        }
    }
}
