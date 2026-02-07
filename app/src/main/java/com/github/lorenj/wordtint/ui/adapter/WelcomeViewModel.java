package com.github.lorenj.wordtint.ui.adapter;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.ui.activity.WelcomeActivity;

public class WelcomeViewModel extends ViewModel {

    private final MutableLiveData<WelcomeActivity.InitState> initState = new MutableLiveData<>();

    public LiveData<WelcomeActivity.InitState> getInitState() {
        return initState;
    }

    public void initDatabase(Context context) {
        /*
        当初始化数据库时进度条发生变动时会回调该方法
        回调的对象是APPDatabase中,回调的方法是这里的第二个方法
        */
        APPDatabase.initDatabase(context, progress -> initState.postValue(new WelcomeActivity.InitState(progress)));
    }


    public interface InitProgressCallback {
        void onProgress(int progress);
    }
}
