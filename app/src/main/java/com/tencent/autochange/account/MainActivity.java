package com.tencent.autochange.account;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.umeng.qq.tencent.IUiListener;
import com.umeng.qq.tencent.Tencent;
import com.umeng.qq.tencent.UiError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements IUiListener {
    private Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTencent = Tencent.createInstance("100424468", this.getApplicationContext());
    }

    public void qqLogin(View view) {
        if (!isInstallQQ()) {
            Toast.makeText(this, Toast.LENGTH_LONG, Toast.LENGTH_SHORT).show();
            return;
        }

        mTencent.login(this, "all", this);
    }

    private boolean isInstallQQ() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo("com.tencent.mobileqq", 0);
            return packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onComplete(Object o) {
        Log.e("TAG", "o->" + o);
    }

    @Override
    public void onError(UiError uiError) {
        Log.e("TAG", "o->" + uiError.errorMessage);
    }

    @Override
    public void onCancel() {
        Log.e("TAG", "o->,---->");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, requestCode, data, this);
    }
}
