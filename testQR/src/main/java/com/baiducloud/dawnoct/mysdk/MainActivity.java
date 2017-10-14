package com.baiducloud.dawnoct.mysdk;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baiducloud.dawnoct.qrcodesdk.QRutils.QRutils;

public class MainActivity extends Activity {

    ImageView mImageView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.imageView);
        editText = (EditText) findViewById(R.id.edt);
    }

    //生成密钥对,
    public void generateKey(final View view) throws Exception {
        QRutils.generateKey(new QRutils.GenerateKeyCallBack() {
            @Override
            public void onSaveResult(boolean success, String key) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "密钥对生成成功!", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("MainActivity", "生成新的密钥对保存成功 ::" + key);
            }
        });
    }

    //写入签名
    public void writeSignature(View view) {
        QRutils.writeUserCert("我是app请求来的签名", new QRutils.WriteUsertCertCallBack() {
            @Override
            public void onResult(final boolean success) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(MainActivity.this, "成功写入签名!", Toast.LENGTH_SHORT).show();
                            Log.e("MainActivity", "写入签名成功");
                        } else {
                            Toast.makeText(MainActivity.this, "写入签名失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //查找签名
    public void findSignature(View view) {
        QRutils.findSignatrueAsyn(new QRutils.FindUsertCertCallBack() {
            @Override
            public void onSuccess(final int validTime) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "签名的有效时长为:" + validTime + "分钟", Toast.LENGTH_SHORT).show();
                        Log.e("MainActivity", "查询签名的剩余有效时长为:" + validTime + "分钟");
                    }
                });

            }

            @Override
            public void onFail(final int f) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("MainActivity", f + "");
                        Toast.makeText(MainActivity.this, "查找签名失败:", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    //生成乘车二维码
    public void generateQR(View view) throws Exception {
        Bitmap bitmap = QRutils.generateQR("乘客");
        if (bitmap == null) {
            Toast.makeText(this, "位图为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mImageView.setImageBitmap(bitmap);
//        String textContent = "";
//        if (TextUtils.isEmpty(textContent)) {
//            Toast.makeText(this, "您的输入为空!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        //带logo
//        mBitmap = CodeUtils.createImage(textContent, 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//        mImageView.setImageBitmap(mBitmap);
    }
}
