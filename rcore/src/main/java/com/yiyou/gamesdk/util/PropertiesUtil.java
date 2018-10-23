package com.yiyou.gamesdk.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * Created by chenshuide on 16-2-17.
 */
public class PropertiesUtil {
    private static final String TAG = "PropertiesUtil";

    public static void writeProperties2File(Properties properties, File dest) {

        try {
            if (properties == null || properties.isEmpty() || dest == null)
                return;

            if (!dest.exists()) {
                dest.createNewFile();
            }


            PrintWriter pw = new PrintWriter(dest);

            properties.list(pw);

            properties.store(pw, "");


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static String getProperties(File dest, String key) {

        Properties properties = getProperties4File(dest);
        if (properties == null || properties.isEmpty())
            return "";
        return properties.getProperty(key);
    }


    public static String setProperties(File dest, String key, String value) {

        Properties properties = getProperties4File(dest);
        if (properties == null) {
            properties = new Properties();
        }

        String oldValue = (String) properties.setProperty(key, value);

        writeProperties2File(properties, dest);

        return oldValue;

    }


    public static Properties getProperties4File(File dest) {

        if (dest == null || !dest.exists())
            return null;

        try {
            Properties properties = new Properties();
            properties.load(new FileReader(dest));
            return properties;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static Properties getProperties4File(InputStream ios) {

        if (ios == null)
            return null;

        try {
            Properties properties = new Properties();
            properties.load(ios);
            return properties;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
            *
            * read changeAccount_switch from  assets config
    *
            * @param context
    * @return changeAccount_switch
    */
    public static String parseChangeAccount4Config(@NonNull Context context) {
        AssetManager assetManager = context.getApplicationContext().getAssets();
        String changeAccountSwitch = "True";

        try {
            InputStream in = assetManager.open("taozi_game_sdk_opt.properties");
            Log.d(TAG, "found opt properties. try parse changeAccount_switch from this config.");
            Properties p = new Properties();
            p.load(in);
            changeAccountSwitch = p.getProperty("changeAccount_switch");
            Log.d(TAG, "changeAccount_switch value is :" + changeAccountSwitch);
            if (StringUtils.isEmpty(changeAccountSwitch)) {
                changeAccountSwitch = "True";
                Log.d(TAG, "changeAccount_switch not found.The default value is True.");
            }
            p.clear();
            in.close();
        } catch (IOException e) {
            Log.d(TAG, "opt properties not found.The default value is True.");
        }

        return changeAccountSwitch;
    }

}
