package com.github.lorenj.wordtint.ui.viewmodel;

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
        APPDatabase.initDatabase(context, progress -> initState.postValue(new WelcomeActivity.InitState(progress)));
    }

    public interface InitProgressCallback {
        void onProgress(int progress);
    }
}
