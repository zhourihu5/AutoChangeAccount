package com.tencent.autochange.account;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umeng.socialize.utils.CommonUtil;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 账号自动切换管理类
 */
public class AccountManager {
    private Application mApplication;
    private static final String QQ_AUTH_ACTIVITY_NAME = "com.tencent.open.agent.AgentActivity";
    private static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";

    private HandlerInvokeCallback mPMSCallback = new HandlerInvokeCallback() {
        @Override
        public void beforeInvoke(Method method, Object[] args) {
            if (method.getName().equals("getPackageInfo")) {
                String packName = (String) args[0];
                if(Objects.equals(packName, QQ_PACKAGE_NAME)) {
                    args[0] = mApplication.getPackageName();
                }
            } else if (method.getName().equals("getApplicationInfo")) {
                String packName = (String) args[0];
                if(Objects.equals(packName, QQ_PACKAGE_NAME)) {
                    args[0] = mApplication.getPackageName();
                }
            } else if (method.getName().equals("queryIntentActivities")) {
                // 偷偷替换，无论怎样都能查到
                Intent intent = (Intent) args[0];
                String className = intent.getComponent().getClassName();
                if (Objects.equals(className, QQ_AUTH_ACTIVITY_NAME)) {
                    intent.setComponent(new ComponentName(mApplication.getPackageName(), H5AuthActivity.class.getName()));
                }
            }
        }

        @Override
        public Object afterInvoke(Object object) {
            return object;
        }
    };

    /**
     * 初始化入口，有可能有有可能没有
     */
    public void init(Application application) {
        this.mApplication = application;
        // 在这写代码怎么可能拿到授权时的用户名和密码？
        // 1. 欺骗没有安装时也告诉 sdk 安装了 , 拦截 pms ，怎么拦截
        PackageManager applicationPM = application.getPackageManager();
        SystemServiceIntercept.proxyServiceHook(applicationPM, "mPM", mPMSCallback);
        // 代理掉 PackageManager 中的 mPM 就可以
        application.registerActivityLifecycleCallbacks(new DefaultActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                PackageManager activityPM = activity.getPackageManager();
                SystemServiceIntercept.proxyServiceHook(activityPM, "mPM", mPMSCallback);
            }
        });

        // 完成第二部，大家自己写下 AMS
        // startActivity
    }
}
