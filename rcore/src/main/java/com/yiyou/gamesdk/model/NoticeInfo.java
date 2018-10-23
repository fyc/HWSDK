package com.yiyou.gamesdk.model;

import java.util.Arrays;

/**
 * Created by win on 2016-4-8.
 */
public class NoticeInfo {
    private int[] gameIds;
    private String[] cpIds;
    private String title;
    private String htmlText;

    public int[] getGameid() {
        return gameIds;
    }

    public void setGameid(int[] gameids) {
        this.gameIds = gameids;
    }

    public String[] getCpid() {
        return cpIds;
    }

    public void setCpid(String[] cpids) {
        this.cpIds = cpids;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    @Override
    public String toString() {
        return "NoticeInfo{" +
                "gameid=" + Arrays.toString(gameIds) +
                ", cpid=" + Arrays.toString(cpIds) +
                ", title='" + title + '\'' +
                ", htmlText='" + htmlText + '\'' +
                '}';
    }
}
