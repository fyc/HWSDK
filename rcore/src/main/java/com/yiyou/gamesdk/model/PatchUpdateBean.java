package com.yiyou.gamesdk.model;

/**
 * Created by win on 17/5/10.
 */
public class PatchUpdateBean {


    /**
     * durl : http://d.52tt.com/DAOTACHUANGQI/v3.0.1/234721
     * action : UPDATE
     * version : 100
     */
    private String downloadUrl;
    private String sdkMd5;
    private String version;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getSdkMd5() {
        return sdkMd5;
    }

    public void setSdkMd5(String sdkMd5) {
        this.sdkMd5 = sdkMd5;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "PatchUpdateBean{" +
                "downloadUrl='" + downloadUrl + '\'' +
                ", sdkMd5='" + sdkMd5 + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
