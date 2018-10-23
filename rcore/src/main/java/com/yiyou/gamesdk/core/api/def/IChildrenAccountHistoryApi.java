package com.yiyou.gamesdk.core.api.def;

import android.support.annotation.NonNull;

import com.yiyou.gamesdk.core.base.http.volley.listener.TtRespListener;
import com.yiyou.gamesdk.model.ChildrenAccountHistoryInfo;

import java.util.List;

/**
 * Created by Nekomimi on 2017/4/24.
 */

public interface IChildrenAccountHistoryApi extends IApiWrapping {

    /**
     * 获取账号历史记录
     * @param childrenUid 账号
     * @return 账号记录 or null
     */
    ChildrenAccountHistoryInfo getChildrenAccountHistory(String childrenUid);

    /**
     *
     * @return 账号记录列表
     */
    List<ChildrenAccountHistoryInfo> getAllChildrenAccountHistories();

    /**
     * 插入或更新账号记录
     * @param childrenAccountHistoryInfo 小号info
     */
    void insertOrUpdateChildrenAccountHistory(@NonNull ChildrenAccountHistoryInfo childrenAccountHistoryInfo);

    /**
     * 根据账号删除记录
     * @param childrenUserID 小号
     */
    void deleteChildrenAccountHistory(String childrenUserID);

    /**
     *
     * @param userId  大号UID
     * @param gameId  当前游戏ID
     * @return  按时间倒序排列的ChildrenAccountHistoryInfo列表
     */
    List<ChildrenAccountHistoryInfo> getChildrenAccountHistory(String userId, String gameId);

    /**
     *
     * @return  按当前游戏账号的时间倒序排列的ChildrenAccountHistoryInfo列表
     */
    List<ChildrenAccountHistoryInfo> getCurrentChildrenAccountHistory();

    void updateCurrentChildrenAccount(List<ChildrenAccountHistoryInfo> accountHistoryInfoList);

    void editChildrenAccountName(long childUserId, String childUserName, TtRespListener callback);

}
