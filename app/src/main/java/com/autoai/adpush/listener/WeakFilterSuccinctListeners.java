package com.autoai.adpush.listener;


import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * 基础的监听器集合（弱引用）, 特点：<br>
 * 1、只会以弱引用持有监听器，避免内存泄露<br>
 * 2、通知时发现引用不存在就会自动清理<br>
 *
 * @author baimi
 */
public class WeakFilterSuccinctListeners {

    /**
     * 是否正在迭代
     */
    private boolean iterable;

    private LinkedList<Listener.SuccinctListener> cache = new LinkedList();
    /**
     * 各监听器引用集合
     */
    private WeakArrayList<Listener.SuccinctListener> references = new WeakArrayList<>();

    /**
     * 添加监听器
     *
     * @param listener
     */
    public synchronized void add(Listener.SuccinctListener listener) {
        if (iterable) {
            cache.add(listener);
        } else {
            references.add(listener);
        }
    }

    /**
     * 通知事件到各个监听器
     */
    public synchronized void conveyEvent(EventFilter filter) {
        iterable = true;
        for (WeakReference<Listener.SuccinctListener> r : this.references.getList()) {
            Listener.SuccinctListener l = r.get();
            if (l != null && filter.onFilter(l)) {
                l.onEvent();
            }
        }
        iterable = false;
        for (Listener.SuccinctListener listener : cache) {
            listener.onEvent();
            add(listener);
        }
        cache.clear();
    }

    public interface EventFilter<T> {
        boolean onFilter(T listener);
    }
}
