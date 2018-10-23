package com.yiyou.gamesdk.model;

/**
 * Created by levyyoung on 15/7/7.
 */
public class InternalOrderInfo {

    private String cpOrderID;
    private String sdkOrderID;
    private String payUrl;
    private String orderInfoUrl;
    private int hasSetSecurity;
    private int realNeeded;

    public int getHasSetSecurity() {
        return hasSetSecurity;
    }

    public void setHasSetSecurity(int hasSetSecurity) {
        this.hasSetSecurity = hasSetSecurity;
    }

    public String getCpOrderID() {
        return cpOrderID;
    }

    public void setCpOrderID(String cpOrderID) {
        this.cpOrderID = cpOrderID;
    }

    public String getSdkOrderID() {
        return sdkOrderID;
    }

    public void setSdkOrderID(String sdkOrderID) {
        this.sdkOrderID = sdkOrderID;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public int getRealNeeded() {
        return realNeeded;
    }

    public void setRealNeeded(int realNeeded) {
        this.realNeeded = realNeeded;
    }

    public String getOrderInfoUrl() {
        return orderInfoUrl;
    }

    public void setOrderInfoUrl(String orderInfoUrl) {
        this.orderInfoUrl = orderInfoUrl;
    }
}
