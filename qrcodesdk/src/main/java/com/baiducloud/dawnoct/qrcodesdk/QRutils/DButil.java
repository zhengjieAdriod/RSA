package com.baiducloud.dawnoct.qrcodesdk.QRutils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by DawnOct on 2017/9/26.
 */

public class DButil {
    /**
     * 导出数据库到sd卡
     *
     * @param context
     * @param dbName
     * @param sdDbName
     */
    public static void copyDataBaseToSD(Context context, String dbName, String sdDbName) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.e("zj", "wuSDKd");
            return;
        }
        File dbFile = new File(context.getApplicationContext().getDatabasePath(dbName) + ".db");
        File file = new File(Environment.getExternalStorageDirectory(), sdDbName + ".db");
        Log.e("zj", file.getAbsolutePath());
        FileChannel inChannel = null, outChannel = null;

        try {
            file.createNewFile();
            inChannel = new FileInputStream(dbFile).getChannel();
            outChannel = new FileOutputStream(file).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (Exception e) {
//            LogUtils.e(TAG, "copy dataBase to SD error.");
            Log.i("zj", "error");
            e.printStackTrace();
        } finally {
            try {
                if (inChannel != null) {
                    inChannel.close();
                    inChannel = null;
                }
                if (outChannel != null) {
                    outChannel.close();
                    outChannel = null;
                }
            } catch (IOException e) {
//                LogUtils.e(TAG, "file close error.");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
