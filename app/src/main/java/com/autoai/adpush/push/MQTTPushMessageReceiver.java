package com.autoai.adpush.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.autoai.adpush.AdUtility;
import com.autoai.adpush.PushWebActivity;
import com.autoai.adpush.bean.AdEventInfo;
import com.autoai.adpush.bean.AdSource;
import com.autoai.adpush.bean.AdStatus;
import com.autoai.roast.RoastDataManager;
import com.navinfo.android.pushmade.PushMessageReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MQTTPushMessageReceiver extends PushMessageReceiver {
    private static final String TAG = "MQTTPushMessageReceiver";
    final MQTTPushUtils pushUtils = MQTTPushUtils.getInstance();
    private String jumpLink = "";
    private String filePath = "";

    @Override
    public void onBind(Context context, int errorCode) {
        Log.d(TAG, "onBind:" + errorCode);
    }

    @Override
    public void onUnbind(Context context, int errorCode) {
        Log.d(TAG, "onUnbind>" + errorCode);
    }

    @Override
    public void onMessage(Context context, String id, String title, String message, String params) {
        Log.d(TAG, String.format("onMessage>id:%s,title:%s,message:%s,params:%s", id, title, message, params));
        RoastDataManager.getInstance(context).ttsSpeak("您有一条好物推送");

        //idea 如果广告弹框正在显示，就不进行弹框处理
        if (AdUtility.getInstance().isShowing()) {
            Log.d(TAG, "AD float window is showing, return");
            return;
        }
        //idea 推送下来了
        AdEventInfo bean = new AdEventInfo();
        try {
            JSONObject jsonObject = new JSONObject(params);
            //如果没有图片链接字段，直接返回
            if (!jsonObject.has("path")) {
                Log.d(TAG, "no AD image path,return");
                return;
            }
            if (!jsonObject.has("link")) {
                Log.d(TAG, "no AD link path,return");
                return;
            }

            String filePath = jsonObject.optString("path");
            if (!TextUtils.isEmpty(filePath)) {
                bean.setFilePath(filePath);
            }
            String jumpLink = jsonObject.optString("link");
            if (!TextUtils.isEmpty(jumpLink)) {
                bean.setJumpLink(jumpLink);
            }
            bean.setAdverType("1");
            //idea  打开广告悬浮窗
//            bean.setEvent(AdStatus.SHOW);
            AdSource.getInstance().setAdEventInfo(bean);
//            AdSource.getInstance().conveyAdChangeListener(bean);
            AdUtility.getInstance().addFloatView();
        } catch (JSONException e) {
            e.printStackTrace();
        }




//        if (isDuplicateData(id, title, message, params)) {
//            return;
//        }
//        sendNotification(context, title, message, params);
//        //idea 如果广告弹框正在显示，就不进行弹框处理
//        if (AdUtility.getInstance().isShowing()) {
//            return;
//        }
//        VRAppLayerManager.getInstance().ttsSpeak("您有一条好物推送");
//
//        //idea 推送下来了
//        AdEventInfo bean = new AdEventInfo();
//        try {
//            JSONObject jsonObject = new JSONObject(params);
//            //如果没有图片链接字段，直接返回
//            if (!jsonObject.has("path")) {
//                return;
//            }
//            String jumpLink = jsonObject.optString("link");
//            if (!TextUtils.isEmpty(jumpLink)) {
//                bean.setJumpLink(jumpLink);
//            }
//            String filePath = jsonObject.optString("path");
//            if (!TextUtils.isEmpty(filePath)) {
//                bean.setFilePath(filePath);
//            }
//            bean.setAdverType("1");
//            //idea  打开广告悬浮窗
//            bean.setEvent(AdStatus.SHOW);
//            AdSource.getInstance().setAdEventInfo(bean);
//            AdSource.getInstance().conveyAdChangeListener(bean);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    /**
     * 是否含有重复数据
     * 过滤重复数据,只处理广告推送重复数据
     *
     * @param id
     * @param title
     * @param message
     * @param params
     */
    private boolean isDuplicateData(String id, String title, String message, String params) {
        String tempMessage = pushUtils.getMessage();
        String tempTitle = pushUtils.getTitle();
        String tempPath = pushUtils.getPath();
        String tempLink = pushUtils.getLink();
        try {
            JSONObject jsonObject = new JSONObject(params);
            jumpLink = jsonObject.optString("link");
            filePath = jsonObject.optString("path");
        } catch (JSONException e) {
            jumpLink = "";
            filePath = "";
            e.printStackTrace();
        }
        //如果推送的消息相同，就不重复提醒
        if (message.equals(tempMessage) && title.equals(tempTitle) && jumpLink.equals(tempLink) && filePath.equals(tempPath)) {
            Log.d(TAG, "有重复,不予通知");
            return true;
        }
        handleCache(filePath, jumpLink, title, message);
        Log.d(TAG, "没有重复");
        return false;
    }

    Handler handler = new Handler();

    /**
     * 处理缓存的推送数据
     *
     * @param path
     * @param link
     * @param title
     * @param message
     */
    private void handleCache(String path, String link, String title, String message) {
        pushUtils.setTitle(title);
        pushUtils.setMessage(message);
        pushUtils.setLink(link);
        pushUtils.setPath(path);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //十秒钟后清空缓存数据
                pushUtils.setTitle("");
                pushUtils.setMessage("");
                pushUtils.setLink("");
                pushUtils.setPath("");
            }
        }, 10 * 1000);
    }

    @Override
    public void onNotificationArrived(Context context, String id, String title, String message, String params) {
        Log.d(TAG, String.format("onNotificationArrived>id:%s,title:%s,message:%s,params:%s", id, title, message, params));
    }

    @Override
    public void onNotificationClicked(Context context, String id, String title, String message, String params) {
        Log.d(TAG, String.format("onNotificationClicked>id:%s,title:%s,message:%s,params:%s", id, title, message, params));
        String url = "";
        try {
            JSONObject jsonObject = new JSONObject(params);
            url = jsonObject.optString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(context, PushWebActivity.class);
        intent.putExtra("data", url);
        intent.putExtra("title", "消息详情");
        context.startActivity(intent);
    }

    @Override
    public void onSetNoDisturbMode(Context context, int errorCode) {
        Log.d(TAG, "onSetNoDisturbMode>" + errorCode);
    }

    @Override
    public void onAddTag(Context context, String tag, int errorCode) {
        Log.d(TAG, "onAddTag>" + errorCode + "," + tag);
    }

    @Override
    public void onDeleteTag(Context context, String tag, int errorCode) {
        Log.d(TAG, "onDeleteTag>" + errorCode + "," + tag);
    }

    @Override
    public void onListTags(Context context, int errorCode, List<String> tags) {
        Log.d(TAG, "onListTags>" + errorCode);
    }

    private static int notify_id = 0;
    private NotificationManager nm;

    public void sendNotification(Context context, String title, String message, String params) {
        notify_id++;
        if (nm == null) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setShowWhen(true);
        builder.setAutoCancel(true);
//        builder.setDefaults(Notification.DEFAULT_SOUND);
//        builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notice_defrult));
        builder.setTicker(title);
        String url = "";
        try {
            JSONObject jsonObject = new JSONObject(params);
            url = jsonObject.optString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(context, PushWebActivity.class);
            intent.putExtra("data", url);
            intent.putExtra("title", "消息详情");
            PendingIntent pd = PendingIntent.getActivity(context, notify_id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pd);
        }
        nm.notify(notify_id, builder.build());
    }


}
