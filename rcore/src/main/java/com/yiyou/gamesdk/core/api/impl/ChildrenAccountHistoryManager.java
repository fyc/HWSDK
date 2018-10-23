package com.yiyou.gamesdk.core.api.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.def.IChildrenAccountHistoryApi;
import com.yiyou.gamesdk.core.base.http.RequestHelper;
import com.yiyou.gamesdk.core.base.http.RequestManager;
import com.yiyou.gamesdk.core.base.http.utils.Urlpath;
import com.yiyou.gamesdk.core.base.http.volley.HwRequest;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.storage.Database;
import com.yiyou.gamesdk.core.storage.StorageAgent;
import com.yiyou.gamesdk.core.storage.db.global.ChildrenAccountTable;
import com.yiyou.gamesdk.model.ChildrenAccountHistoryInfo;
import com.yiyou.gamesdk.util.ToastUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by Nekomimi on 2017/4/24.
 */

class ChildrenAccountHistoryManager implements IChildrenAccountHistoryApi {

    private static final String TAG = "RSDK: "+"ChildrenAccountHistoryManager";
    private static final Object lock = new Object();

    private Map<String, ChildrenAccountHistoryInfo> cache = new LinkedHashMap<>();

    public ChildrenAccountHistoryManager() {loadHistoryToCache();}

    @Override
    public ChildrenAccountHistoryInfo getChildrenAccountHistory(String userID) {
        return cache.get(userID);
    }

    @Override
    public List<ChildrenAccountHistoryInfo> getAllChildrenAccountHistories() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void insertOrUpdateChildrenAccountHistory(@NonNull ChildrenAccountHistoryInfo childrenAccountHistoryInfo) {
        cache.put(String.valueOf(childrenAccountHistoryInfo.childrenUserID) , childrenAccountHistoryInfo);
        final ContentValues cvToSubmit = ChildrenAccountHistoryInfo.transformToCV(childrenAccountHistoryInfo);
        StorageAgent.dbAgent().getPublicDatabase()
                .executeTask(new Database.DatabaseTask() {
                    @Override
                    public void process(SQLiteDatabase database) {
                        database.insertWithOnConflict(ChildrenAccountTable.TABLE_NAME, null,
                                cvToSubmit, SQLiteDatabase.CONFLICT_REPLACE);
                    }
                });
    }

    @Override
    public void deleteChildrenAccountHistory(final String childrenUserID) {
        synchronized (lock) {
            cache.remove(childrenUserID);
        }
        StorageAgent.dbAgent().getPublicDatabase()
                .executeTask(new Database.DatabaseTask() {
                    @Override
                    public void process(SQLiteDatabase database) {
                        database.delete(ChildrenAccountTable.TABLE_NAME,
                                ChildrenAccountTable.COL_CHILDREN_USER_ID + " = ?",
                                new String[]{ childrenUserID });
                    }
                });
    }


    @Override
    public List<ChildrenAccountHistoryInfo> getChildrenAccountHistory(@NonNull String userId,@NonNull String gameId) {
        List<ChildrenAccountHistoryInfo> result = new ArrayList<>();
        Log.d(TAG, "getChildrenAccountHistory: " + cache.size());
        for (ChildrenAccountHistoryInfo childrenAccountHistoryInfo: cache.values()){
            if (TextUtils.equals(userId,String.valueOf(childrenAccountHistoryInfo.userID))
                    && TextUtils.equals(gameId,childrenAccountHistoryInfo.gameId) ){
                result.add(childrenAccountHistoryInfo);
            }
        }
        return result;
    }

    @Override
    public List<ChildrenAccountHistoryInfo> getCurrentChildrenAccountHistory() {
        return getChildrenAccountHistory(String.valueOf(ApiFacade.getInstance().getMainUid()), String.valueOf(ApiFacade.getInstance().getCurrentGameID()));
    }

    @Override
    public void updateCurrentChildrenAccount( List<ChildrenAccountHistoryInfo> accountHistoryInfoList) {
        List<ChildrenAccountHistoryInfo> originList = getCurrentChildrenAccountHistory();
        for (ChildrenAccountHistoryInfo childrenAccountHistoryInfo : originList){
            deleteChildrenAccountHistory(childrenAccountHistoryInfo.childrenUserID+"");
        }
        for (ChildrenAccountHistoryInfo childrenAccountHistoryInfo : accountHistoryInfoList){
            insertOrUpdateChildrenAccountHistory(childrenAccountHistoryInfo);
        }
    }

    @Override
    public void editChildrenAccountName(long childUserId, String childUserName, TtRespListener callback) {
        if (childUserId == 0 || TextUtils.isEmpty(childUserName) ){
            ToastUtils.showMsg("输入错误");
            return;
        }
        Map<String, String> params = new ArrayMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("childUserId",String.valueOf(childUserId));
        params.put("childUserName",childUserName);
        HwRequest hwRequest = new HwRequest<>(Urlpath.CHILD_ACCOUNT_UPDATE,params,null,callback);
        RequestManager.getInstance(CoreManager.getContext()).addRequest(hwRequest, null);
    }

    private void loadHistoryToCache() {
        Cursor cursor = StorageAgent.dbAgent().getPublicDatabase()
                .query(false, ChildrenAccountTable.TABLE_NAME, null, null, null, null, null,
                        ChildrenAccountTable.COL_LAST_LOGIN_TIME + " DESC", null);
        if (cursor != null) {
            try {
                synchronized (lock) {
                    while (cursor.moveToNext()) {
                        ChildrenAccountHistoryInfo info = ChildrenAccountHistoryInfo.transformFromCursor(cursor);
                        cache.put(String.valueOf(info.childrenUserID) , info);
                    }
                }
            }finally {
                cursor.close();
            }
        }
    }

}
