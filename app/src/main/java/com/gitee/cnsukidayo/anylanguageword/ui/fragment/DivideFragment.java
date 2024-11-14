package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.AnyLanguageWordProperties;
import com.gitee.cnsukidayo.anylanguageword.context.pathsystem.document.WordContextPath;
import com.gitee.cnsukidayo.anylanguageword.context.support.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.entity.local.DivideDTOLocal;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.divide.ChildDivideListAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.gitee.cnsukidayo.anylanguageword.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.cnsukidayo.wword.model.dto.LanguageClassDTO;
import io.github.cnsukidayo.wword.model.dto.UserProfileDTO;

/**
 * 显示单词划分的Fragment
 */
public class DivideFragment extends Fragment {

    private View rootView;
    private RecyclerView divideRecyclerView;
    private LinearLayoutManager divideLayoutManager;
    private ChildDivideListAdapter childDivideListAdapter;
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


    private void bindView() {
        this.divideRecyclerView = rootView.findViewById(R.id.single_recycler_view);
    }

    private void initView() {
        this.divideLayoutManager = new LinearLayoutManager(getContext());
        this.divideRecyclerView.setLayoutManager(divideLayoutManager);
        this.childDivideListAdapter = new ChildDivideListAdapter(getContext());

        this.divideRecyclerView.setAdapter(childDivideListAdapter);
        this.childDivideListAdapter.setRecycleViewItemOnClickListener(recycleViewItemOnClickListener);
    }

    private void requestData() {
        // 查询当前用户的所有划分
        StaticFactory.getExecutorService().execute(() -> {
            // 读取文件列表
            File file = new File(AnyLanguageWordProperties.getExternalFilesDir(), WordContextPath.WORD_LIST.getPath());
            List<DivideDTOLocal> allWordList = new ArrayList<>();
            for (File singleWordList : file.listFiles()) {
                try {
                    allWordList.add(JsonUtils.readJson(singleWordList.getAbsolutePath().replace(AnyLanguageWordProperties.getExternalFilesDir().getAbsolutePath(), ""),
                            DivideDTOLocal.class));
                    allWordList.sort((o1, o2) -> Math.toIntExact(o1.getOrder() - o2.getOrder()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            updateUIHandler.post(() -> childDivideListAdapter.replaceAll(allWordList));
        });
    }

}