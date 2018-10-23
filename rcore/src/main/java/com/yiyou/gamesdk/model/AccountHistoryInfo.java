package com.yiyou.gamesdk.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.yiyou.gamesdk.core.storage.db.global.AccountTable;

import static android.R.attr.accountType;

/**
 * Created by levyyoung on 15/6/24.
 */
public class AccountHistoryInfo {

    public static final int ACCOUNT_TYPE_USER = 0; //普通用户

    public static final int ACCOUNT_STATUS_ACTIVATING = 0; //激活

    public long userID              = 0;
    public String TTAccount           = "";
    public String username          = "";
    public String phone             = "";
    public String password          = "";
//    public String accessToken       = "";
//    public String refreshToken      = "";
    public String avatarUrl         = "";
    public boolean hasPayPassword   = false;
//    public int expiresIn            = 0;
//    public String channel           = "";
    public long     lastLoginTime = 0;
//    public int      lastLoginGameId = 0;


    @Override
    public String toString() {
        return "AccountHistoryInfo{" +
                "account='" + TTAccount + '\'' +
                ", userID=" + userID +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", accountType=" + accountType +
                // // TODO: 2017/4/24 add more
                '}';
    }

    public static ContentValues transformToCV(AccountHistoryInfo info) {
        ContentValues cv = new ContentValues();
        cv.put(AccountTable.COL_USERID, info.userID);
        cv.put(AccountTable.COL_TT_ACCOUNT, info.TTAccount);
        cv.put(AccountTable.COL_USERNAME, info.username);
        cv.put(AccountTable.COL_PHONE, info.phone);
        cv.put(AccountTable.COL_PWD, info.password);
        cv.put(AccountTable.COL_AVATAR_URL, info.avatarUrl);
        cv.put(AccountTable.COL_LAST_LOGIN_TIME, info.lastLoginTime);
        cv.put(AccountTable.COL_HAS_PAYPASSWORD, info.hasPayPassword);
        return cv;
    }

    public static AccountHistoryInfo fromCV(ContentValues contentValues) {
        AccountHistoryInfo info = new AccountHistoryInfo();
        info.userID = contentValues.getAsLong(AccountTable.COL_USERID);
        if (contentValues.containsKey(AccountTable.COL_TT_ACCOUNT)) {
            info.TTAccount = contentValues.getAsString(AccountTable.COL_TT_ACCOUNT);
        }
        if (contentValues.containsKey(AccountTable.COL_USERNAME)) {
            info.username = contentValues.getAsString(AccountTable.COL_USERNAME);
        }
        if (contentValues.containsKey(AccountTable.COL_PHONE)) {
            info.phone = contentValues.getAsString(AccountTable.COL_PHONE);
        }
        if (contentValues.containsKey(AccountTable.COL_PWD)) {
            info.password = contentValues.getAsString(AccountTable.COL_PWD);
        }
        if (contentValues.containsKey(AccountTable.COL_AVATAR_URL)) {
            info.avatarUrl = contentValues.getAsString(AccountTable.COL_AVATAR_URL);
        }
        if (contentValues.containsKey(AccountTable.COL_LAST_LOGIN_TIME)) {
            info.lastLoginTime = contentValues.getAsLong(AccountTable.COL_LAST_LOGIN_TIME);
        }
        if (contentValues.containsKey(AccountTable.COL_HAS_PAYPASSWORD)){
            info.hasPayPassword = contentValues.getAsBoolean(AccountTable.COL_HAS_PAYPASSWORD);
        }
        return info;
    }

    public static AccountHistoryInfo transformFromCursor(Cursor cursor) {
        AccountHistoryInfo info = new AccountHistoryInfo();
        info.userID = cursor.getLong(AccountTable.INDEX_USERID);
        info.TTAccount = cursor.getString(AccountTable.INDEX_TT_ACCOUNT);
        info.username = cursor.getString(AccountTable.INDEX_USERNAME);
        info.phone = cursor.getString(AccountTable.INDEX_PHONE);
        info.password = cursor.getString(AccountTable.INDEX_PWD);
        info.avatarUrl = cursor.getString(AccountTable.INDEX_AVATAR_URL);
        info.lastLoginTime = cursor.getLong(AccountTable.INDEX_LAST_LOGIN_TIME);
        info.hasPayPassword = cursor.getInt(AccountTable.INDEX_HAS_PAYPASSWORD) == 1;
        return info;
    }
}
