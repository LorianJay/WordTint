package com.github.lorenj.wordtint.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.context.AnyLanguageWordProperties;
import com.github.lorenj.wordtint.context.pathsystem.document.WordContextPath;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.entity.dto.LanguageClassDTO;
import com.github.lorenj.wordtint.entity.dto.UserProfileDTO;
import com.github.lorenj.wordtint.entity.local.DivideDTOLocal;
import com.github.lorenj.wordtint.ui.adapter.book.BookSectionListAdapter;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.github.lorenj.wordtint.utils.JsonUtils;
import com.github.lorenj.wordtint.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * 显示单词划分的Fragment
 */
public class DivideFragment extends Fragment {

    private View rootView;
    private RecyclerView divideRecyclerView;
    private LinearLayoutManager divideLayoutManager;
    private BookSectionListAdapter bookSectionListAdapter;
    /**
     * 当前要展示的哪个语种下的所有划分
     */
    private LanguageClassDTO languageClassDTO;

    /**
     * 用于保存当前用户的个人信息
     */
    private UserProfileDTO userProfileDTO;

    /**
     * 更新UI的handler
     */
    private final Handler updateUIHandler = new Handler();

    /**
     * RecycleView回调的事件
     */
    private RecycleViewItemClickCallBack<DivideDTOLocal> recycleViewItemOnClickListener;
    private ArrayList<DivideDTOLocal> allWordList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_single_recycle_view, container, false);
        bindView();
        initView();
        requestData();
        return rootView;
    }

    /**
     * 设置当前要展示的语种
     *
     * @param languageClassDTO 语种对象不为null
     */
    public void setLanguageClassDTO(LanguageClassDTO languageClassDTO) {
        this.languageClassDTO = languageClassDTO;
    }

    public void setRecycleViewItemOnClickListener(RecycleViewItemClickCallBack<DivideDTOLocal> recycleViewItemOnClickListener) {
        this.recycleViewItemOnClickListener = recycleViewItemOnClickListener;
    }

    public RecyclerView getDivideRecyclerView() {
        return divideRecyclerView;
    }

    private void bindView() {
        this.divideRecyclerView = rootView.findViewById(R.id.single_recycler_view);
    }

    private void initView() {
        this.divideLayoutManager = new LinearLayoutManager(getContext());
        this.divideRecyclerView.setLayoutManager(divideLayoutManager);
        this.bookSectionListAdapter = new BookSectionListAdapter(getContext());

        this.divideRecyclerView.setAdapter(bookSectionListAdapter);
        //this.bookSectionListAdapter.setRecycleViewItemOnClickListener(recycleViewItemOnClickListener);
    }

    private void requestData() {
        // 查询当前用户的所有划分
        StaticFactory.getExecutorService().execute(() -> {
            if (allWordList != null) {
                //updateUIHandler.post(() -> bookSectionListAdapter.replaceAll(allWordList));
                return;
            }
            // 读取文件列表
            File file = new File(AnyLanguageWordProperties.getExternalFilesDir(), WordContextPath.WORD_LIST.getPath());
            allWordList = new ArrayList<>();
            for (File singleWordList : file.listFiles()) {
                try {
                    allWordList.add(JsonUtils.readJson(singleWordList.getAbsolutePath().replace(AnyLanguageWordProperties.getExternalFilesDir().getAbsolutePath(), ""),
                            DivideDTOLocal.class));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            allWordList.sort((o1, o2) -> Math.toIntExact(o1.getOrder() - o2.getOrder()));
            //updateUIHandler.post(() -> bookSectionListAdapter.replaceAll(allWordList));
        });
    }

}