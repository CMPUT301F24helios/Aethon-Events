package com.example.aethoneventsapp;

import java.util.HashMap;
import java.util.Map;

public class GlobalDataStore {
    private static GlobalDataStore instance;
    private final Map<String, Object> dataMap;

    private GlobalDataStore() {
        dataMap = new HashMap<>();
    }

    // Get the single instance of GlobalDataStore
    public static synchronized GlobalDataStore getInstance() {
        if (instance == null) {
            instance = new GlobalDataStore();
        }
        return instance;
    }

    // Set data in the map
    public void setData(String key, Object value) {
        dataMap.put(key, value);
    }

    // Get data from the map
    public Object getData(String key) {
        return dataMap.get(key);
    }
}
