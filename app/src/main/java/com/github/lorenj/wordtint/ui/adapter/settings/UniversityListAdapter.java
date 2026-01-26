package com.github.lorenj.wordtint.ui.adapter.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.entity.dto.UniversityDTO;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.github.lorenj.wordtint.R;

import java.util.ArrayList;
import java.util.List;


public class UniversityListAdapter extends RecyclerView.Adapter<UniversityListAdapter.RecyclerViewHolder>
        implements RecyclerViewAdapterItemChange<UniversityDTO> {

    private final Context context;
    // 所有学校的列表
    private List<UniversityDTO> allUniversityList = new ArrayList<>();

    /**
     * 设置点击子划分的回调事件
     */
    private RecycleViewItemClickCallBack<UniversityDTO> recycleViewItemOnClickListener;

    public UniversityListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.account_profile_fragment_university_element, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UniversityDTO universityDTO = allUniversityList.get(position);
        holder.universityName.setText(universityDTO.getSchoolName());
    }

    @Override
    public int getItemCount() {
        return allUniversityList.size();
    }

    @Override
    public void addItem(UniversityDTO item) {

    }

    @Override
    public void removeItem(UniversityDTO item) {

    }

    /**
     * 展示所有的学校列表
     *
     * @param universityDTOList 学校列表不为null
     */
    public void showAllUniversity(List<UniversityDTO> universityDTOList) {
        int preCount = allUniversityList.size();
        this.allUniversityList = universityDTOList;
        notifyItemRangeChanged(0, Math.max(getItemCount(), preCount));
    }

    public void setRecycleViewItemOnClickListener(RecycleViewItemClickCallBack<UniversityDTO> recycleViewItemOnClickListener) {
        this.recycleViewItemOnClickListener = recycleViewItemOnClickListener;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView universityName;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.universityName = itemView.findViewById(R.id.account_profile_fragment_university_name);
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recycleViewItemOnClickListener.viewClickCallBack(allUniversityList.get(getAdapterPosition()));
        }
    }


}
