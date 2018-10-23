package com.yiyou.gamesdk.core.storage.db.global;

import com.yiyou.gamesdk.core.storage.ITable;

/**
 * Created by Nekomimi on 2017/6/13.
 */

public class AnnouncementTableV1 implements ITable {

    public static final String TABLE_NAME = "announcement";
    public static final int TABLE_VERSION = 1;

    public static final String COL_ID                      = "id";
    public static final String COL_TIME                    = "times";
    public static final String COL_LAST_DAY                = "last_day";
    public static final String COL_CHILDREN_UID            = "children_uid";


    public static final int INDEX_ID                      = 0;
    public static final int INDEX_TIME                    = 1;
    public static final int INDEX_LAST_DAY                = 2;
    public static final int INDEX_CHILDREN_UID            = 3;
    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    public int tableVersion() {
        return TABLE_VERSION;
    }

    @Override
    public String createTableSQL() {
        return "CREATE TABLE IF NOT EXISTS '"+tableName()+"' ( "
                + COL_ID             + " number, "
                + COL_TIME           + " number,"
                + COL_LAST_DAY       + " text, "
                + COL_CHILDREN_UID   + " text )";
    }

    @Override
    public String[] alterTableSQL(int oldVersion, int newVersion) {
        return new String[0];
    }
}
