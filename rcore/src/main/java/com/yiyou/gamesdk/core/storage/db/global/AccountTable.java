package com.yiyou.gamesdk.core.storage.db.global;

import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.core.storage.ITable;

/**
 * Created by levyyoung on 15/5/15.
 */
public class AccountTable implements ITable{

    public static final String TABLE_NAME = "account";
    public static final int TABLE_VERSION = 1;

    public static final String COL_USERID                     = "user_id";
    public static final String COL_TT_ACCOUNT                 = "tt_account";
    public static final String COL_USERNAME                   = "username";
    public static final String COL_PHONE                      = "phone";
    public static final String COL_PWD                        = "pwd";
//    public static final String COL_ACCESS_TOKEN               = "access_token";
//    public static final String COL_REFRESH_TOKEN              = "refresh_token";
//    public static final String COL_EXPIRES_IN                 = "expires_in";
    public static final String COL_AVATAR_URL                 = "avatar_url";
//    public static final String COL_CHANNEL                    = "channel";
    public static final String COL_LAST_LOGIN_TIME            = "last_login_time";
//    public static final String COL_LAST_GAME_ID               = "last_login_game_id";
    public static final String COL_HAS_PAYPASSWORD            = "has_pay_password";

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

    public static final int INDEX_USERID                       = 0;
    public static final int INDEX_TT_ACCOUNT                   = 1;
    public static final int INDEX_USERNAME                     = 2;
    public static final int INDEX_PHONE                        = 3;
    public static final int INDEX_PWD                          = 4;
//    public static final int INDEX_ACCESS_TOKEN                 = 5;
//    public static final int INDEX_REFRESH_TOKEN                = 6;
//    public static final int INDEX_EXPIRES_IN                   = 7;
    public static final int INDEX_AVATAR_URL                   = 5;
//    public static final int INDEX_CHANNEL                      = 9;
    public static final int INDEX_LAST_LOGIN_TIME              = 6;
//    public static final int INDEX_LAST_GAME_ID                 = 11;
    public static final int INDEX_HAS_PAYPASSWORD              = 7;


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
                + COL_USERID + " number primary key, "
                + COL_TT_ACCOUNT + " text, "
                + COL_USERNAME + " text, "
                + COL_PHONE + " text, "
                + COL_PWD + " text, "
//                    + COL_ACCESS_TOKEN     + " text, "
//                + COL_REFRESH_TOKEN     + " text, "
//                + COL_EXPIRES_IN     + " number, "
                + COL_AVATAR_URL + " text, "
//                + COL_CHANNEL     + " text, "
                + COL_LAST_LOGIN_TIME + " number, "
//                + COL_LAST_GAME_ID        + " number, "
                + COL_HAS_PAYPASSWORD + " number, "
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
//        if (oldVersion == 1 && newVersion == 2){
//            Log.d("test", "alterTableSQL: ");
//            return new String[]{
//                    "ALTER TABLE " + TABLE_NAME + " RENAME TO " + TABLE_NAME + "_temp",
//                    createTableSQL(),
//                    new StringBuilder().append("insert into ").append(TABLE_NAME).append("(")
//                            .append(COL_USERID).append(", ")
//                            .append(COL_TT_ACCOUNT).append(", ")
//                            .append(COL_USERNAME).append(", ")
//                            .append(COL_PHONE).append(", ")
//                            .append(COL_PWD).append(", ")
//                            .append(COL_ACCESS_TOKEN).append(", ")
//                            .append(COL_REFRESH_TOKEN).append(", ")
//                            .append(COL_EXPIRES_IN).append(", ")
//                            .append(COL_AVATAR_URL).append(", ")
//                            .append(COL_CHANNEL).append(", ")
//                            .append(COL_LAST_LOGIN_TIME).append(", ")
//                            .append(COL_LAST_GAME_ID).append(", ")
//                            .append(COL_HAS_PAYPASSWORD).append(", ")
//                            .append(COL_REVERSE_TEXT_0).append(", ")
//                            .append(COL_REVERSE_TEXT_1).append(", ")
//                            .append(COL_REVERSE_TEXT_2).append(", ")
//                            .append(COL_REVERSE_TEXT_3).append(", ")
//                            .append(COL_REVERSE_TEXT_4).append(", ")
//                            .append(COL_REVERSE_TEXT_5).append(", ")
//                            .append(COL_REVERSE_NUMBER_0).append(", ")
//                            .append(COL_REVERSE_NUMBER_1).append(", ")
//                            .append(COL_REVERSE_NUMBER_2).append(", ")
//                            .append(COL_REVERSE_NUMBER_3).append(", ")
//                            .append(COL_REVERSE_NUMBER_4).append(", ")
//                            .append(COL_REVERSE_NUMBER_5).append(", ")
//                            .append(COL_REVERSE_BLOB_0).append(", ")
//                            .append(COL_REVERSE_BLOB_1).append(", ")
//                            .append(COL_REVERSE_BLOB_2).append(") ")
//                            .append("select ")
//                            .append(COL_USERID).append(", ")
//                            .append(COL_TT_ACCOUNT).append(", ")
//                            .append(COL_USERNAME).append(", ")
//                            .append(COL_PHONE).append(", ")
//                            .append(COL_PWD).append(", ")
//                            .append(COL_ACCESS_TOKEN).append(", ")
//                            .append(COL_REFRESH_TOKEN).append(", ")
//                            .append(COL_EXPIRES_IN).append(", ")
//                            .append(COL_AVATAR_URL).append(", ")
//                            .append(COL_CHANNEL).append(", ")
//                            .append(COL_LAST_LOGIN_TIME).append(", ")
//                            .append(COL_LAST_GAME_ID).append(", ")
//                            .append(" \"0\" ").append(", ")
//                            .append(COL_REVERSE_TEXT_0).append(", ")
//                            .append(COL_REVERSE_TEXT_1).append(", ")
//                            .append(COL_REVERSE_TEXT_2).append(", ")
//                            .append(COL_REVERSE_TEXT_3).append(", ")
//                            .append(COL_REVERSE_TEXT_4).append(", ")
//                            .append(COL_REVERSE_TEXT_5).append(", ")
//                            .append(COL_REVERSE_NUMBER_0).append(", ")
//                            .append(COL_REVERSE_NUMBER_1).append(", ")
//                            .append(COL_REVERSE_NUMBER_2).append(", ")
//                            .append(COL_REVERSE_NUMBER_3).append(", ")
//                            .append(COL_REVERSE_NUMBER_4).append(", ")
//                            .append(COL_REVERSE_NUMBER_5).append(", ")
//                            .append(COL_REVERSE_BLOB_0).append(", ")
//                            .append(COL_REVERSE_BLOB_1).append(", ")
//                            .append(COL_REVERSE_BLOB_2).append(" ")
//                            .append("from ").append(TABLE_NAME).append("_temp").toString(),
//                    "DROP TABLE " + TABLE_NAME + "_temp"
//            };
//        }
        Log.d("AccountTable", "oldVersion: " + oldVersion + ";newVersion: " + newVersion );

        return new String[0];
    }
}
