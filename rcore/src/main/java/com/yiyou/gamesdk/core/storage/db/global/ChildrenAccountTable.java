package com.yiyou.gamesdk.core.storage.db.global;

import android.util.Log;

import com.yiyou.gamesdk.core.storage.ITable;

/**
 * Created by Nekomimi on 2017/4/24.
 */

public class ChildrenAccountTable implements ITable {

    public static final String TABLE_NAME = "children_account";
    public static final int TABLE_VERSION = 1;

    public static final String COL_CHILDREN_USER_ID             = "children_user_id";
    public static final String COL_CHILDREN_USER_NAME           = "children_user_name";
    public static final String COL_USERID                       = "user_id";
    public static final String COL_BUNDLE_ID                    = "bundle_id";
    public static final String COL_TT_ACCOUNT                   = "tt_account";
    public static final String COL_LAST_LOGIN_TIME              = "last_login_time";
    public static final String COL_GAME_ID                      = "game_id";

    //reverse cols
    public static final String COL_REVERSE_TEXT_0           = "reverse_text_0";
    public static final String COL_REVERSE_TEXT_1           = "reverse_text_1";
    public static final String COL_REVERSE_TEXT_2           = "reverse_text_2";
    public static final String COL_REVERSE_TEXT_3           = "reverse_text_3";
    public static final String COL_REVERSE_TEXT_4           = "reverse_text_4";
    public static final String COL_REVERSE_TEXT_5           = "reverse_text_5";
    public static final String COL_REVERSE_NUMBER_0         = "reverse_number_0";
    public static final String COL_REVERSE_NUMBER_1         = "reverse_number_1";
    public static final String COL_REVERSE_NUMBER_2         = "reverse_number_2";
    public static final String COL_REVERSE_NUMBER_3         = "reverse_number_3";
    public static final String COL_REVERSE_NUMBER_4         = "reverse_number_4";
    public static final String COL_REVERSE_NUMBER_5         = "reverse_number_5";
    public static final String COL_REVERSE_BLOB_0           = "reverse_blob_0";
    public static final String COL_REVERSE_BLOB_1           = "reverse_blob_1";
    public static final String COL_REVERSE_BLOB_2           = "reverse_blob_2";


    public static final int INDEX_CHILDREN_USER_ID                = 0;
    public static final int INDEX_CHILDREN_USER_NAME              = 1;
    public static final int INDEX_USERID                          = 2;
    public static final int INDEX_BUNDLE_ID                       = 3;
    public static final int INDEX_TT_ACCOUNT                      = 4;
    public static final int INDEX_LAST_LOGIN_TIME                 = 5;
    public static final int INDEX_GAME_ID                         = 6;

    //reverse cols
    public static final int INDEX_REVERSE_TEXT_0           = 7;
    public static final int INDEX_REVERSE_TEXT_1           = 8;
    public static final int INDEX_REVERSE_TEXT_2           = 9;
    public static final int INDEX_REVERSE_TEXT_3           = 10;
    public static final int INDEX_REVERSE_TEXT_4           = 11;
    public static final int INDEX_REVERSE_TEXT_5           = 12;
    public static final int INDEX_REVERSE_NUMBER_0         = 13;
    public static final int INDEX_REVERSE_NUMBER_1         = 14;
    public static final int INDEX_REVERSE_NUMBER_2         = 15;
    public static final int INDEX_REVERSE_NUMBER_3         = 16;
    public static final int INDEX_REVERSE_NUMBER_4         = 17;
    public static final int INDEX_REVERSE_NUMBER_5         = 18;
    public static final int INDEX_REVERSE_BLOB_0           = 19;
    public static final int INDEX_REVERSE_BLOB_1           = 20;
    public static final int INDEX_REVERSE_BLOB_2           = 21;

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
                + COL_CHILDREN_USER_ID + " number primary key, "
                + COL_CHILDREN_USER_NAME + " text, "
                + COL_USERID          + " number, "
                + COL_BUNDLE_ID + " text, "
                + COL_TT_ACCOUNT + " text, "
                + COL_LAST_LOGIN_TIME + " number, "
                + COL_GAME_ID + " text, "
                + COL_REVERSE_TEXT_0   + " text, "
                + COL_REVERSE_TEXT_1   + " text, "
                + COL_REVERSE_TEXT_2   + " text, "
                + COL_REVERSE_TEXT_3   + " text, "
                + COL_REVERSE_TEXT_4   + " text, "
                + COL_REVERSE_TEXT_5   + " text, "
                + COL_REVERSE_NUMBER_0 + " number, "
                + COL_REVERSE_NUMBER_1 + " number, "
                + COL_REVERSE_NUMBER_2 + " number, "
                + COL_REVERSE_NUMBER_3 + " number, "
                + COL_REVERSE_NUMBER_4 + " number, "
                + COL_REVERSE_NUMBER_5 + " number, "
                + COL_REVERSE_BLOB_0   + " blob, "
                + COL_REVERSE_BLOB_1   + " blob, "
                + COL_REVERSE_BLOB_2   + " blob ) ";
    }

    @Override
    public String[] alterTableSQL(int oldVersion, int newVersion) {
        Log.d("ChildrenAccountTable", "oldVersion: " + oldVersion + ";newVersion: " + newVersion );
        return new String[0];
    }
}
