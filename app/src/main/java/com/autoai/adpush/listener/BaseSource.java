package com.autoai.adpush.listener;

/**
 * 数据源基础类型
 * <p>
 * Created by lixingmao on 2018/5/23.
 */
public abstract class BaseSource {
    private WeakSimpleListeners<Enum<?>> listeners = new WeakSimpleListeners<>();

    /**
     * 添加数据类型监听器
     */
    public void addListener(Listener.SimpleListener<Enum<?>> listener) {
        listeners.add(listener);
    }

    /**
     * 通知数据的变化, 对应的类型由子类统一定制
     *
     * @param events
     */
    public void conveyEvent(Enum<?>... events) {
        if (events != null) {
            for (Enum event : events) {
                listeners.conveyEvent(event);
            }
        }
    }
}
