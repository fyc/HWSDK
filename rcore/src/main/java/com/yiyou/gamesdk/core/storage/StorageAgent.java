package com.yiyou.gamesdk.core.storage;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.yiyou.gamesdk.core.consts.StatusCodeDef;
import com.yiyou.gamesdk.core.storage.db.global.GameSDKDatabase;
import com.yiyou.gamesdk.core.storage.db.global.TaoZiSdkDatabase;
import com.yiyou.gamesdk.core.storage.events.StorageEvent;
import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.IEventListener;
import com.mobilegamebar.rsdk.outer.util.StorageConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by levyyoung on 15/5/8.
 */
public class StorageAgent {

    public static final String TAG = "RSDK:StorageAgent ";

    private static final Object initLock = new Object();
    private static final Object sourceTag = new Object();

    private static DBAgent dbAgent;
    private static ConfigAgent configAgent;

    public static DBAgent dbAgent() {
        return dbAgent;
    }

    public static ConfigAgent configAgent() {
        return configAgent;
    }

    public static void init(Context context, int gameId) {
        synchronized (initLock) {
            if (dbAgent == null) {
                configAgent = new ConfigAgent();
                configAgent.init(gameId);
                dbAgent = new DBAgent();
                dbAgent.init(context);
            }
        }
    }

    public static void uninit() {
        synchronized (initLock) {
            if (dbAgent != null) {
                dbAgent.uninit();
                dbAgent = null;
            }
            if (configAgent != null) {
                configAgent.uninit();
                configAgent = null;
            }
        }
    }


    public static class DBAgent{

        private static final Class<?>[] DBConfig = new Class<?>[]{
                GameSDKDatabase.class, TaoZiSdkDatabase.class
        };

        private int preparedCount = 0;
        private Map<String, Database> dbMap = new ArrayMap<>();

        private DBAgent() {
        }

        public Database getPublicDatabase() {
            return dbMap.get(GameSDKDatabase.DB_NAME);
        }

        public Database getPrivateDatabase() {
            return dbMap.get(TaoZiSdkDatabase.DB_NAME);
        }

        public Database getDatabase(String dbName) {
            return dbMap.get(dbName);
        }

        protected void init(Context context) {
            initEvents();
            initDatabases(context);
        }

        protected void uninit() {
            for (Map.Entry<String, Database> entry : dbMap.entrySet()) {
                entry.getValue().close();
            }
            preparedCount = 0;
            EventDispatcherAgent.defaultAgent()
                    .removeEventListenersBySource(sourceTag);
        }

        private void initDatabases(Context context) {
            new GameSDKDatabase(context);
            new TaoZiSdkDatabase(context);
        }

        private void initEvents() {
            EventDispatcherAgent.defaultAgent()
                    .addEventListener(sourceTag, StorageEvent.TYPE_ON_DB_OPEN,onDBEvenListener);
            EventDispatcherAgent.defaultAgent()
                    .addEventListener(sourceTag, StorageEvent.TYPE_ON_DB_Upgrade,onDBEvenListener);
        }

        private IEventListener<StorageEvent.DBEventParam> onDBEvenListener
                = new IEventListener<StorageEvent.DBEventParam>() {
            @Override
            public void onEvent(String s, StorageEvent.DBEventParam param) {
                if (s.equals(StorageEvent.TYPE_ON_DB_OPEN)) {
                    dbMap.put(param.data.databaseName(), param.data);
                    preparedCount++;
                    checkIfAllDBPrepared();
                }
            }
        };

        private void checkIfAllDBPrepared() {
            if (preparedCount == DBConfig.length) {
                EventDispatcherAgent.defaultAgent()
                        .broadcast(StorageEvent.TYPE_ALL_DB_PREPARED, StatusCodeDef.SUCCESS);
            }
        }

    }

    public static class ConfigAgent{
        private static final Object configLock = new Object();
        static ExecutorService executorService = new ThreadPoolExecutor(0, 1,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        private String configRootPath;
        private Map<String, Properties> configCache = new ArrayMap<>();
        private ConfigAgent() {

        }

        public void init(int gameId) {
            configRootPath = StorageConfig.getGameDirPath(gameId) + "/";
        }

        public void uninit() {

        }

        public Properties loadConfig(String configName) {
            String configPath = getConfigPath(configName);
            Properties properties;
            properties = configCache.get(configName);
            if (properties == null) {
                properties = new Properties();
                try {
                    properties.load(new FileInputStream(new File(configPath)));
                    configCache.put(configName, properties);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return properties;
        }

        public void saveConfig(final Properties properties, final String configName, final String comment) {
            final String configPath = getConfigPath(configName);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileOutputStream outputStream = new FileOutputStream(new File(configPath));
                        properties.store(outputStream, comment);
                        synchronized (configLock) {
                            configCache.put(configName, properties);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private String getConfigPath(String configName) {
            return configRootPath + configName;
        }

    }

}
