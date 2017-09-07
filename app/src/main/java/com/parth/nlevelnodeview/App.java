package com.parth.nlevelnodeview;

import android.app.Application;

import com.google.gson.Gson;

/**
 * Created by Parth Vora on 20/8/17.
 */
public class App extends Application {

    private static Gson gson;

    public static Gson getGson() {
        if(gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
