package com.mobilegamebar.rsdk.outer.event;

import com.mobilegamebar.rsdk.events.IEventDispatcher;

import java.util.List;

/**
 * Created by levyyoung on 15/5/4.
 */
public class EventDispatcherAgent implements IEventDispatcher {

    private static EventDispatcherAgent _Instance = null;
    private static EventDispatcher defaultDispatcher = null;
    public synchronized static EventDispatcherAgent defaultAgent() {
        if (_Instance == null) {
            _Instance = new EventDispatcherAgent();
        }
        return _Instance;
    }

    private EventDispatcherAgent() {
        defaultDispatcher = new EventDispatcher();
    }

    @Override
    public void addDispatcherByTag(String tag, IEventDispatcher dispatcher) {
        defaultDispatcher.addDispatcherByTag(tag,dispatcher);
    }

    @Override
    public IEventDispatcher getDispatcherByTag(String tag) {
        return defaultDispatcher.getDispatcherByTag(tag);
    }

    @Override
    public List<IEventDispatcher> getDispatchers() {
        return defaultDispatcher.getDispatchers();
    }

    @Override
    public void removeDispatcher(String tag) {
        defaultDispatcher.removeDispatcher(tag);
    }

    /**
     * 定向分发事件。对指定dispatcher D派发类型为type T的事件。
     * D中所有关注事件类型T的listener都会收到事件调用。
     * 这种分发中,D的子dispatcher不参与分发。
     *
     * @param tag    指定dispatcher的tag
     * @param params 事件参数
     */
    @Override
    public <P> void distribute(final String tag, final String eventType, final P params) {
        defaultDispatcher.distribute(tag, eventType, params);
    }

    /**
     * 广播事件。对发起广播的dispatcher D及其子dispatcher集DS派发类型为type T的事件。
     * D及DS中所有关注事件类型T的listener都会收到事件调用。
     *
     * @param params 事件参数
     */
    @Override
    public <P> void broadcast(final String eventType, final P params) {
        defaultDispatcher.broadcast(eventType, params);
    }

    @Override
    public void addEventListener(Object source, String type, int priority, IEventListener listener) {
        defaultDispatcher.addEventListener(source, type, priority, listener);
    }

    @Override
    public void addEventListener(Object source, String type, IEventListener listener) {
        defaultDispatcher.addEventListener(source, type, 0, listener);
    }

    @Override
    public void removeEventListener(String type, IEventListener listener) {
        defaultDispatcher.removeEventListener(type, listener);
    }

    @Override
    public void removeEventListenersBySource(Object source) {
        defaultDispatcher.removeEventListenersBySource(source);
    }

    @Override
    public void removeEventListenersByType(String type) {
        defaultDispatcher.removeEventListenersByType(type);
    }

    @Override
    public void removeAllEventListeners() {
        defaultDispatcher.removeAllEventListeners();
    }

    @Override
    public boolean hasEventListener(String type) {
        return defaultDispatcher.hasEventListener(type);
    }

}
