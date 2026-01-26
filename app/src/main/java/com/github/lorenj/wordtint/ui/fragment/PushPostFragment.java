package com.github.lorenj.wordtint.ui.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.lorenj.wordtint.entity.dto.PublishPostParam;
import com.github.lorenj.wordtint.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class PushPostFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    private Handler updateUIHandler;
    // 文件选择按钮
    private TextView chooseFileTextView;
    // 选择的文件名文本框
    private TextView chooseFileNameTextView;
    // 封面选择按钮
    private TextView chooseCoverTextView;
    // 封面名文本框
    private TextView chooseCoverNameTextView;
    // 标题input
    private TextView titleInput;
    // 发布按钮
    private Button pushPost;
    // 发布帖子的参数
    private PublishPostParam publishPostParam = new PublishPostParam();

    // 选择文件Code
    private static final int CHOOSE_FILE_CODE = 1;
    private static final int CHOOSE_COVER_CODE = 2;

    public PushPostFragment() {
    }

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
        rootView = inflater.inflate(R.layout.fragment_push_post, container, false);
        updateUIHandler = new Handler();
        bindView();
        initView();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.fragment_push_post_choose_file) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, CHOOSE_FILE_CODE);
        } else if (itemId == R.id.fragment_push_post_choose_cover) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, CHOOSE_COVER_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CHOOSE_FILE_CODE) {
            Uri uri = data.getData();
            File file = null;
            chooseFileNameTextView.setText(new File(uri.getPath()).getName());

            ContentResolver contentResolver = getContext().getContentResolver();

            String displayName = System.currentTimeMillis() + Math.round((Math.random() + 1) * 1000)

                    + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));

            try {
                InputStream is = contentResolver.openInputStream(uri);
                File cache = new File(getContext().getCacheDir().getAbsolutePath(), displayName);
                FileOutputStream fos = new FileOutputStream(cache);
                FileUtils.copy(is, fos);
                file = cache;
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            publishPostParam.setMarkDownFile(file);
        } else if (requestCode == CHOOSE_COVER_CODE) {
            Uri uri = data.getData();
            File file = null;

            ContentResolver contentResolver = getContext().getContentResolver();

            String displayName = System.currentTimeMillis() + Math.round((Math.random() + 1) * 1000)

                    + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));

            try {
                InputStream is = contentResolver.openInputStream(uri);
                File cache = new File(getContext().getCacheDir().getAbsolutePath(), displayName);
                FileOutputStream fos = new FileOutputStream(cache);
                FileUtils.copy(is, fos);
                file = cache;
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            publishPostParam.setCoverFile(file);
            chooseCoverNameTextView.setText(new File(uri.getPath()).getName());
        }
    }

    private void bindView() {
        this.chooseFileTextView = rootView.findViewById(R.id.fragment_push_post_choose_file);
        this.chooseFileNameTextView = rootView.findViewById(R.id.fragment_push_post_file_name);
        this.chooseCoverTextView = rootView.findViewById(R.id.fragment_push_post_choose_cover);
        this.chooseCoverNameTextView = rootView.findViewById(R.id.fragment_push_post_cover_name);
        this.titleInput = rootView.findViewById(R.id.fragment_push_post_title_input);
        this.pushPost = rootView.findViewById(R.id.fragment_push_post_push_post);

        this.chooseFileTextView.setOnClickListener(this);
        this.chooseCoverTextView.setOnClickListener(this);
        this.pushPost.setOnClickListener(this);
    }

    private void initView() {

    }

    // 通过Uri获取真实路径
    public File getFileFromUri(Uri uri) {
        String filePath = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContext().getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        return new File(filePath);
    }


}