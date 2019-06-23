package com.e.mad1;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

public class AppExecutors {

    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainThread;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    AppExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(4),
                new MainThreadExecutor());
    }

    public Executor diskIO() {
        return diskIO;
    }
    public Executor networkIO() {
        return networkIO;
    }   // for connect to network, use this later

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
