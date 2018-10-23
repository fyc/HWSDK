package com.yiyou.gamesdk.core.storage.db.global;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.yiyou.gamesdk.core.storage.Database;
import com.yiyou.gamesdk.core.storage.ITable;
import com.mobilegamebar.rsdk.outer.util.StorageConfig;

import java.util.Map;

/**
 * Created by levyyoung on 15/5/14.
 */
public class GameSDKDatabase extends Database {

    public static final String DB_NAME = "hw_r_game_sdk.db";
    public static final int DB_VERSION = 1;

    public GameSDKDatabase(Context context) {
        super(context);
    }

    //升级数据库版本时，新增的表
    @Override
    public Map<Integer, ITable[]> tablesToAddedOnUpgrade() {
        Map<Integer, ITable[]> entry = new ArrayMap<>();
        entry.put(DB_VERSION,addTables());
        return entry;
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
        return new ITable[]{new AccountTable(),new ChildrenAccountTable()};
    }

    //不升级数据库版本，新增表
    @Override
    public ITable[] addTables() {
        return new ITable[]{new ChildrenAccountTable()};
    }

    @Override
    public int databaseVersion() {
        return DB_VERSION;
    }
}
