package com.example.lab3;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistoryStorage {

    private static final String PREFERENCES_NAME = "calculator";
    private static final String KEY_HISTORY = "history";

    private final SharedPreferences preferences;

    public HistoryStorage(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public List<String> load() {
        String saved = preferences.getString(KEY_HISTORY, "");
        List<String> items = new ArrayList<>();
        if (!saved.isEmpty()) {
            items.addAll(Arrays.asList(saved.split("\n")));
        }
        return items;
    }

    public void save(List<String> items) {
        preferences.edit().putString(KEY_HISTORY, TextUtils.join("\n", items)).apply();
    }
}
