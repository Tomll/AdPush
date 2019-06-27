package com.autoai.adpush;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.autoai.adpush.bean.AdEventInfo;
import com.autoai.adpush.bean.AdSource;
import com.autoai.adpush.bean.AdStatus;
import com.autoai.adpush.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//import com.android.launcher3.widgets.WidgetsManager;


public class AdUtility {

    private static String TAG = "AdUtility";
    private Context mContext;

    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
    private WindowManager mWindowManager;
    private View contentView;

    private static AdUtility sInstance = null;

    public static AdUtility getInstance() {
        if (sInstance == null) {
            sInstance = new AdUtility();
        }
        return sInstance;
    }


    public void init(Context context) {
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    ImageView imgAd;
    View imgClose;

    public void addFloatView() {
        contentView = View.inflate(mContext, R.layout.ad_floating, null);
        contentView.setVisibility(View.GONE);

        imgAd = contentView.findViewById(R.id.image_ad);
        imgClose = contentView.findViewById(R.id.image_close);

        AdEventInfo eventInfo = AdSource.getInstance().getAdEventInfo();
        if (eventInfo != null) {
            Log.d(TAG, "eventInfo.getFilePath() --------"+eventInfo.getFilePath());
            //Glide.with(mContext).load(eventInfo.getFilePath()).asBitmap().into(target);
            getBitmapFromNet(imgAd,eventInfo.getFilePath());//加载图片到imgAd
        }

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFloatView();
                Log.d(TAG, "onClick imgClose --------dongrp");
            }
        });
        imgAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAd();
                Log.d(TAG, "onClick imgAd --------dongrp");
            }
        });

        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //wmParams.x = mWindowManager.getDefaultDisplay().getWidth() / 4;
        //wmParams.y = mWindowManager.getDefaultDisplay().getHeight()/4;
        wmParams.format = PixelFormat.TRANSPARENT;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.dimAmount = 0.5f;
        //wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.gravity = Gravity.CENTER;
        wmParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        mWindowManager.addView(contentView, wmParams);

    }

/*    private SimpleTarget target = new SimpleTarget<Bitmap>(355, 400) {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            imgAd.setImageBitmap(bitmap);
            contentView.setVisibility(View.VISIBLE);
            Log.d(TAG, "onResourceReady --------dongrp");
//            if (WidgetsManager.getInstance().getActivity() != null) {
                imgAd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //悬浮窗显示成功后5秒后再关闭
                        removeFloatView();
                    }
                }, 5000);
            }
//        }
    };*/

    public boolean isShowing() {
        if (contentView != null) {
            return contentView.getVisibility() == View.VISIBLE;
        }
        return false;
    }


    public void removeFloatView() {
//        if( contentView == null || !contentView.isAttachedToWindow()) return;
//        mWindowManager.removeView(contentView);
//        hideWindow();
        if (contentView == null) return;
        contentView.setVisibility(View.GONE);
    }

/*    private void hideWindow() {
        AdEventInfo eventInfo = new AdEventInfo();
        eventInfo.setEvent(AdStatus.HIDE);
        AdSource.getInstance().conveyAdChangeListener(eventInfo);
    }*/


    /**
     * 点击广告
     */
    private void clickAd() {
        AdEventInfo bean = AdSource.getInstance().getAdEventInfo();
        if (bean == null) {
            return;
        }
        if (TextUtils.isEmpty(bean.getJumpLink())) {
            return;
        }
        removeFloatView();
//        Context context = WidgetsManager.getInstance().getActivity();
        String jumpLink = bean.getJumpLink();
        StringBuilder sb = new StringBuilder();
        sb.append(jumpLink);
        String token = AdSource.getInstance().getToken();
        if (!TextUtils.isEmpty(token)) {
            //token里如果含"+" ,需要特殊处理一下
            token = token.replace("+", "%2B");
            sb.append("?token=").append(token);
        }
        Navigation.getInstance().startWebView(mContext, sb.toString());


    }



    //**************网络下载图片，并将图片展示到imageview上
    public void getBitmapFromNet(ImageView imageView, String url) {
        new DownImageAsyncTask().execute(imageView, url);//开启异步下载
    }

    //网络下载异步任务
    private class DownImageAsyncTask extends AsyncTask<Object, Void, Bitmap> {
        private ImageView imageView;
        private String url;

        @Override
        protected Bitmap doInBackground(Object... objects) {//可变参数，数组
            imageView = (ImageView) objects[0];
            url = (String) objects[1];
            Bitmap bitmap = downloadBitmap(url);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
            contentView.setVisibility(View.VISIBLE);
            contentView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //悬浮窗显示成功后5秒后再关闭
                    removeFloatView();
                }
            }, 5000);

//            //将下载好的图片缓存起来
//            if (null != bitmap) {
//                addBitmapToMemory(url, bitmap);
//                addBitmapToLocal(url, bitmap);
//            }
        }
    }

    //同步下载方法（必须放在子线程）
    private static Bitmap downloadBitmap(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.d("ThreeLevelCacheBitmapUt", "responseCode:" + responseCode);
            if (responseCode == 200) {
                InputStream in = conn.getInputStream();
                //图片压缩处理
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 2;//宽高压缩为原来的1/2，
                option.inPreferredConfig = Bitmap.Config.RGB_565;//设置图片的格式
                Bitmap bitmap = BitmapFactory.decodeStream(in, null, option);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return null;
    }


}
