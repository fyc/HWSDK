package com.yiyou.gamesdk.model;

import java.util.List;

/**
 * Created by Nekomimi on 2017/11/15.
 */

public class InventoriesInfo {

    /**
     * @type : type.googleapis.com/trade.GetInventoriesResp
     * count : 1
     * inventories : [{"inventoryID":1,"userID":27937658,"phone":"1355254","gameID":10000,"bundleID":"com.yiyou.gamesdk.testapp.syb","childUserID":123,"childUserName":"asdfasdf","title":"adfadf","serverName":"afasdf","price":12,"time":"12","auditTime":"12","status":2,"totalAmount":23,"childAccountCreateTime":"34","desc":"xxxx","auditDesc":"sdfgsdg","materiales":[{"materialID":1,"inventoryID":1,"title":"xxxx","picURL":"http://ooy4j2kxh.bkt.clouddn.com/Winter%20Road%20Trip.jpg","desc":"xxxx"}]}]
     */

    private String count;
    private List<InventoryInfo> inventories;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<InventoryInfo> getInventories() {
        return inventories;
    }

    public void setInventories(List<InventoryInfo> inventories) {
        this.inventories = inventories;
    }


}
