package com.mobilegamebar.rsdk.outer.event;

/**
 * Created by levyyoung on 15/5/4.
 */
public interface IEventListener<P> {

    /**
     * @param eventType 事件类型
     * @param params
     */
    void onEvent(String eventType, P params);

}
