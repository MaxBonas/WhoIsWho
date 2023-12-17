 package com.max.whoiswho;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.FirebaseApp;

public class MyApp extends Application implements LifecycleObserver {

    private AudioManager audioManager;

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onBackground() {
        // La aplicación entra en segundo plano
        if (audioManager != null) {
            audioManager.pauseMusic();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onForeground() {
        // La aplicación vuelve al primer plano
        if (audioManager != null) {
            audioManager.playMusic();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        FirebaseApp.initializeApp(this);

        // Registra el observador del ciclo de vida
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }
}
