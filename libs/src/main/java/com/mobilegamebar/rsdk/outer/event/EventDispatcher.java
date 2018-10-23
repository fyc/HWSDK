package com.mobilegamebar.rsdk.outer.event;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.mobilegamebar.rsdk.events.EventListenerHolder;
import com.mobilegamebar.rsdk.events.IEventDispatcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by levyyoung on 15/5/4.
 */
public class EventDispatcher implements IEventDispatcher {

    //最多同时存在一线程，线程空闲超过60秒则回收。
    static ExecutorService executorService = new ThreadPoolExecutor(0, 1,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private static final String TAG = "RSDK:EventDispatcher ";

    private ArrayMap<String, IEventDispatcher> subDispatchers = new ArrayMap<>();
    private Handler dispatchHandler = null;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static class EventSourceRef{
        public EventSourceRef(int sourceId, @NonNull EventListenerHolder holder,
                              @NonNull PriorityQueue<EventListenerHolder> holderQueue) {
            this.sourceId = sourceId;
            this.holder = holder;
            this.holderQueue = holderQueue;
        }

        public int sourceId;
        public EventListenerHolder holder;
        public PriorityQueue<EventListenerHolder> holderQueue;

        public void clear() {
            sourceId = 0;
            holder = null;
            holderQueue = null;
        }
    }

    ArrayMap<String, PriorityQueue<EventListenerHolder>> listenerHolder = new ArrayMap<>();
    ArrayMap<Integer, Set<IEventListener>> sourceMapping = new ArrayMap<>(); //来源表
    ArrayMap<IEventListener, EventSourceRef> reverseSourceMapping = new ArrayMap<>();//反向来源表


    private AtomicInteger atomicInteger = new AtomicInteger();

    public int genateSeq() {
        return atomicInteger.incrementAndGet();
    }

    private Set<IEventListener> getSourceListeners(int sourceId) {
        Set<IEventListener> listeners = sourceMapping.get(sourceId);
        if (listeners == null) {
            listeners = new HashSet<>();
            sourceMapping.put(sourceId, listeners);
        }
        return listeners;
    }

    public EventDispatcher() {
        this(null);
    }

    public EventDispatcher(Handler handler) {
        if (handler != null) {
            this.dispatchHandler = handler;
        } else {
            this.dispatchHandler = new Handler(Looper.getMainLooper());
        }

    }

    @Override
    public void addDispatcherByTag(String tag, IEventDispatcher dispatcher) {
        Log.d(TAG, String.format("[ add dispatcher by tag. tag %s dispatcher %s]", tag, dispatcher.toString()));
        lock.writeLock().lock();
        try {
            subDispatchers.put(tag, dispatcher);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public IEventDispatcher getDispatcherByTag(String tag) {
        Log.d(TAG, String.format("[ get dispatcher by tag %s]", tag));
        lock.readLock().lock();
        try{
            return subDispatchers.get(tag);
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<IEventDispatcher> getDispatchers() {
        Log.d(TAG, "get dispatchers.");
        lock.readLock().lock();
        try {
            return new ArrayList<>(subDispatchers.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void removeDispatcher(String tag) {
        Log.d(TAG, String.format("[ remove dispatcher by tag %s ]", tag));
        lock.writeLock().lock();
        try{
            subDispatchers.remove(tag);
        }finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 定向分发事件。对指定dispatcher D派发类型为type T的事件。
     * D中所有关注事件类型T的listener都会收到事件调用。
     * 这种分发中,D的子dispatcher不参与分发。
     *
     * @param tag 指定dispatcher的tag
     * @param params        事件参数
     */
    @Override
    public <P> void distribute(final String tag, final String eventType, final P params) {
        Log.d(TAG, String.format("[ distribute. tag %s params %s]", tag, params));
        lock.readLock().lock();
        try{
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    distribute(subDispatchers.get(tag), eventType, params);
                }
            });
        }finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 广播事件。对发起广播的dispatcher D及其子dispatcher集DS派发类型为type T的事件。
     * D及DS中所有关注事件类型T的listener都会收到事件调用。
     *
     * @param params 事件参数
     */
    @Override
    public <P> void broadcast(final String eventType, final P params) {
        Log.d(TAG, String.format("[ broadcast. params %s ]", "" + params));
        lock.readLock().lock();
        try {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(eventType)) {
                        Log.e(TAG, "[ unknown event type. stop broadcasting. ]");
                        return;
                    }
                    distribute(EventDispatcher.this, eventType, params);
                    for (IEventDispatcher dispatcher : getDispatchers()) {
                        dispatcher.broadcast(eventType,params);
                    }
                }
            });
        } finally {
            lock.readLock().unlock();
        }
    }

    private <P> void distribute(IEventDispatcher dispatcher,
                                final String eventType, final P params) {
        if (dispatcher == null) {
            Log.e(TAG,"[ dispatcher is null. stop distributing. ]");
            return;
        }
        if (TextUtils.isEmpty(eventType)) {
            Log.e(TAG, "[ unknown event type. stop distributing. ]");
            return;
        }
        PriorityQueue<EventListenerHolder> holders = listenerHolder.get(eventType);
        if (holders != null) {
            for (final EventListenerHolder holder : holders) {
                final IEventListener iEventListener = holder.getListener();
                if (iEventListener != null) {
                    dispatchHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            iEventListener.onEvent(eventType, params);
                        }
                    });
                } else {
                    //当弱引用 get 为null remove holder
                    holders.remove(holder);
                }


            }
        }
    }

