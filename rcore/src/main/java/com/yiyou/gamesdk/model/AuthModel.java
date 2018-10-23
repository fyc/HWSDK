package com.yiyou.gamesdk.model;

import java.util.List;

/**
 * Created by win on 17/4/24.
 */
public class AuthModel {
    private long userID;
    private String TTAccount;
    private String userName;
    private String phone;
    private String avatarURL;
    private String accessToken = "";
    private String refreshToken;
    private boolean hasPayPassword;
    private int expiresIn;
    private String channel;
    private String realName;
    private String realCardNo;
    private String realMobile;
    private String realVerified;
    private String realModifiedCnt;
    private List<childAccount> childAccounts ;

    public class childAccount{
        private long userID;
        private long childUserID;
        private String childUserName;
        private String bundleID;
        private String TTAccount;

        public long getUserID() {
            return userID;
        }

        public void setUserID(long userID) {
            this.userID = userID;
        }

        public long getChildUserID() {
            return childUserID;
        }

        public void setChildUserID(long childUserID) {
            this.childUserID = childUserID;
        }

        public String getChildUserName() {
            return childUserName;
        }

        public void setChildUserName(String childUserName) {
            this.childUserName = childUserName;
        }

        public String getBundleID() {
            return bundleID;
        }

        public void setBundleID(String bundleID) {
            this.bundleID = bundleID;
        }

        public String getTTAccount() {
            return TTAccount;
        }

        public void setTTAccount(String TTAccount) {
            this.TTAccount = TTAccount;
        }
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getTTAccount() {
        return TTAccount;
    }

    public void setTTAccount(String TTAccount) {
        this.TTAccount = TTAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRealCardNo() {
        return realCardNo;
    }

    public void setRealCardNo(String realCardNo) {
        this.realCardNo = realCardNo;
    }

    public String getRealMobile() {
        return realMobile;
    }

    public void setRealMobile(String realMobile) {
        this.realMobile = realMobile;
    }

    public String getRealVerified() {
        return realVerified;
    }

    public void setRealVerified(String realVerified) {
        this.realVerified = realVerified;
    }

    public String getRealModifiedCnt() {
        return realModifiedCnt;
    }

    public boolean isHasPayPassword() {
        return hasPayPassword;
    }

    public void setHasPayPassword(boolean hasPayPassword) {
        this.hasPayPassword = hasPayPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRealModifiedCnt(String realModifiedCnt) {
        this.realModifiedCnt = realModifiedCnt;
    }

    public List<AuthModel.childAccount> getChildAccounts() {
        return childAccounts;
    }

    public void setChildAccounts(List<AuthModel.childAccount> childAccounts) {
        this.childAccounts = childAccounts;
    }

    @Override
    public String toString() {
        return "AuthModel{" +
                "userID=" + userID +
                ", TTAccount='" + TTAccount + '\'' +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", channel='" + channel + '\'' +
                ", expiresIn=" + hasPayPassword + '\'' +
                ", realName='" + realName + '\'' +
                ", realCardNo='" + realCardNo + '\'' +
                ", realMobile='" + realMobile + '\'' +
                ", realVerified='" + realVerified + '\'' +
                ", realModifiedCnt='" + realModifiedCnt + '\'' +
                ", childAccounts=" + childAccounts +
                '}';
    }
}
