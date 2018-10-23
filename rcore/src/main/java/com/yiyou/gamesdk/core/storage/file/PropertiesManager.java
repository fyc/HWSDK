package com.yiyou.gamesdk.core.storage.file;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.mobilegamebar.rsdk.outer.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by chenshuide on 15/7/21.
 */
public class PropertiesManager {

    private static final String TAG = "RSDK:PropertiesManager";

    private static PropertiesManager instance;

    private Map<String, Properties> cache = new ArrayMap<>();

    private PropertiesManager() {
    }

    public static PropertiesManager getInstance() {
        if (instance == null)
            instance = new PropertiesManager();

        return instance;
    }

    /**
     * Searches for the property with the specified name.
     *
     * @param key name
     * @return property value if no mapping value return  ""
     */
    public String getValueFromDefaultConfig(@NonNull String key) {
        return getValue(PropertiesConfig.getDefaultPropertiesFile(), key);

    }

    /**
     * Searches for the property with the specified name.
     *
     * @param saveFile file
     * @param key      name
     * @return property value if no mapping value return  ""
     */
    public String getValue(@NonNull File saveFile, @NonNull String key) {

        Properties properties = cache.get(saveFile.getAbsolutePath());

        if (properties != null) {
            Log.d(TAG, "hit in " + key);
            return properties.getProperty(key, "");
        }

        properties = new Properties();


        try {
            properties.load(new FileInputStream(saveFile));

            cache.put(saveFile.getAbsolutePath(), properties);

            return properties.getProperty(key, "");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";


    }

    /**
     * If the key already exists,
     * the old value is replaced. The key and value cannot be {@code null}.
     *
     *@param saveFile file
     * @param key   key
     * @param value value
     */
    public void setValue(@NonNull File saveFile, @NonNull String key, @NonNull String value) {

        try {

            if (value.equals(getValue(saveFile, key))) {
                Log.d(TAG, "key-value same " + key);
                return;
            }

            Properties properties = cache.get(saveFile.getAbsolutePath());

            properties.setProperty(key, value);//写到内存

            OutputStream os = new FileOutputStream(saveFile);
            properties.store(os, "");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * If the key already exists,
     * the old value is replaced. The key and value cannot be {@code null}.
     *
     * @param key   key
     * @param value value
     */
    public void setValueToDefaultConfig(@NonNull String key, @NonNull String value) {
        setValue(PropertiesConfig.getDefaultPropertiesFile(), key, value);

    }

    /**
     * 更新键值到内存， 不做文件IO
     */
    public void updateValueToDefaultConfig(@NonNull String key, @NonNull String value) {
        String defaultConfigPath = PropertiesConfig.getDefaultPropertiesFile().getAbsolutePath();
        Properties properties = cache.get(defaultConfigPath);
        if (properties == null) {
            properties = new Properties();
            cache.put(defaultConfigPath, properties);
        }
        properties.setProperty(key, value);//写到内存
    }

    public void flushValueToDefaultConfig() {
        File defaultConfig = PropertiesConfig.getDefaultPropertiesFile();
        Properties properties = cache.get(defaultConfig.getAbsolutePath());
        try {
            properties.store(new FileOutputStream(defaultConfig), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getSum(@NonNull File saveFile){
        int sum=0;
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(saveFile));
            sum = properties.keySet().size();
            return sum;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sum;
    }

    public void removeProperties(@NonNull File saveFile){
        try {
            Properties properties = cache.get(saveFile.getAbsolutePath());
            properties.clear();
            cache.remove(saveFile.getAbsolutePath());
            OutputStream os = new FileOutputStream(saveFile);
            properties.store(os, "");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<Object> getKeys(@NonNull File saveFile){
        Properties properties = cache.get(saveFile.getAbsolutePath());

        if (properties != null) {
            return properties.keySet();
        }
        properties = new Properties();
        try {
            properties.load(new FileInputStream(saveFile));

            cache.put(saveFile.getAbsolutePath(), properties);

            return properties.keySet();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
