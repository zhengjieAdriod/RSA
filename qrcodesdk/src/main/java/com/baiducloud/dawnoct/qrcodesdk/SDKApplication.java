package com.baiducloud.dawnoct.qrcodesdk;

import android.database.sqlite.SQLiteDatabase;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.litepal.LitePalApplication;
import org.litepal.tablemanager.Connector;

/**
 * Created by zj on 2016/9/24.
 */
public class SDKApplication extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);//初始化二维码
        SQLiteDatabase db = Connector.getDatabase();//初始化表
    }
}
