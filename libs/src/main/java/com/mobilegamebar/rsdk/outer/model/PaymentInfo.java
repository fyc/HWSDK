package com.mobilegamebar.rsdk.outer.model;

/**
 * Created by levyyoung on 15/5/8.
 */
public class PaymentInfo {

    public static final String PAY_METHOD_ALL = "ALL";

    private String cpOrderId;//游戏的订单id
    private Number cpFee;//金额 0.00 元 格式
    private String subject;//物品名称
    private String body;//物品名称描述
    private String exInfo;// 额外信息
    private String serverId;//游戏 区 id
    private long chargeDate;// 下单时间
    private String payMethod;//支付方法
    private String cpCallbackUrl;//回调支付结果回调url

    /**
     * @return CP订单号
     */
    public String getCpOrderId() {
        return cpOrderId;
    }

    /**
     * 设置CP订单号
     */
    public void setCpOrderId(String cpOrderId) {
        this.cpOrderId = cpOrderId;
    }

    /**
     * @return CP订单金额
     */
    public Number getCpFee() {
        return cpFee;
    }

    /**
     * 设置
     * @param cpFee CP订单金额
     */
    public void setCpFee(Number cpFee) {
        this.cpFee = cpFee;
    }

    /**
     * @return 订单商品名称
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 设置
     * @param subject 订单商品名称
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return 商品描述
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body  设置商品描述
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return CP扩展信息，该字段将会在支付成功后原样返回给CP
     */
    public String getExInfo() {
        return exInfo;
    }

    /**
     * 设置CP扩展信息，该字段将会在支付成功后原样返回给CP
     * @param exInfo CP扩展信息
     */
    public void setExInfo(String exInfo) {
        this.exInfo = exInfo;
    }

    /**
     * 设置
     * @return 游戏服务器id
     */
    public String getServerId() {
        return serverId;
    }

    /**
     *
     * @param serverId 游戏服务器
     */
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    /**
     *
     * @return cp充值时间
     */
    public long getChargeDate() {
        return chargeDate;
    }

    /**
     *
     * @param chargeDate cp充值时间
     */
    public void setChargeDate(long chargeDate) {
        this.chargeDate = chargeDate;
    }

    /**
     *
     * @return 支付方式
     */
    public String getPayMethod() {
        return payMethod;
    }

    /**
     * 设置
     * @param payMethod 支付方式
     */
    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    /**
     *
     * @return 游戏方支付回调
     */
    public String getCpCallbackUrl() {
        return cpCallbackUrl;
    }

    /**
     * 设置
     * @param cpCallbackUrl 游戏方支付回调
     */
    public void setCpCallbackUrl(String cpCallbackUrl) {
        this.cpCallbackUrl = cpCallbackUrl;
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "cpOrderId='" + cpOrderId + '\'' +
                ", cpFee=" + cpFee +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", exInfo='" + exInfo + '\'' +
                ", serverId='" + serverId + '\'' +
                ", chargeDate=" + chargeDate +
                ", payMethod='" + payMethod + '\'' +
                ", cpCallbackUrl='" + cpCallbackUrl + '\'' +
                '}';
    }

}
