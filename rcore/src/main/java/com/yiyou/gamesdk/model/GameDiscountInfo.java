package com.yiyou.gamesdk.model;

import java.util.List;
import java.util.Locale;

/**
 * Created by Nekomimi on 2017/11/22.
 */

public class GameDiscountInfo {

    /**
     * coupons : [{"ruleId":1345,"actId":1174,"couponName":"券2","actTitle":"多渠道","summary":"可用包含sdkdemo等多张游戏优惠券","detail":null,"couponJson":null,"type":"SELECTD_GAME","gameIds":"10000","splitable":0,"amount":20000,"limitAmount":0,"status":0,"endTime":1511335320000},{"ruleId":1350,"actId":1177,"couponName":"现金券1","actTitle":"无渠道","summary":"可用包含sdkdemo等多张游戏优惠券","detail":null,"couponJson":null,"type":"SELECTD_GAME","gameIds":"10000","splitable":0,"amount":4000,"limitAmount":0,"status":1,"endTime":1511336880000},{"ruleId":1358,"actId":1187,"couponName":"test2","actTitle":"02","summary":"可用包含sdkdemo等多张游戏优惠券","detail":null,"couponJson":null,"type":"ALL","gameIds":"","splitable":0,"amount":8800,"limitAmount":0,"status":0,"endTime":1511746920000},{"ruleId":1364,"actId":1194,"couponName":"测试","actTitle":"活动推送","summary":"可用包含sdkdemo等多张游戏优惠券","detail":null,"couponJson":null,"type":"SELECTD_GAME","gameIds":"10000","splitable":0,"amount":4500,"limitAmount":0,"status":0,"endTime":1511770680000},{"ruleId":1363,"actId":1193,"couponName":"oooo","actTitle":"ddd","summary":"可用包含sdkdemo等多张游戏优惠券","detail":null,"couponJson":null,"type":"SELECTD_GAME","gameIds":"10000,10001","splitable":0,"amount":10000,"limitAmount":1000,"status":0,"endTime":1511943360000}]
     * gameDiscount : {"discount":35,"originalDiscount":85,"title":"demo限时折扣活动","endTime":1513310100000}
     */

    private GameDiscount gameDiscount;
    private List<Coupon> coupons;

    private long serverTime;

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public GameDiscount getGameDiscount() {
        return gameDiscount;
    }

    public void setGameDiscount(GameDiscount gameDiscount) {
        this.gameDiscount = gameDiscount;
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    public static class GameDiscount {
        /**
         * discount : 35
         * originalDiscount : 85
         * title : demo限时折扣活动
         * endTime : 1513310100000
         */
        public static final int LIMIT_DISCOUNT = 1;
        public static final int GOLD_DISCOUNT = 2;
        public static final int GOLD_DISCOUNT_COUNTDOWN = 3;

        private int discount;
        private int originalDiscount;
        private String title;
        private long endTime;
        private int discountStatus;

        public int getDiscountByInt() {
            return discount;
        }

        public String getDiscountByString() {
            return String.format(Locale.getDefault(), "%.1f", (double) discount / 10);
        }

        public int getOriginalDiscountByInt() {
            return originalDiscount;
        }

        public String getOriginalDiscountByString() {
            return String.format(Locale.getDefault(), "%.1f", (double) originalDiscount / 10);
        }

        public void setOriginalDiscount(int originalDiscount) {
            this.originalDiscount = originalDiscount;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public int getDiscountStatus() {
            return discountStatus;
        }

        public void setDiscountStatus(int discountStatus) {
            this.discountStatus = discountStatus;
        }
    }

    public static class Coupon {
        /**
         * ruleId : 1345
         * actId : 1174
         * couponName : 券2
         * actTitle : 多渠道
         * summary : 可用包含sdkdemo等多张游戏优惠券
         * detail : null
         * couponJson : null
         * type : SELECTD_GAME
         * gameIds : 10000
         * splitable : 0
         * amount : 20000
         * limitAmount : 0
         * status : 0
         * endTime : 1511335320000
         */

        private int ruleId;
        private int actId;
        private String couponName;
        private String actTitle;
        private String summary;
        private Object detail;
        private Object couponJson;
        private String type;
        private String gameIds;
        private int splitable;
        private int amount;
        private int limitAmount;
        private int status;
        private long endTime;

        public int getRuleId() {
            return ruleId;
        }

        public void setRuleId(int ruleId) {
            this.ruleId = ruleId;
        }

        public int getActId() {
            return actId;
        }

        public void setActId(int actId) {
            this.actId = actId;
        }

        public String getCouponName() {
            return couponName;
        }

        public void setCouponName(String couponName) {
            this.couponName = couponName;
        }

        public String getActTitle() {
            return actTitle;
        }

        public void setActTitle(String actTitle) {
            this.actTitle = actTitle;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public Object getDetail() {
            return detail;
        }

        public void setDetail(Object detail) {
            this.detail = detail;
        }

        public Object getCouponJson() {
            return couponJson;
        }

        public void setCouponJson(Object couponJson) {
            this.couponJson = couponJson;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getGameIds() {
            return gameIds;
        }

        public void setGameIds(String gameIds) {
            this.gameIds = gameIds;
        }

        public int getSplitable() {
            return splitable;
        }

        public void setSplitable(int splitable) {
            this.splitable = splitable;
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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }
    }
}
