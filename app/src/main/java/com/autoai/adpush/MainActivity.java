package com.autoai.adpush;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.autoai.account.utils.Utils;
import com.autoai.adpush.bean.AdEventInfo;
import com.autoai.adpush.bean.AdSource;
import com.autoai.adpush.listener.Listener;
import com.autoai.adpush.push.MQTTPushUtils;
import com.autoai.roast.RoastDataManager;
import com.navinfo.android.pushmade.PushApis;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivit";
//    private AdDataChangeListener adDataChangeListener = new AdDataChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (TextUtils.isEmpty(Utils.getIMEI(MainActivity.this))) {
            Toast.makeText(MainActivity.this, "车机的IEMI不能为空,请刷入正确的IMEI", Toast.LENGTH_LONG).show();
            finish();
        }

        //以下是MQTT推送服务初始化逻辑
        PushApis.init(MainActivity.this, "e872830488fc4ebb8974855bd9a39b41");
        PushApis.enableDebug(true);
        //轮询查询，并启动推送服务
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "检测推送服务");
                if (!TextUtils.isEmpty(PushApis.getDeviceGUID(MainActivity.this)) && !PushApis.isPushEnabled()) {
                    PushApis.startPush();//启动推送服务
                    Log.d(TAG, "检测到推送服务未启动，执行 启动推送服务逻辑");
                }
            }
        }, 0, 500);//500ms周期轮询


        //其他相关的初始化操作
        AdUtility.getInstance().init(this);
        RoastDataManager.getInstance(this).bindVR();
        //AdSource.getInstance().setAdStatusChangeListener(adDataChangeListener);//设置广告变化监听

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDrawOverlyPermission();//检查悬浮窗 及AccessibilityService权限
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RoastDataManager.getInstance(this).unBindVR();
    }

    /**
     * 检查悬浮窗 及AccessibilityService权限,并引导开启
     */
    public void checkDrawOverlyPermission() {
        //API >=23，需要在manifest中申请权限，并在每次需要用到权限的时候检查是否已有该权限，因为用户随时可以取消掉。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(false)
                        .setCancelable(false)
                        .setTitle("权限请求").setMessage("请允许 AdPush 在其他应用的上层显示")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, 18);
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        }
    }


    /**
     * 广告变化监听器
     */
    private class AdDataChangeListener implements Listener.GenericListener<AdEventInfo> {
        @Override
        public void onEvent(AdEventInfo eventInfo) {
            switch (eventInfo.getEvent()) {
                case RESET:
//                    Log.d(TAG, "重置广告位");
//                    String filePath = eventInfo.getFilePath();
//                    String jumpLink = eventInfo.getJumpLink();
//                    if (filterRepeateAd(jumpLink)) {
//                        Log.d(TAG, "有重复的广告，不予处理");
//                        return;
//                    }
//                    // 当图片超过3张，则会将最后一张图片移除
//                    if (mHomepageAdvertisingBeanList.size() >= 3) {
//                        //添加新广告到首位
//                        mHomepageAdvertisingBeanList.add(0, eventInfo);
//                        //移除最后一位广告图
//                        mHomepageAdvertisingBeanList.remove(mHomepageAdvertisingBeanList.size() - 1);
//                    } else {
//                        //添加新广告到首位
//                        mHomepageAdvertisingBeanList.add(0, eventInfo);
//                    }
//                    if (mAdAdapter != null) {
//                        Log.d(TAG, "reset---->notifyDataSetChanged");
//                        mViewPager.setAdapter(null);
//                        mAdAdapter = new ADAdapter();
//                        mViewPager.setAdapter(mAdAdapter);
//                        mAdAdapter.notifyDataSetChanged();
//                        mViewPager.setCurrentItem(0, false);
//                    } else {
//                        mAdAdapter = new ADAdapter();
//                        Log.d(TAG, "reset---->setAdapter");
//                        mViewPager.setAdapter(mAdAdapter);
//                        onPageSelected(0);
//                    }
                    break;

                //显示广告
                case SHOW:
                    System.out.println("广告变化");
                    AdUtility.getInstance().addFloatView();
                    break;
                //隐藏广告
                case HIDE:
//                    resetAd();
                    break;
                //延时处理
                case DELAY:
                    break;
                default:
                    break;
            }
        }
    }


}
