package com.mobilegamebar.rsdk.events;


import com.mobilegamebar.rsdk.outer.event.IEventListener;

import java.util.List;

/**
 * Created by levyyoung on 15/5/4.
 */
public interface IEventDispatcher {

    public static final int DISTRIBUTE = 1;
    public static final int BROADCAST  = 2;

    void addDispatcherByTag(String tag, IEventDispatcher dispatcher);
    IEventDispatcher getDispatcherByTag(String tag);
    List<IEventDispatcher> getDispatchers();
    void removeDispatcher(String tag);

    /**
     * 定向分发事件。对指定dispatcher D派发类型为type T的事件。
     * D中所有关注事件类型T的listener都会收到事件调用。
     * 这种分发中,D的子dispatcher不参与分发。
     * @param tag 指定dispatcher的tag
     * @param eventType 事件类型
     * @param params 事件参数
     * @param <P>   事件参数类型
     */
    <P> void distribute(String tag, String eventType, P params);

    /**
     * 广播事件。对发起广播的dispatcher D及其子dispatcher集DS派发类型为type T的事件。
     * D及DS中所有关注事件类型T的listener都会收到事件调用。
     * @param eventType 事件类型
     * @param params 事件参数
     * @param <P> 事件参数类型
     */
    <P> void broadcast(String eventType, P params);

    void addEventListener(Object source, String type, int priority, IEventListener listener);
    void addEventListener(Object source, String type, IEventListener listener);
    void removeEventListener(String type, IEventListener listener);
    void removeEventListenersBySource(Object source);
    void removeEventListenersByType(String type);
    void removeAllEventListeners();

    boolean hasEventListener(String type);

}
