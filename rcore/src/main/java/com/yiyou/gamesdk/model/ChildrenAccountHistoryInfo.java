package com.yiyou.gamesdk.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.yiyou.gamesdk.core.storage.db.global.ChildrenAccountTable;

/**
 * Created by Nekomimi on 2017/4/24.
 */

public class ChildrenAccountHistoryInfo {

    public long userID              = 0;
    public String TTAccount           = "";
    public String childrenUsername          = "";
    public long     lastLoginTime = 0;
    public long childrenUserID     = 0;
    public String gameId           = "";
    public String bundleID         = "";


    public static ContentValues transformToCV(ChildrenAccountHistoryInfo info) {
        ContentValues cv = new ContentValues();
        cv.put(ChildrenAccountTable.COL_USERID, info.userID);
        cv.put(ChildrenAccountTable.COL_TT_ACCOUNT, info.TTAccount);
        cv.put(ChildrenAccountTable.COL_CHILDREN_USER_NAME, info.childrenUsername);
        cv.put(ChildrenAccountTable.COL_LAST_LOGIN_TIME, info.lastLoginTime);
        cv.put(ChildrenAccountTable.COL_GAME_ID, info.gameId);
        cv.put(ChildrenAccountTable.COL_CHILDREN_USER_ID, info.childrenUserID);
        cv.put(ChildrenAccountTable.COL_BUNDLE_ID, info.bundleID);
        return cv;
    }

    public static ChildrenAccountHistoryInfo fromCV(ContentValues contentValues) {
        ChildrenAccountHistoryInfo info = new ChildrenAccountHistoryInfo();


        return info;
    }

    public static ChildrenAccountHistoryInfo transformFromCursor(Cursor cursor) {
        ChildrenAccountHistoryInfo info = new ChildrenAccountHistoryInfo();
        info.userID = cursor.getLong(ChildrenAccountTable.INDEX_USERID);
        info.TTAccount = cursor.getString(ChildrenAccountTable.INDEX_TT_ACCOUNT);
        info.lastLoginTime = cursor.getLong(ChildrenAccountTable.INDEX_LAST_LOGIN_TIME);
        info.childrenUserID = cursor.getLong(ChildrenAccountTable.INDEX_CHILDREN_USER_ID);
        info.childrenUsername = cursor.getString(ChildrenAccountTable.INDEX_CHILDREN_USER_NAME);
        info.gameId = cursor.getString(ChildrenAccountTable.INDEX_GAME_ID);
        info.bundleID = cursor.getString(ChildrenAccountTable.INDEX_BUNDLE_ID);
        return info;
    }
}
