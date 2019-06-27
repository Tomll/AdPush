package com.autoai.adpush.listener;


import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

class WeakArrayList<V> {

    /**
     * 存储GC回收对象,Java虚拟机将回收对象存入到改集合中。
     */
    ReferenceQueue<V> referenceQueue;

    LinkedList<WeakReference> elementData;

    WeakArrayList() {
        elementData = new LinkedList<>();
        referenceQueue = new ReferenceQueue<>();
    }

    boolean add(V v) {
        poll();
        if (null != v) {
            for (WeakReference item : elementData) {
                if (item.get() == v) {
                    return false;
                }
            }
            return elementData.add(new WeakReference(v, referenceQueue));
        }
        return false;
    }

    LinkedList<WeakReference> getList() {
        poll();
        return elementData;
    }

    /**
     * 清除被GC回收的对象
     */
    private void poll() {
        Object object;
        while ((object = referenceQueue.poll()) != null) {
            elementData.remove(object);
        }
    }
}
