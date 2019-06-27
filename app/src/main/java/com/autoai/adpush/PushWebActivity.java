package com.autoai.adpush;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Created by wumj on 2018/5/15.
 */

public class PushWebActivity extends Activity implements View.OnClickListener {

    private FrameLayout fl_wv;
    private TextView tv_webTitle;
    private LinearLayout ll_error_network;
    private ProgressBar pb_progress;
    private ImageView iv_back;
    private ImageView iv_forward;
    private ImageView iv_finish;

    private WebView web_terms;

    private final String URLKEY = "data";
    private static final String KEY_TITLE = "title";

    private String requestUrl = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.push_msg_web_activity);
        fl_wv = (FrameLayout) findViewById(R.id.fl_wv);
        tv_webTitle = (TextView)findViewById(R.id.tv_webTitle);
        ll_error_network = (LinearLayout) findViewById(R.id.ll_error_network);
        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_forward = (ImageView) findViewById(R.id.iv_forward);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        web_terms = (WebView) findViewById(R.id.wv);
        ll_error_network.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_forward.setOnClickListener(this);
        iv_finish.setOnClickListener(this);
        initData();
    }
    @SuppressWarnings("unused")
    private final class JSInterface{
        /**
         * 注意这里的@JavascriptInterface注解， target是4.2以上都需要添加这个注解，否则无法调用
         * @param text
         */
        @JavascriptInterface
        public void showToast(String text){
        }
        @JavascriptInterface
        public void showJsText(String text){
        }
    }

    public void initData() {
        WebSettings webSettings = web_terms.getSettings();
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        web_terms.addJavascriptInterface(new JSInterface(), "jsi");

        //缩放操作
        webSettings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        web_terms.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                requestUrl = url;
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                fl_wv.setVisibility(View.GONE);
                ll_error_network.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);

            }
        });

        web_terms.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                tv_webTitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                pb_progress.setProgress(newProgress);
                if (newProgress >= 100) {
                    pb_progress.setVisibility(View.GONE);
                } else {
                    pb_progress.setVisibility(View.VISIBLE);
                }
            }
        });

        if (getIntent() != null) {
            String data = getIntent().getStringExtra(URLKEY);
            String title = getIntent().getStringExtra(KEY_TITLE);
            tv_webTitle.setText(title);
            requestUrl = data;
            if (!TextUtils.isEmpty(data)) {
                web_terms.loadUrl(data);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (web_terms.canGoBack()) {
                    web_terms.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.iv_forward:
                if (web_terms.canGoForward()) {
                    web_terms.goForward();
                }
                break;
            case R.id.iv_finish:
                finish();
                break;
            case R.id.ll_error_network:
                ll_error_network.setVisibility(View.GONE);
                fl_wv.setVisibility(View.VISIBLE);
                web_terms.loadUrl(requestUrl);
                break;
        }
    }
}
