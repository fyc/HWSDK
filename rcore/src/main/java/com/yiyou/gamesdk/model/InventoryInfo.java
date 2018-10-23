package com.yiyou.gamesdk.model;

import java.util.List;

/**
 * Created by Nekomimi on 2017/11/15.
 */

public class InventoryInfo {


    /**
     * inventoryID : 1
     * userID : 27937658
     * phone : 1355254
     * gameID : 10000
     * bundleID : com.yiyou.gamesdk.testapp.syb
     * childUserID : 123
     * childUserName : asdfasdf
     * title : adfadf
     * serverName : afasdf
     * price : 12
     * time : 12
     * auditTime : 12
     * status : 2
     * totalAmount : 23
     * childAccountCreateTime : 34
     * desc : xxxx
     * auditDesc : sdfgsdg
     * materiales : [{"materialID":1,"inventoryID":1,"title":"xxxx","picURL":"http://ooy4j2kxh.bkt.clouddn.com/Winter%20Road%20Trip.jpg","desc":"xxxx"}]
     */

    private int inventoryID;
    private int userID;
    private String phone;
    private int gameID;
    private String bundleID;
    private int childUserID;
    private String childUserName;
    private String title;
    private String serverName;
    private int price;
    private String time;
    private String auditTime;
    private int status;
    private int totalAmount;
    private String childAccountCreateTime;
    private String desc;
    private String auditDesc;
    private List<MaterialesBean> materiales;

    public int getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getBundleID() {
        return bundleID;
    }

    public void setBundleID(String bundleID) {
        this.bundleID = bundleID;
    }

    public int getChildUserID() {
        return childUserID;
    }

    public void setChildUserID(int childUserID) {
        this.childUserID = childUserID;
    }

    public String getChildUserName() {
        return childUserName;
    }

    public void setChildUserName(String childUserName) {
        this.childUserName = childUserName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getChildAccountCreateTime() {
        return childAccountCreateTime;
    }

    public void setChildAccountCreateTime(String childAccountCreateTime) {
        this.childAccountCreateTime = childAccountCreateTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAuditDesc() {
        return auditDesc;
    }

    public void setAuditDesc(String auditDesc) {
        this.auditDesc = auditDesc;
    }

    public List<MaterialesBean> getMateriales() {
        return materiales;
    }

    public void setMateriales(List<MaterialesBean> materiales) {
        this.materiales = materiales;
    }

    public static class MaterialesBean {
        /**
         * materialID : 1
         * inventoryID : 1
         * title : xxxx
         * picURL : http://ooy4j2kxh.bkt.clouddn.com/Winter%20Road%20Trip.jpg
         * desc : xxxx
         */

        private int materialID;
        private int inventoryID;
        private String title;
        private String picURL;
        private String desc;

        public int getMaterialID() {
            return materialID;
        }

        public void setMaterialID(int materialID) {
            this.materialID = materialID;
        }

        public int getInventoryID() {
            return inventoryID;
        }

        public void setInventoryID(int inventoryID) {
            this.inventoryID = inventoryID;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPicURL() {
            return picURL;
        }

        public void setPicURL(String picURL) {
            this.picURL = picURL;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
