package com.yiyou.gamesdk.core.api.def;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.yiyou.gamesdk.model.AccountHistoryInfo;

import java.util.List;

/**
 * Created by levyyoung on 15/6/24.
 */
public interface IAccountHistoryApi extends IApiWrapping {

    /**
     * 获取账号历史记录
     * @param userID 账号
     * @return 账号记录 or null
     */
    AccountHistoryInfo getAccountHistoryByUid(String userID);

    /**
     *
     * @return 账号记录列表
     */
    List<AccountHistoryInfo> getAccountHistories();

    /**
     * 插入或更新账号记录
     * @param accountHistoryCV 账号记录CV. 必须包含account字段
     */
    void insertOrUpdateAccountHistory(@NonNull ContentValues accountHistoryCV);

    /**
     * 根据账号删除记录
     * @param userID 账号
     */
    void deleteAccountHistory(String userID);

    /**
     * 从历史记录获取登录密码
     * @param userID 账号
     * @return md5密码
     */
    String getPasswordFromHistory(String userID);

    /**
     * 从历史记录获取登录密码
     * @param username 账号
     * @return md5密码
     */
    String getPasswordFromHistoryByUsername(String username);

    /**
     * 从历史记录获取登录密码
     * @param phone 手机号
     * @return md5密码
     */
    String getPasswordFromHistoryByPhone(String phone);

//    /**
//     * 修改历史记录中的密码
//     * @param passwd
//     */
//    void resetPasswd(String passwd);

    /**
     * 获取当前游戏登录历史记录
     *
     * @return 当前游戏登录历史记录
     */
     List<AccountHistoryInfo> getCurrentGameAuthHistories();


    /**
     * 获取账号历史记录
     * @param username 用户名
     * @return 账号记录 or null
     */
    AccountHistoryInfo getAccountHistoryByUserName(String username);

    /**
     * 获取当前账号的AccountHistoryInfo
     * @return 当前账号的历史信息
     */
    AccountHistoryInfo getCurrentHistoryAccount();


    /**
     * 根据手机号获取历史用户信息
     * @return AccountHistoryInfo
     */
    AccountHistoryInfo getHistoryAccountByPhone(String phone);


    /**
     * 插入或更新账号记录
    * @param accountHistoryInfo 账号记录. 必须包含userId
    */
    void insertOrUpdateAccountHistory(@NonNull AccountHistoryInfo accountHistoryInfo);

    /**
     * 刷新历史记录
     */
    void refresAccounthHistory();
}
