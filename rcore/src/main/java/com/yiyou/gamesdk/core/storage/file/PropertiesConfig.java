package com.yiyou.gamesdk.core.storage.file;

import android.support.annotation.NonNull;
import com.mobilegamebar.rsdk.outer.util.StorageConfig;

import java.io.File;
import java.io.IOException;

/**
 * Created by chenshuide on 15/7/21.
 */
public class PropertiesConfig {


    public static final String GAME_CONFIG_NAME = "channel.properties";


    /**
     *
     * @return PropertiesFile
     */
    public static File getDefaultPropertiesFile() {

        return getPropertiesFileByName(StorageConfig.getSDKPublicChannelPath(), GAME_CONFIG_NAME);

    }


    public static File getPropertiesFileByName(@NonNull String path, @NonNull String filename) {
        File propertiesFile = null;
        try {

            propertiesFile = new File(path, filename);

            if (!propertiesFile.exists())
                propertiesFile.createNewFile();


        } catch (IOException e) {

        }

        return propertiesFile;

    }


    public interface Constant {
        String KEY_CHANNEL = "channel";
    }

}
