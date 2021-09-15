package com.tencent.autochange.account;

import android.app.Application;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 第三方登录和分享
        UMConfigure.init(this, "596837c98630f5189a00055e",
                "UMENG_APPKEY", UMConfigure.DEVICE_TYPE_PHONE, "UM Share");

        PlatformConfig.setWeixin("wxa26d7077695dc09f", "6ab9bd7df1be1dbdb94f1161fd79f8ad");
        PlatformConfig.setQQZone("101445328", "852e7a6ad71be4eb04fca68d0580412e");
        UMShareAPI.get(this);

        // sdk 入口，这个代码是可以在流水线自动配置的
        AccountManager accountManager = new AccountManager();
        accountManager.init(this);
    }
}
