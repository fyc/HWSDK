package com.mobilegamebar.rsdk.outer.model;


/**
 * Created by Win on 17/4/25.
 */
public class GameParamInfo {

    private int    gameId = 0;
    private String SdkKey = "";

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getSdkKey() {
        return SdkKey;
    }

    public void setSdkKey(String sdkKey) {
        SdkKey = sdkKey;
    }

    @Override
    public String toString() {
        return "GameParamInfo{" +
                "gameId=" + gameId +
                ", SdkKey='" + SdkKey + '\'' +
                '}';
    }
}
