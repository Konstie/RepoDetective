package com.app.githubdetective;

import android.app.Application;

import com.app.githubdetective.injection.AppComponent;
import com.app.githubdetective.injection.AppModule;
import com.app.githubdetective.injection.DaggerAppComponent;

import timber.log.Timber;

public class RepoDetectiveApp extends Application {
    private static AppComponent sAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        sAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
}
