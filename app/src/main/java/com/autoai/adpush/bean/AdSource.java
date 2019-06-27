package com.autoai.adpush.bean;

import com.autoai.adpush.listener.BaseSource;
import com.autoai.adpush.listener.Listener;
import com.autoai.adpush.listener.WeakGenericListeners;

/**
 * 广告数据源
 *
 * @author zhaozy
 * @date 2018/10/15
 */


public class AdSource extends BaseSource {
    /**
     * 获得单例
     *
     * @return
     */
    public static AdSource getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 单例持有器
     */
    private static final class InstanceHolder {
        private static final AdSource INSTANCE = new AdSource();
    }

    /**
     * 禁止构造
     */
    private AdSource() {
    }

    private AdEventInfo adEventInfo;

    public AdEventInfo getAdEventInfo() {
        return adEventInfo;
    }

    public void setAdEventInfo(AdEventInfo adEventInfo) {
        this.adEventInfo = adEventInfo;
    }

    /**
     * 监听器集合
     */
    public WeakGenericListeners<AdEventInfo> adStatusChangeListeners = new WeakGenericListeners<>();


    /**
     * 添加监听器
     */
    public void setAdStatusChangeListener(Listener.GenericListener<AdEventInfo> listener) {
        adStatusChangeListeners.add(listener);
    }

    public void clearAdStatusChangeListener(){
        adStatusChangeListeners = new WeakGenericListeners<>();
    }
    /**
     * 获取用户token,来自个人中心
     * @return
     */
    public String getToken() {
        return null;
//        return Settings.Global.getString(GlobalUtil.getContext().getContentResolver(), "token");
    }

    /**
     * 事件变化通知
     *
     * @param eventInfo
     */
    public void conveyAdChangeListener(AdEventInfo eventInfo) {
        adStatusChangeListeners.conveyEvent(eventInfo);
    }


}
