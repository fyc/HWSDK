package com.yiyou.gamesdk.model;

/**
 *
 * Created by Nekomimi on 2017/6/15.
 */

public class CouponInfo {
    //优惠券id
    private String id;
    //优惠券状态('UNUSED':未使用,'USED':已使用,'EXPIRED':已过期,'DISABLE':失效)
    private String status;
    //优惠券金额
    private int amount;
    //满足优惠券使用限额
    private int limitAmount;
    //生效时间
    private String effectiveDate;
    //失效时间
    private String expiryDate;
    //详情
    private String comment;
    //标题
    private String name;
    //使用限制概要
    private String summary;
    //使用限制详情
    private String detail;
    //失效时间
    private String spendingTime;

    public final static String UNUSED = "UNUSED";
    public final static String USED = "USED";
    public final static String EXPIRED = "EXPIRED";


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(int limitAmount) {
        this.limitAmount = limitAmount;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getSpendingTime() {
        return spendingTime;
    }

    public void setSpendingTime(String spendingTime) {
        this.spendingTime = spendingTime;
    }
}
