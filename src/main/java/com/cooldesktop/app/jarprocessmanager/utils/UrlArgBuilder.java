package com.cooldesktop.app.jarprocessmanager.utils;

import java.util.HashMap;
import java.util.Map;

public class UrlArgBuilder {
    private Map<String, String> urlMap = new HashMap<>();

    public UrlArgBuilder set(String key, String value) {
        urlMap.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        urlMap.forEach((key, value) -> {
            result.append(key + "=" + value);
            result.append("&");
        });
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }
}
