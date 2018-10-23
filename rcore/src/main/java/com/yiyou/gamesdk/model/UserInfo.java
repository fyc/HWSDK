package com.yiyou.gamesdk.model;

/**
 * Created by chenshuide on 15/6/10.
 */
public class UserInfo {


    private long uid;
    private String nickname;
    private String account;
    private String moible;

    @Override
    public String toString() {
        return "UserInfo{" +
                "uid='" + uid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", account='" + account + '\'' +
                ", moible='" + moible + '\'' +
                '}';
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMoible() {
        return moible;
    }

    public void setMoible(String moible) {
        this.moible = moible;
    }
}
