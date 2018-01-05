package com.jaky.demo.statis;

import android.app.Application;

import com.onyx.android.sdk.data.DataManager;
import com.raizlabs.android.dbflow.config.DatabaseHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaky on 2018/1/5 0005.
 */

public class StatisticsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DataManager.init(this, databaseHolderList());
    }

    private List<Class<? extends DatabaseHolder>> databaseHolderList() {
        List<Class<? extends DatabaseHolder>> list = new ArrayList<>();
        return list;
    }
}
