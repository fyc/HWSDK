package com.yiyou.gamesdk.model;

import java.util.List;

/**
 * Created by Nekomimi on 2017/8/25.
 */

public class GiftPkgDetailInfo {
    private int gameId;
    private String gameName;
    private String iconUrl;
    private int discount;
    private int limitDiscount;
    private String gameSize;
    private String gameCategory;
    private String gameShortIntroduction;
    private long startTime;
    private List<GiftPkgInfo> gamePackageList;
    private String gameBundleId;

    private int rechargepackageType;  //是否限时礼包
    private long recLimitStartTime;  //限时礼包开始时间
    private long recLimitEndTime; //限时礼包结束时间

    public String getGameBundleId() {
        return gameBundleId;
    }

    public int getRechargepackageType() {
        return rechargepackageType;
    }

    public void setRechargepackageType(int rechargepackageType) {
        this.rechargepackageType = rechargepackageType;
    }

    public long getRecLimitStartTime() {
        return recLimitStartTime;
    }

    public void setRecLimitStartTime(long recLimitStartTime) {
        this.recLimitStartTime = recLimitStartTime;
    }

    public long getRecLimitEndTime() {
        return recLimitEndTime;
    }

    public void setRecLimitEndTime(long recLimitEndTime) {
        this.recLimitEndTime = recLimitEndTime;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getLimitDiscount() {
        return limitDiscount;
    }

    public void setLimitDiscount(int limitDiscount) {
        this.limitDiscount = limitDiscount;
    }

    public String getGameSize() {
        return gameSize;
    }

    public void setGameSize(String gameSize) {
        this.gameSize = gameSize;
    }

    public String getGameCategory() {
        return gameCategory;
    }

    public void setGameCategory(String gameCategory) {
        this.gameCategory = gameCategory;
    }

    public String getGameShortIntroduction() {
        return gameShortIntroduction;
    }

    public void setGameShortIntroduction(String gameShortIntroduction) {
        this.gameShortIntroduction = gameShortIntroduction;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public List<GiftPkgInfo> getGamePackageList() {
        return gamePackageList;
    }

    public void setGamePackageList(List<GiftPkgInfo> gamePackageList) {
        this.gamePackageList = gamePackageList;
    }


}
