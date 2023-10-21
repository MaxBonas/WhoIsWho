package com.max.whoiswho;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

public class MyApp extends Application implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onBackground() {
        // La aplicación entra en segundo plano
        TitleActivity.pauseMusic();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onForeground() {
        // La aplicación vuelve al primer plano
        TitleActivity.playMusic();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Registra el observador del ciclo de vida
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }
}