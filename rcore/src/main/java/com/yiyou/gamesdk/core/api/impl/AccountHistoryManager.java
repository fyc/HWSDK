package com.yiyou.gamesdk.core.api.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.def.IAccountHistoryApi;
import com.yiyou.gamesdk.core.storage.Database;
import com.yiyou.gamesdk.core.storage.StorageAgent;
import com.yiyou.gamesdk.core.storage.db.global.AccountTable;
import com.yiyou.gamesdk.model.AccountHistoryInfo;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.mobilegamebar.rsdk.outer.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by levyyoung on 15/6/24.
 */
class AccountHistoryManager implements IAccountHistoryApi {

    private static final String TAG = "RSDK:AccountHistoryManager ";
    private static final Object lock = new Object();

    private Map<String, AccountHistoryInfo> cache = new LinkedHashMap<>();

    public AccountHistoryManager() {
        loadHistoryToCache();
    }

    @Override
    public AccountHistoryInfo getAccountHistoryByUid(String userID) {
        return cache.get(userID);
    }

    @Override
    public AccountHistoryInfo getAccountHistoryByUserName(String username) {
        AccountHistoryInfo info = null;
        for (AccountHistoryInfo historyInfo : cache.values()) {
            if (historyInfo.username.equals(username)) {
                info = historyInfo;
            }
        }
        return info;
    }

    @Override
    public List<AccountHistoryInfo> getAccountHistories() {
        return new ArrayList<>(cache.values());
    }




    @Override
    public void insertOrUpdateAccountHistory(@NonNull ContentValues accountHistoryCV) {
        String userID = accountHistoryCV.getAsString(AccountTable.COL_USERID);
        if (StringUtils.isBlank(userID)) {
            Log.e(TAG, "error insert or update userID history. invalid userID.");
            return;
        }

        //检查是否是更新
        AccountHistoryInfo orgInfo = getAccountHistoryByUid(userID);
        ContentValues orgCV = null;
        if (orgInfo != null) {
            orgCV = AccountHistoryInfo.transformToCV(orgInfo);
            orgCV.putAll(accountHistoryCV);
        }

        cache.put(userID, AccountHistoryInfo.fromCV(accountHistoryCV));

        final ContentValues cvToSubmit = orgCV == null ? accountHistoryCV : orgCV;
        //搞起
        StorageAgent.dbAgent().getPublicDatabase()
                .executeTask(new Database.DatabaseTask() {
                    @Override
                    public void process(SQLiteDatabase database) {
                        database.insertWithOnConflict(AccountTable.TABLE_NAME, null,
                                cvToSubmit, SQLiteDatabase.CONFLICT_REPLACE);
                    }
                });
    }

    @Override
    public void deleteAccountHistory(final String userID) {
        synchronized (lock) {
            cache.remove(userID);
        }
        StorageAgent.dbAgent().getPublicDatabase()
                .executeTask(new Database.DatabaseTask() {
                    @Override
                    public void process(SQLiteDatabase database) {
                        database.delete(AccountTable.TABLE_NAME,
                                AccountTable.COL_USERID + " = ?",
                                new String[]{userID});
                    }
                });
    }

    private void loadHistoryToCache() {
        Cursor cursor = StorageAgent.dbAgent().getPublicDatabase()
                .query(false, AccountTable.TABLE_NAME, null, null, null, null, null,
                        AccountTable.COL_LAST_LOGIN_TIME + " DESC", null);
        if (cursor != null) {
            try {
                synchronized (lock) {
                    while (cursor.moveToNext()) {
                        AccountHistoryInfo info = AccountHistoryInfo.transformFromCursor(cursor);
                        cache.put(String.valueOf(info.userID), info);
                    }
                }
            } finally {
                cursor.close();
            }
        }
    }

    @Override
    public String getPasswordFromHistory(String account) {
        AccountHistoryInfo info = getAccountHistoryByUid(account);
        if (info != null) {
            return info.password;
        }
        return null;
    }

    /**
     * 获取当前游戏登录历史记录
     *
     * @return 当前游戏登录历史记录
     */
    @Override
    public List<AccountHistoryInfo> getCurrentGameAuthHistories() {
        int currentGameID = ApiFacade.getInstance().getCurrentGameID();
        List<AccountHistoryInfo> histories = new ArrayList<>();
        for (AccountHistoryInfo info : cache.values()) {

                histories.add(info);

        }
        return histories;
    }

    @Override
    public String getPasswordFromHistoryByUsername(String username) {
        AccountHistoryInfo historyInfo = getAccountHistoryByUserName(username);
        if (historyInfo != null) {
            return historyInfo.password;
        }
        return null;
    }

    @Override
    public String getPasswordFromHistoryByPhone(String phone){
        AccountHistoryInfo historyInfo = getHistoryAccountByPhone(phone);
        if (historyInfo != null) {
            return historyInfo.password;
        }
        return null;
    }

    @Override
    public AccountHistoryInfo getCurrentHistoryAccount() {
        String uid = String.valueOf(ApiFacade.getInstance().getMainUid());
        if (cache.containsKey(uid)){
            return cache.get(uid);
        }else {
            return null;
        }
    }

    @Override
    public void insertOrUpdateAccountHistory(@NonNull AccountHistoryInfo accountHistoryInfo) {
        if (accountHistoryInfo.userID == 0) {
            Log.e(TAG, "error insert or update userID history. invalid userID.");
            return;
        }
        cache.put(String.valueOf(accountHistoryInfo.userID),accountHistoryInfo);
        final ContentValues cv = AccountHistoryInfo.transformToCV(accountHistoryInfo);
        StorageAgent.dbAgent().getPublicDatabase()
                .executeTask(new Database.DatabaseTask() {
                    @Override
                    public void process(SQLiteDatabase database) {
                        database.insertWithOnConflict(AccountTable.TABLE_NAME, null,
                                cv, SQLiteDatabase.CONFLICT_REPLACE);
                    }
                });


    }

    @Override
    public AccountHistoryInfo getHistoryAccountByPhone(String phone) {
        if (TextUtils.isEmpty(phone)){
            Log.e(TAG, "no account history found by this phone.");
            return null;
        }
        for (AccountHistoryInfo info : cache.values()){
            if (phone.equals(info.phone)){
                return info;
            }
        }
        return null;
    }

    @Override
    public void refresAccounthHistory() {
        cache.clear();
        loadHistoryToCache();
    }
}
