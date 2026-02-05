package com.github.lorenj.wordtint.ui.adapter.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.database.entity.ReciteRecordEntity;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;

import java.util.ArrayList;
import java.util.List;

public class RecordMarkAdapter extends RecyclerView.Adapter<RecordMarkAdapter.RecordToastViewHolder>
        implements RecyclerViewAdapterItemChange<ReciteRecordEntity> {

    private final Context context;
    private final List<ReciteRecordEntity> allReciteRecordEntityList = new ArrayList<>();

    public RecordMarkAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecordToastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordToastViewHolder(LayoutInflater.from(context).inflate(R.layout.item_record_toast, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordToastViewHolder holder, @SuppressLint("RecyclerView") int position) {
    }

    @Override
    public int getItemCount() {
        return allReciteRecordEntityList.size();
    }


    public class RecordToastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;

        public RecordToastViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        @Override
        public void onClick(View v) {
        }
    }


}
