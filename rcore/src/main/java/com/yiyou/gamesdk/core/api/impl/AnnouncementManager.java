package com.yiyou.gamesdk.core.api.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.google.gson1.Gson;
import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.api.def.IAnnouncementApi;
import com.yiyou.gamesdk.core.base.http.RequestHelper;
import com.yiyou.gamesdk.core.base.http.RequestManager;
import com.yiyou.gamesdk.core.base.http.utils.Urlpath;
import com.yiyou.gamesdk.core.base.http.volley.HwRequest;
import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.core.storage.Database;
import com.yiyou.gamesdk.core.storage.StorageAgent;
import com.yiyou.gamesdk.core.storage.db.global.AnnouncementTable;
import com.yiyou.gamesdk.model.AnnouncementInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * Created by Nekomimi on 2017/6/10.
 */
public class AnnouncementManager implements IAnnouncementApi {

    private static final String TAG = "TTSDK: " + "PostLoginInfoManager";
    private static final Object lock = new Object();




    public AnnouncementInfo getAnnouncement(int id) {
        String childrenUid = ApiFacade.getInstance().getSubUid() + "";
        Cursor cursor = StorageAgent.dbAgent().getPrivateDatabase()
                .query(false, AnnouncementTable.TABLE_NAME, null, AnnouncementTable.COL_ID + " = ?"
                                + " AND " + AnnouncementTable.COL_CHILDREN_UID + " = ?" ,
                        new String[]{String.valueOf(id), childrenUid},
                        null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    AnnouncementInfo loginmessageInfo = AnnouncementInfo.transformToCursor(cursor);
                    Log.d(TAG, "getLoginMessageInfo : " + loginmessageInfo);
                    return loginmessageInfo;
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }


    public void insertAnnounInfo(final AnnouncementInfo Info) {
        Database.DatabaseTask databaseTask = new Database.DatabaseTask() {
            @Override
            public void process(SQLiteDatabase database) {
                if (Info != null) {
                    ContentValues values = AnnouncementInfo.transformToContentValue(Info);
                    database.insert(AnnouncementTable.TABLE_NAME, null, values);
                    Log.d(TAG, "insertNewNoticeInfo : " + Info);
                }
            }
        };

        StorageAgent.dbAgent().getPrivateDatabase().executeTask(databaseTask);
    }
//
//
    public void updateAnnouncementInfo(final AnnouncementInfo Info) {
        Database.DatabaseTask databaseTask = new Database.DatabaseTask() {
            @Override
            public void process(SQLiteDatabase database) {
                if (Info != null) {
                    ContentValues values = AnnouncementInfo.transformToContentValue(Info);
                    String childrenUid = ApiFacade.getInstance().getSubUid() + "";
                    String whereClause = AnnouncementTable.COL_ID + " = ?" + " AND " + AnnouncementTable.COL_CHILDREN_UID + " = ?";
                    String[] whereArg = {String.valueOf(Info.getId()), childrenUid};
                    database.update(AnnouncementTable.TABLE_NAME, values, whereClause, whereArg);
                    Log.d(TAG, "updateAnnouncementInfo : " + Info);
                }
            }
        };
        StorageAgent.dbAgent().getPrivateDatabase().executeTask(databaseTask);
    }



    private AnnouncementInfo handleAnnouncement(AnnouncementInfo info) {
        Log.d(TAG, "handleAnnouncement: ");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        AnnouncementInfo infoTemp = getAnnouncement(info.getId());
        if (infoTemp == null){
            info.setTimes(1);
            info.setDay(format.format(new Date()));
            info.setFirstDay(String.valueOf(new Date().getTime()));
            insertAnnounInfo(info);
            return info;
        }
        if (info.getLimitType() == 1){
            Log.d(TAG, "handleAnnouncement: limitType1: " + format.format(new Date()));
            if(!format.format(new Date()).equals(infoTemp.getDay())){
                info.setTimes(1);
                info.setDay(format.format(new Date()));
                info.setFirstDay(TextUtils.isEmpty(infoTemp.getFirstDay()) ? String.valueOf(new Date().getTime()) : infoTemp.getFirstDay());
                updateAnnouncementInfo(info);
                return info;
            }else {
                int time = infoTemp.getTimes();
                if (time >= info.getLimitTimes()){
                    return null;
                }else {
                    info.setTimes(time+1);
                    info.setDay(format.format(new Date()));
                    info.setFirstDay(TextUtils.isEmpty(infoTemp.getFirstDay()) ? String.valueOf(new Date().getTime()) : infoTemp.getFirstDay());
                    updateAnnouncementInfo(info);
                    return info;
                }
            }
        }else if (info.getLimitType() == 0){
            int time = infoTemp.getTimes();
            if (time >= info.getLimitTimes()){
                return null;
            }else {
                info.setTimes(time+1);
                info.setDay(format.format(new Date()));
                info.setFirstDay(TextUtils.isEmpty(infoTemp.getFirstDay()) ? String.valueOf(new Date().getTime()) : infoTemp.getFirstDay());
                updateAnnouncementInfo(info);
                return info;
            }
        }
        return null;
    }


    @Override
    public void requestAnnouncement(final int from, final IOperateCallback<List<AnnouncementInfo>> callback) {//from:1-登录，2-悬浮球，3-退出
        Map<String, String> params = new TreeMap<>();
        RequestHelper.buildParamsWithBaseInfo(params);
        params.put("uid", String.valueOf(ApiFacade.getInstance().getSubUid()));
        params.put("gid", String.valueOf(ApiFacade.getInstance().getCurrentGameID()));
        HwRequest request = new HwRequest<>(Urlpath.ANNOUNCEMENT, params,null,new TtRespListener<JSONObject>(){

            @Override
            public void onNetSucc(String url, Map<String, String> params, JSONObject jsonObject) {
                super.onNetSucc(url, params, jsonObject);
                List<AnnouncementInfo> results = new ArrayList<>();
                String message = "";
                int result = handleResponse(jsonObject, results, message);
                callback.onResult(result, results);
            }

            @Override
            public void onNetError(String url, Map<String, String> params, String errno, String errmsg) {
                super.onNetError(url, params, errno, errmsg);
            }

            @Override
            public void onFail(int errorNo, String errmsg) {
                super.onFail(errorNo, errmsg);
            }
        });

        RequestManager.getInstance(CoreManager.getContext()).addRequest(request, null);
    }

    /**
     * 处理不同的返回格式
     * @param jsonObject 返回的源json
     * @param results  处理成功后把公告列表放到这里,处理失败把result置空
     * @param m       接受处理后的结果信息
     * @return       返回的结果
     */
    private int handleResponse(JSONObject jsonObject, List<AnnouncementInfo> results, String m){
        Log.d(TAG, "handleResponse: ");
        int result = 0;
        try {
            JSONObject state = jsonObject.getJSONObject("state");
            result = state.getInt("code");
            m = state.getString("message");
            if (result == 2000000){
                Log.d(TAG, "handleResponse: " + "data");
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray != null && jsonArray.length() > 0){
                    Gson gson = new Gson();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject job = jsonArray.optJSONObject(i);
                        AnnouncementInfo infoBf = gson.fromJson(job.toString(), AnnouncementInfo.class);
                        AnnouncementInfo infoAf = handleAnnouncement(infoBf);
                        if (infoAf != null) {
                            results.add(infoAf);
                        }
                    }
                    Collections.sort(results, new Comparator<AnnouncementInfo>() {
                        @Override
                        public int compare(AnnouncementInfo noticeInfo, AnnouncementInfo t1) {
                            return noticeInfo.getRank() - t1.getRank();
                        }
                    });
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
            result = 100;
            m = e.getMessage();
            results.clear();
            return result;
        }

        return result;
    }
    /**
     * 数据库读公告信息
     * @param Msgid 公告Id
     * @return 公告model
     */
    private AnnouncementInfo getAnnouncementInfo(int Msgid){
//        String userID = ApiFacade.getInstance().getMainUid() + "";
//        Cursor cursor = StorageAgent.dbAgent().getPublicDatabase()
//                .query(false, AnnouncementTable.TABLE_NAME, null, AnnouncementTable.COL_ID + " = ?"
//                                + " AND " + AnnouncementTable.COL_UID + " = ?"+" AND " + AnnouncementTable.COL_LIMIT_TIMES + " = ?",
//                        new String[]{String.valueOf(Msgid), userID, String.valueOf(0)},
//                        null, null, null, null);
//
//        if (cursor != null) {
//            try {
//                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
//                    AnnouncementInfo newNoticeInfo = AnnouncementInfo.transformToCursor(cursor);
//                    Log.d(TAG, "getNewNoticeInfo : " + newNoticeInfo);
//                    return newNoticeInfo;
//                }
//            } finally {
//                cursor.close();
//            }
//        }
        return null;
    }

    @Override
    public List<AnnouncementInfo> getLocalAnnouncement() {
        String childrenUid = ApiFacade.getInstance().getSubUid() + "";
        Map<String, AnnouncementInfo> cache = new LinkedHashMap<>();
        Cursor cursor = StorageAgent.dbAgent().getPrivateDatabase()
                    .query(false, AnnouncementTable.TABLE_NAME, null, AnnouncementTable.COL_CHILDREN_UID + " = ?" ,
                            new String[]{ childrenUid},
                            null, null, AnnouncementTable.COL_FIRST_DAY + " desc", "20");
            if (cursor != null) {
                try {
                    synchronized (lock) {
                        while (cursor.moveToNext()) {
                            AnnouncementInfo info = AnnouncementInfo.transformToCursor(cursor);
                            cache.put(String.valueOf(info.getId()), info);
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
        List<AnnouncementInfo> histories = new ArrayList<>();
        histories.addAll(cache.values());
        return histories;
    }

}
