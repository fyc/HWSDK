package com.yiyou.gamesdk.model;

import java.util.List;

/**
 * Created by Nekomimi on 2017/11/16.
 */

public class GamePackages {


    private List<GamePackageInfo> gamePackageList;

    public List<GamePackageInfo> getGamePackageList() {
        return gamePackageList;
    }

    public void setGamePackageList(List<GamePackageInfo> gamePackageList) {
        this.gamePackageList = gamePackageList;
    }

    public static class GamePackageInfo {


        private int packageId;
        private int gameId;
        private int gameDisplayId;
        private String gameName;
        private String packageName;
        private String packageContent;
        private String packageCondition;
        private String expTime;
        private int packageTotal;
        private int packageRemain;
        private int packageStatus;
        private String uploadTime;
        private String updateTime;
        private String packageRanges;
        private String packageMetho;
        private String redeemCode;
        private String iconUrl;
        private int packageType;
        private int packageAmounts;
        private int userReceiveStatus;

        public int getPackageId() {
            return packageId;
        }

        public void setPackageId(int packageId) {
            this.packageId = packageId;
        }

        public int getGameId() {
            return gameId;
        }

        public void setGameId(int gameId) {
            this.gameId = gameId;
        }

        public int getGameDisplayId() {
            return gameDisplayId;
        }

        public void setGameDisplayId(int gameDisplayId) {
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

        public int getPackageTotal() {
            return packageTotal;
        }

        public void setPackageTotal(int packageTotal) {
            this.packageTotal = packageTotal;
        }

        public int getPackageRemain() {
            return packageRemain;
        }

        public void setPackageRemain(int packageRemain) {
            this.packageRemain = packageRemain;
        }

        public int getPackageStatus() {
            return packageStatus;
        }

        public void setPackageStatus(int packageStatus) {
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

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public int getPackageType() {
            return packageType;
        }

        public void setPackageType(int packageType) {
            this.packageType = packageType;
        }

        public int getPackageAmounts() {
            return packageAmounts;
        }

        public void setPackageAmounts(int packageAmounts) {
            this.packageAmounts = packageAmounts;
        }

        public int getUserReceiveStatus() {
            return userReceiveStatus;
        }

        public void setUserReceiveStatus(int userReceiveStatus) {
            this.userReceiveStatus = userReceiveStatus;
        }
    }
}
