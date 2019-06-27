package com.autoai.adpush.listener;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * 基础的监听器集合(弱引用),特点:<br>
 * 1. 只会以弱引用持有监听器，避免内存泄露<br>
 * 2. 通知时发现引用不存在就会自动清理<br>
 *
 * @param <E> 具体的监听器类型,必须实现 {@link SimpleListener}
 * @author baimi
 */
public class WeakSimpleListeners<E extends Enum<?>> {
    /**
     * 是否正在迭代
     */
    private boolean iterable;

    private LinkedList<Listener.SimpleListener> cache = new LinkedList();
    /**
     * 各监听器引用集合
     */
    private WeakArrayList<Listener.SimpleListener> references = new WeakArrayList();

    /**
     * 添加监听器
     *
     * @param listener
     */
    public void add(Listener.SimpleListener<E> listener) {
        if (iterable) {
            cache.add(listener);
        } else {
            references.add(listener);
        }
    }

    /**
     * 通知事件到各监听器
     *
     * @param e
     */
    public void conveyEvent(E e) {
        iterable = true;
        for (WeakReference<Listener.SimpleListener> r : this.references.getList()) {
            Listener.SimpleListener l = r.get();
            if (l != null) {
                l.onEvent(e);
            }
        }
        iterable = false;
        for (Listener.SimpleListener listener : cache) {
            listener.onEvent(e);
            add(listener);
        }
        cache.clear();
    }

}
