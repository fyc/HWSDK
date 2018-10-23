package com.yiyou.gamesdk.model;

public class GameUpdateInfo {

	
	public static final String UPDATE_TYPE_FORCE = "FORCE" ; //强更
	public static final String UPDATE_TYPE_NONFORCE = "NOTFORCE"; //非强更
	public static final String UPDATE_TYPE_INITIAL ="INITIAL";  //初始版本
	public String gameID;
	public String gameName;
	public int gameDisplayId;
	public String versionTittle;
	public String versionType;
	public String gameDownloadUrl;
	public String versionContent; //内容

	@Override
	public String toString() {
		return "GameUpdateInfo{" +
				"gameID='" + gameID + '\'' +
				", gameName='" + gameName + '\'' +
				", versionTittle='" + versionTittle + '\'' +
				", versionType='" + versionType + '\'' +
				", gameDownloadUr='" + gameDownloadUrl + '\'' +
				", versionContent='" + versionContent + '\'' +
				'}';
	}

}
