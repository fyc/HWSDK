package com.yiyou.gamesdk.core.storage.events;

import android.database.sqlite.SQLiteDatabase;

import com.mobilegamebar.rsdk.outer.event.BaseEventParam;

/**
 * Created by levyyoung on 15/5/12.
 */
public class InternalStorageEvent {

    public static final String TYPE_ON_CONN_CREATE = "com.yiyou.gamesdk.events.onConnCreate";
    public static final String TYPE_ON_CONN_OPEN = "com.yiyou.gamesdk.events.onConnOpen";
    public static final String TYPE_ON_CONN_UPGRADE = "com.yiyou.gamesdk.events.onConnUpgrade";

    public static final class Param extends BaseEventParam<SQLiteDatabase> {
        public boolean readOnly = false;

        public Param(int code, SQLiteDatabase data, boolean readOnly) {
            super(code, data);
            this.readOnly = readOnly;
        }
    }
}
