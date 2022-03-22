package com.example.instagram.Activity;


import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutor {
    private static AppExecutor instance;
    private final Executor mainIO;
    private final Executor subIO;

    public AppExecutor(Executor mainIO, Executor subIO) {
        this.mainIO = mainIO;
        this.subIO = subIO;
    }

    public static AppExecutor getInstance() {
        if (instance == null) instance = new AppExecutor(new MainThreadHandler(),Executors.newSingleThreadExecutor());
        return instance;
    }

    public static class MainThreadHandler implements Executor {

        private Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    }

    public Executor getMainIO() {
        return mainIO;
    }

    public Executor getSubIO() {
        return subIO;
    }
}
