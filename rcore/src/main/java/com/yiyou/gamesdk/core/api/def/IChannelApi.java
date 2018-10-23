package com.yiyou.gamesdk.core.api.def;

/**
 * Created by levyyoung on 15/6/15.
 */
public interface IChannelApi extends IApiWrapping {

    /**
     * 从配置或安装包读取渠道信息。 必须在setCurrentGameId后调用
     */
    void setupChannelInfo();

    /**
     * 设置当前游戏Id.
     * 游戏ID由CP传入，initCoreManager时设置。
     * @param gameID 游戏Id
     */
    void setCurrentGameID(int gameID);

    /**
     * 获取
     * @return 当前游戏ID
     */
    int getCurrentGameID();


    /**
     * 获取
     * 从当前游戏软件信息中获取
     * @return 当前游戏名称
     */
    String getCurrentGameName();

    String getChannel();

    String getSdkKey();

    void setSdkKey(String key);

}
