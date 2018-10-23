package com.yiyou.gamesdk.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.storage.db.global.AnnouncementTable;

/**
 * Created by Win on 2017/3/28.
 */
public class AnnouncementInfo {

    int id;             //公告唯一标识
    String url;         //公告主体展示的页面
    String title;       //公告title
    String content;     //公告内容
    int rank;           //公告优先级
    int type;           //公告类型
    int limitTimes;     //公告显示的次数
    int limitType;      //1:daily,2:all
    int isPlatformBulletin;//0:游戏公告; 1:安卓平台公告
    int times;
    private String day;
    private String firstDay;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getLimitType() {
        return limitType;
    }

    public void setLimitType(int limitType) {
        this.limitType = limitType;
    }

    public int getLimitTimes() {
        return limitTimes;
    }

    public void setLimitTimes(int limitTimes) {
        this.limitTimes = limitTimes;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIsPlatformBulletin() {
        return isPlatformBulletin;
    }

    public void setIsPlatformBulletin(int isPlatformBulletin) {
        this.isPlatformBulletin = isPlatformBulletin;
    }

    public String getFirstDay() {
        return firstDay;
    }

    public void setFirstDay(String firstDay) {
        this.firstDay = firstDay;
    }

    public static AnnouncementInfo transformToCursor(Cursor cursor){
        AnnouncementInfo info = new AnnouncementInfo();
        info.id = cursor.getInt(AnnouncementTable.INDEX_ID);
        info.times = cursor.getInt(AnnouncementTable.INDEX_TIME);
        info.day = cursor.getString(AnnouncementTable.INDEX_LAST_DAY);
        info.firstDay = cursor.getString(AnnouncementTable.INDEX_FIRST_DAY);
        info.title = cursor.getString(AnnouncementTable.INDEX_TITLE);
        info.url = cursor.getString(AnnouncementTable.INDEX_URL);
        info.type = cursor.getInt(AnnouncementTable.INDEX_TYPE);
        return info;
    }

    public static ContentValues transformToContentValue(AnnouncementInfo info) {
        ContentValues cv = new ContentValues();
        cv.put(AnnouncementTable.COL_ID, info.id);
        cv.put(AnnouncementTable.COL_TIME, info.times);
        cv.put(AnnouncementTable.COL_LAST_DAY,info.day);
        cv.put(AnnouncementTable.COL_CHILDREN_UID, String.valueOf(ApiFacade.getInstance().getSubUid()));
        cv.put(AnnouncementTable.COL_FIRST_DAY, info.firstDay);
        cv.put(AnnouncementTable.COL_TITLE, info.title);
        cv.put(AnnouncementTable.COL_URL, info.url);
        cv.put(AnnouncementTable.COL_TYPE, info.isPlatformBulletin);
        return cv;
    }

}