    @Override
    public void addEventListener(@NonNull Object source, @NonNull String type, int priority,
                                 @NonNull IEventListener listener) {
        Log.d(TAG, String.format("[ try add event listener. type %s priority %d listener %s]",
                type, priority, listener.toString()));
        lock.writeLock().lock();
        try {
            int sourceId = source.hashCode();
            Set<IEventListener> listeners = getSourceListeners(sourceId);
            boolean ret = listeners.add(listener);
            //利用source mapping 去重
            if (ret) {
                Log.d(TAG, String.format("listener %s enqueued.", listener.toString()));
                //enqueue
                PriorityQueue<EventListenerHolder> holderQueue = listenerHolder.get(type);
                if (holderQueue == null) {
                    holderQueue = new PriorityQueue<>();
                    listenerHolder.put(type, holderQueue);
                }
                EventListenerHolder holder = new EventListenerHolder(priority, listener);
                holder.setSeq(genateSeq());
                holderQueue.add(holder);
                //建立反向索引
                reverseSourceMapping.put(listener,
                        new EventSourceRef(sourceId, holder, holderQueue));
            } else {
                Log.d(TAG, String.format("listener %s with source %d already enqueued.",
                        listener.toString(), sourceId));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addEventListener(@NonNull Object source, @NonNull String type, @NonNull IEventListener listener) {
        addEventListener(source, type, 0, listener);
    }

    @Override
    public void removeEventListener(@NonNull String type, @NonNull IEventListener listener) {
        Log.d(TAG, String.format("[ remove event listener. type %s listener %s]",
                type, listener.toString()));
        lock.writeLock().lock();
        try{
            if (reverseSourceMapping.containsKey(listener)) {
                EventSourceRef sourceRef = reverseSourceMapping.get(listener);
                Set<IEventListener> listeners = getSourceListeners(sourceRef.sourceId);
                listeners.remove(listener);
                reverseSourceMapping.remove(listener);
                sourceRef.holderQueue.remove(sourceRef.holder);
                sourceRef.holder.recycle();
                sourceRef.clear();
            }
        }finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public void removeEventListenersBySource(@NonNull Object source) {
        int sourceId = source.hashCode();
        Log.d(TAG, String.format("[remove event listeners by source %d ]", sourceId));
        lock.writeLock().lock();
        try {
            if (sourceMapping.containsKey(sourceId)) {
                Set<IEventListener> listeners = getSourceListeners(sourceId);
                for (IEventListener l : listeners) {
                    EventSourceRef sourceRef = reverseSourceMapping.get(l);
                    if (sourceRef != null) {
                        sourceRef.holderQueue.remove(sourceRef.holder);
                        reverseSourceMapping.remove(l);
                        sourceRef.clear();
                    }
                }
                listeners.clear();
            }
        }finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeEventListenersByType(@NonNull String type) {
        Log.d(TAG, String.format("[ remove event listener by type %s ]",
                type));
        lock.writeLock().lock();
        try{
            PriorityQueue<EventListenerHolder> holderQueue = listenerHolder.get(type);
            if (holderQueue != null) {
                for (EventListenerHolder holder : holderQueue) {
                    IEventListener listener = holder.getListener();
                    if (listener != null && reverseSourceMapping.containsKey(listener)) {
                        EventSourceRef sourceRef = reverseSourceMapping.get(listener);
                        if (sourceRef != null) {
                            sourceMapping.remove(sourceRef.sourceId);
                            reverseSourceMapping.remove(listener);
                            sourceRef.clear();
                        }
                    }
                }
                holderQueue.clear();
            }
        }finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeAllEventListeners() {
        Log.d(TAG, "[ remove all event listeners ]");
        lock.writeLock().lock();
        try{
            for (String type : listenerHolder.keySet()) {
                PriorityQueue<EventListenerHolder> holderQueue = listenerHolder.get(type);
                if (holderQueue != null) {
                    holderQueue.clear();
                }
            }
            sourceMapping.clear();
            for (EventSourceRef sourceRef : reverseSourceMapping.values()) {
                sourceRef.clear();
            }
            reverseSourceMapping.clear();
        }finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean hasEventListener(@NonNull String type) {
        Log.d(TAG, String.format("[ has event listener %s ]", type));
        lock.readLock().lock();
        try {
            PriorityQueue<EventListenerHolder> holderQueue = listenerHolder.get(type);
            return holderQueue != null && !holderQueue.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }
}
