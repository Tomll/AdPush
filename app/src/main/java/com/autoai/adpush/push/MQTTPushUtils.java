package com.autoai.adpush.push;

import android.content.Context;
import android.util.Log;

import com.navinfo.android.pushmade.PushApis;


public class MQTTPushUtils {

    private static final String TAG = "MQTTPushMessage";

    private static MQTTPushUtils instance = new MQTTPushUtils();

    public static MQTTPushUtils getInstance() {
        return instance;
    }

    private String message = "";
    private String title = "";
    private String path = "";
    private String link = "";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

//    public void init(Context context) {
//        PushApis.init(context, "e872830488fc4ebb8974855bd9a39b41");
//        PushApis.enableDebug(true);
//        PushApis.startPush();
//        Log.d(TAG, "启动推送服务 ： e872830488fc4ebb8974855bd9a39b41");
//    }
}
