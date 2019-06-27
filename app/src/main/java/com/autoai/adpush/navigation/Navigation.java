package com.autoai.adpush.navigation;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * Created by zhangyf on 2018/5/31.
 */

public class Navigation {


    public static Navigation getInstance() {
        return Navigation.InstanceHolder.INSTANCE;
    }

    /**
     * 单例持有器
     */
    private static final class InstanceHolder {
        private static final Navigation INSTANCE = new Navigation();
    }

    /**
     * 禁止构造
     */
    private Navigation() {
    }


    public void startWebView(Context context, String url) {

        if (TextUtils.isEmpty(url)) {
//            ToastUtil.showToast("链接地址不能为空");
            return;
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("com.mapbar.launcher.pushwebview");
        intent.putExtra("data", url);
        intent.putExtra("title", "产品详情");
        context.startActivity(intent);
    }


}
