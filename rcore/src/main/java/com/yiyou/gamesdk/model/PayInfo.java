package com.yiyou.gamesdk.model;

import java.util.List;

/**
 * Created by Nekomimi on 2017/11/16.
 */

public class PayInfo {


    private double remainFee;
    private String orderNo;
    private double wallet;
    private double cliOrderPrice;
    private double cliPrice;
    private double discount;
    private double originalDiscount;
    private double couponSpend;
    private double walletSpend;
    private long limitDiscountEndTime;
    private long serverTime;
    private String orderTitle;
    private String token;
    private String payChannel;
    private int discountStatus;



    private List<CouponInfo> enableCoupons;
    private List<CouponInfo> disableCoupons;

    public double getRemainFee() {
        return remainFee;
    }

    public void setRemainFee(double remainFee) {
        this.remainFee = remainFee;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    public double getCliOrderPrice() {
        return cliOrderPrice;
    }

    public void setCliOrderPrice(double cliOrderPrice) {
        this.cliOrderPrice = cliOrderPrice;
    }

    public double getCliPrice() {
        return cliPrice;
    }

    public void setCliPrice(double cliPrice) {
        this.cliPrice = cliPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getOriginalDiscount() {
        return originalDiscount;
    }

    public void setOriginalDiscount(double originalDiscount) {
        this.originalDiscount = originalDiscount;
    }

    public double getCouponSpend() {
        return couponSpend;
    }

    public void setCouponSpend(double couponSpend) {
        this.couponSpend = couponSpend;
    }

    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<CouponInfo> getEnableCoupons() {
        return enableCoupons;
    }

    public void setEnableCoupons(List<CouponInfo> enableCoupons) {
        this.enableCoupons = enableCoupons;
    }

    public List<CouponInfo> getDisableCoupons() {
        return disableCoupons;
    }

    public void setDisableCoupons(List<CouponInfo> disableCoupons) {
        this.disableCoupons = disableCoupons;
    }

    public long getLimitDiscountEndTime() {
        return limitDiscountEndTime;
    }

    public void setLimitDiscountEndTime(long limitDiscountEndTime) {
        this.limitDiscountEndTime = limitDiscountEndTime;
    }

    public int getDiscountStatus() {
        return discountStatus;
    }

    public void setDiscountStatus(int discountStatus) {
        this.discountStatus = discountStatus;
    }

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public double getWalletSpend() {
        return walletSpend;
    }

    public void setWalletSpend(double walletSpend) {
        this.walletSpend = walletSpend;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }
}
