package com.yiyou.gamesdk.core.storage.events;

import com.yiyou.gamesdk.core.storage.Database;
import com.mobilegamebar.rsdk.outer.event.BaseEventParam;

/**
 * Created by levyyoung on 15/5/12.
 */
public class StorageEvent {
    public static final String TYPE_ON_DB_OPEN = "com.yiyou.gamesdk.event.onDBOpen";
    public static final String TYPE_ON_DB_Upgrade = "com.yiyou.gamesdk.event.onDBUpgrade";

    public static final String TYPE_ALL_DB_PREPARED = "com.yiyou.gamesdk.event.allDBPrepared";

    public static final class DBEventParam extends BaseEventParam<Database> {
        public DBEventParam() {
        }

        public DBEventParam(int code, Database data) {
            super(code, data);
        }
    }

}
