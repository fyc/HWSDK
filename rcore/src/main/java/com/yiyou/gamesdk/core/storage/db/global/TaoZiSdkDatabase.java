package com.yiyou.gamesdk.core.storage.db.global;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.mobilegamebar.rsdk.outer.util.StorageConfig;
import com.yiyou.gamesdk.core.storage.Database;
import com.yiyou.gamesdk.core.storage.ITable;

import java.util.Map;

/**
 * Created by Nekomimi on 2017/6/13.
 */

public class TaoZiSdkDatabase extends Database {

    public static final String DB_NAME = "taozi_sdk.db";
    public static final int DB_VERSION = 1;

    public TaoZiSdkDatabase(Context context) {
        super(context);
    }


    @Override
    public String storagePath() {
        return StorageConfig.getPublicDBDirPath();
    }

    @Override
    public String databaseName() {
        return DB_NAME;
    }

    @Override
    public ITable[] staticTables() {
        return new ITable[]{new AnnouncementTable(), new AnnouncementTableV1()};
    }

    @Override
    public int databaseVersion() {
        return DB_VERSION;
    }

    @Override
    public Map<Integer, ITable[]> tablesToAddedOnUpgrade() {
        Map<Integer, ITable[]> entry = new ArrayMap<>();
        entry.put(DB_VERSION,addTables());
        return entry;
    }

    @Override
    public ITable[] addTables() {
        return new ITable[]{new AnnouncementTable()};
    }
}
