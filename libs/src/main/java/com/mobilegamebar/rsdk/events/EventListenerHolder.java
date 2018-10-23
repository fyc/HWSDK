package com.mobilegamebar.rsdk.events;


import com.mobilegamebar.rsdk.outer.event.IEventListener;

/**
 * Created by levyyoung on 15/5/4.
 */
public class EventListenerHolder implements Comparable<EventListenerHolder> {

    private int seq;
    public int priority = 0;
    public IEventListener listener = null;


    public EventListenerHolder(IEventListener listener) {
        this(0, listener);
    }

    public EventListenerHolder(int priority, IEventListener listener) {
        this.listener = listener;
        this.priority = priority;
    }


    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public IEventListener getListener() {
        return listener;
    }

    public void setListener(IEventListener listener) {
        this.listener = listener;
    }

    public void recycle() {
        this.priority = 0;
        this.listener = null;
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(EventListenerHolder another) {

        if (another.priority == priority) {
            return this.seq - another.seq;
        } else {

            return another.priority - this.priority;

        }

    }


    @Override
    public String toString() {
        return "EventListenerHolder{" +
                "seq=" + seq +
                ", priority=" + priority +
                ", listener=" + listener +
                '}';
    }
}
