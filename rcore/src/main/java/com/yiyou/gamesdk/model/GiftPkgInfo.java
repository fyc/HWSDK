package com.yiyou.gamesdk.model;

import android.text.TextUtils;

import com.yiyou.gamesdk.util.CommonUtils;

public class GiftPkgInfo {

    //是否已领取
    public boolean didReceived(){
        return !TextUtils.isEmpty(redeemCode);
    }


    private Integer packageId;
    private Integer gameId;
    private Integer gameDisplayId;
    private String gameName;
    private String packageName;
    private String packageContent;
    private String packageCondition;
    private String expTime;
    private Integer packageTotal;
    private int packageRemain;
    private Integer packageStatus;
    private String uploadTime;
    private String updateTime;
    private String packageRanges;
    private String packageMetho;
    private String redeemCode;
    private int packageAmounts;  //分为单位
    //1: 高价值礼包； 0: 普通礼包
    private int packageType;
    private int userReceiveStatus;  //用户是否达到充值礼包领取条件  1 ： 达到条件领取 ， 2：不能领取

    private String total;
    private String iconUrl;
    private String discription1;
    private String discription2;
    private int rechargepackageId;
    private String gameBundleId;  //打开游戏用的bundleId

    private boolean isFirst;

    public String getGameBundleId() {
        return gameBundleId;
    }

    public void setGameBundleId(String gameBundleId) {
        this.gameBundleId = gameBundleId;
    }

    public int getUserReceiveStatus() {
        return userReceiveStatus;
    }

    public int getPackageType() {
        return packageType;
    }

    public int getRechargepackageId() {
        return rechargepackageId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDiscription1() {
        return discription1;
    }

    public void setDiscription1(String discription1) {
        this.discription1 = discription1;
    }

    public String getDiscription2() {
        return discription2;
    }

    public void setDiscription2(String discription2) {
        this.discription2 = discription2;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getGameDisplayId() {
        return gameDisplayId;
    }

    public void setGameDisplayId(Integer gameDisplayId) {
        this.gameDisplayId = gameDisplayId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageContent() {
        return packageContent;
    }

    public void setPackageContent(String packageContent) {
        this.packageContent = packageContent;
    }

    public String getPackageCondition() {
        return packageCondition;
    }

    public void setPackageCondition(String packageCondition) {
        this.packageCondition = packageCondition;
    }

    public String getExpTime() {
        return expTime;
    }

    public void setExpTime(String expTime) {
        this.expTime = expTime;
    }

    public Integer getPackageTotal() {
        return packageTotal;
    }

    public void setPackageTotal(Integer packageTotal) {
        this.packageTotal = packageTotal;
    }

    public int getPackageRemain() {
        return packageRemain;
    }

    public void setPackageRemain(int packageRemain) {
        this.packageRemain = packageRemain;
    }

    public Integer getPackageStatus() {
        return packageStatus;
    }

    public void setPackageStatus(Integer packageStatus) {
        this.packageStatus = packageStatus;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getPackageRanges() {
        return packageRanges;
    }

    public void setPackageRanges(String packageRanges) {
        this.packageRanges = packageRanges;
    }

    public String getPackageMetho() {
        return packageMetho;
    }

    public void setPackageMetho(String packageMetho) {
        this.packageMetho = packageMetho;
    }

    public String getRedeemCode() {
        return redeemCode;
    }

    public void setRedeemCode(String redeemCode) {
        this.redeemCode = redeemCode;
    }

    public int getPackageAmountsByInt() {
        return packageAmounts;
    }

    public String getPackageAmountsByString() {
        return CommonUtils.pennyToYuan(packageAmounts);
    }

    public void setPackageAmounts(int packageAmounts) {
        this.packageAmounts = packageAmounts;
    }
}