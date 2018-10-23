package com.yiyou.gamesdk.core.storage.db.global;

import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.core.storage.ITable;

/**
 * Created by Nekomimi on 2017/6/13.
 */

public class AnnouncementTable implements ITable {

    public static final String TABLE_NAME = "announcement_2";
    public static final int TABLE_VERSION = 1;

    public static final String COL_ID                      = "id";
    public static final String COL_TIME                    = "times";
    public static final String COL_LAST_DAY                = "last_day";
    public static final String COL_CHILDREN_UID            = "children_uid";
    public static final String COL_FIRST_DAY               = "first_day";
    public static final String COL_TITLE                   = "title";
    public static final String COL_URL                     = "url";
    public static final String COL_TYPE                    = "type";

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


    public static final int INDEX_ID                      = 0;
    public static final int INDEX_TIME                    = 1;
    public static final int INDEX_LAST_DAY                = 2;
    public static final int INDEX_CHILDREN_UID            = 3;
    public static final int INDEX_FIRST_DAY               = 4;
    public static final int INDEX_TITLE                   = 5;
    public static final int INDEX_URL                     = 6;
    public static final int INDEX_TYPE                    = 7;

    //reverse cols
    public static final int INDEX_REVERSE_TEXT_0           = 8;
    public static final int INDEX_REVERSE_TEXT_1           = 9;
    public static final int INDEX_REVERSE_TEXT_2           = 10;
    public static final int INDEX_REVERSE_TEXT_3           = 11;
    public static final int INDEX_REVERSE_TEXT_4           = 12;
    public static final int INDEX_REVERSE_TEXT_5           = 13;
    public static final int INDEX_REVERSE_NUMBER_0         = 14;
    public static final int INDEX_REVERSE_NUMBER_1         = 15;
    public static final int INDEX_REVERSE_NUMBER_2         = 16;
    public static final int INDEX_REVERSE_NUMBER_3         = 17;
    public static final int INDEX_REVERSE_NUMBER_4         = 18;
    public static final int INDEX_REVERSE_NUMBER_5         = 19;
    public static final int INDEX_REVERSE_BLOB_0           = 20;
    public static final int INDEX_REVERSE_BLOB_1           = 21;
    public static final int INDEX_REVERSE_BLOB_2           = 22;

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
                + COL_CHILDREN_UID   + " text, "
                + COL_FIRST_DAY      + " text, "
                + COL_TITLE          + " text, "
                + COL_URL            + " text, "
                + COL_TYPE           + " number, "
                + COL_REVERSE_TEXT_0 + " text, "
                + COL_REVERSE_TEXT_1 + " text, "
                + COL_REVERSE_TEXT_2 + " text, "
                + COL_REVERSE_TEXT_3 + " text, "
                + COL_REVERSE_TEXT_4 + " text, "
                + COL_REVERSE_TEXT_5 + " text, "
                + COL_REVERSE_NUMBER_0 + " number, "
                + COL_REVERSE_NUMBER_1 + " number, "
                + COL_REVERSE_NUMBER_2 + " number, "
                + COL_REVERSE_NUMBER_3 + " number, "
                + COL_REVERSE_NUMBER_4 + " number, "
                + COL_REVERSE_NUMBER_5 + " number, "
                + COL_REVERSE_BLOB_0 + " blob, "
                + COL_REVERSE_BLOB_1 + " blob, "
                + COL_REVERSE_BLOB_2 + " blob ) ";
    }

    @Override
    public String[] alterTableSQL(int oldVersion, int newVersion) {
        Log.d("AnnouncementTable", "oldVersion: " + oldVersion + ";newVersion: " + newVersion );
//        if (oldVersion == 1 && newVersion == TABLE_VERSION){
//            return new String[]{
//                    "ALTER TABLE " + TABLE_NAME + " RENAME TO " + TABLE_NAME + "_temp",
//                    createTableSQL(),
//                    new StringBuilder().append("insert into ").append(TABLE_NAME).append("(")
//                            .append(COL_ID).append(", ")
//                            .append(COL_TIME).append(", ")
//                            .append(COL_LAST_DAY).append(", ")
//                            .append(COL_CHILDREN_UID).append(", ")
//                            .append(COL_FIRST_DAY).append(", ")
//                            .append(COL_TITLE).append(", ")
//                            .append(COL_URL).append(") ")
//                            .append("select ")
//                            .append(COL_ID).append(", ")
//                            .append(COL_TIME).append(", ")
//                            .append(COL_LAST_DAY).append(", ")
//                            .append(COL_CHILDREN_UID).append(", ")
//                            .append(" \"\" ").append(", ")
//                            .append(" \"\" ").append(", ")
//                            .append(" \"\" ").append(" ")
//                            .append("from ").append(TABLE_NAME).append("_temp").toString(),
//                    "DROP TABLE " + TABLE_NAME + "_temp"
//            };
//        }
        return new String[0];
    }
}
