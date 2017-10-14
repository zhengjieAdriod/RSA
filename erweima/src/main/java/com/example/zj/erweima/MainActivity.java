package com.example.zj.erweima;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

//import com.baiducloud.dawnoct.qrcodesdk.QRcodeActivity;
import com.baiducloud.dawnoct.qrcodesdk.QRutils.Base64Decoder;
import com.baiducloud.dawnoct.qrcodesdk.QRutils.QRutils;
import com.baiducloud.dawnoct.qrcodesdk.QRutils.RSAUtil;
import com.example.zj.erweima.bean.News;
import com.google.gson.Gson;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 001;
    private static final int REQUEST_IMAGE = 002;
    ImageView mImageView;
    EditText editText;
    Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.imageView);
        editText = (EditText) findViewById(R.id.edt);
//        SQLiteDatabase db = Connector.getDatabase();
    }


    //扫描二维码
    public void erWeiMa01(View v) {

        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    //扫描二维码(自定义扫描界面)
    public void erWeiMa03(View v) {
        Intent intent = new Intent(MainActivity.this, CustumerCaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    //解析二维码图片
    public void erWeiMa02(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);//调用系统API打开图库
    }

    //生成二维码图片(带logo)
    public void erWeiMa04(View view) {
        String textContent = editText.getText().toString();
        if (TextUtils.isEmpty(textContent)) {
            Toast.makeText(this, "您的输入为空!", Toast.LENGTH_SHORT).show();

            return;
        }
        editText.setText("");
        //带logo
        mBitmap = CodeUtils.createImage(textContent, 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        mImageView.setImageBitmap(mBitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
        /**
         * 解析二维码图片
         */
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();

                ContentResolver cr = getContentResolver();
                try {
                    Bitmap mBitmap = MediaStore.Images.Media.getBitmap(cr, uri);//先得到bitmap图片

                    String picturePath = uriToBitmapPath(uri);//根据图片uri得到图片路径

                    CodeUtils.analyzeBitmap(picturePath, new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            Toast.makeText(MainActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                            Log.i("zj", result);
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                        }
                    });

                    if (mBitmap != null) {
                        mBitmap.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //通过uri获得图片的路径
    private String uriToBitmapPath(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor == null) {
            return "";
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

        String picturePath = cursor.getString(columnIndex);
        return picturePath;

    }

    //调用自定义的sdk
    public void applySDK(View view) {
//        Intent intent = new Intent(MainActivity.this, QRcodeActivity.class);
//        startActivity(intent);
    }

    //测试加密解密
    public void testRSA(View view) throws Exception {
        List<News> personList = new ArrayList<>();
        int testMaxCount = 2;//测试的最大数据条数
        //添加测试数据
        for (int i = 0; i < testMaxCount; i++) {
            News person = new News();
            person.setCommentCount(i);
            person.setContent(String.valueOf(i));
            personList.add(person);
        }
        Gson gson = new Gson();
        String jsonData = gson.toJson(personList);

        Log.e("MainActivity", "加密前json数据 ---->" + jsonData);
        Log.e("MainActivity", "加密前json数据长度 ---->" + jsonData.length());
        KeyPair keyPair = RSAUtil.generateRSAKeyPair(RSAUtil.DEFAULT_KEY_SIZE);
        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String s = publicKey.toString();
        Log.e("MainActivity", "公钥串: " + s + "______");
        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //公钥加密
        long start = System.currentTimeMillis();
        byte[] encryptBytes = RSAUtil.encryptByPublicKeyForSpilt(jsonData.getBytes(), publicKey.getEncoded());
        long end = System.currentTimeMillis();
        Log.e("MainActivity", "公钥加密耗时 cost time---->" + (end - start));
        String encryStr = Base64Decoder.encode(encryptBytes);
        Log.e("MainActivity", "加密后json数据 --1-->" + encryStr);
        Log.e("MainActivity", "加密后json数据长度 --1-->" + encryStr.length());
        //私钥解密
        start = System.currentTimeMillis();
        byte[] decryptBytes = RSAUtil.decryptByPrivateKeyForSpilt(Base64Decoder.decode(encryStr), privateKey.getEncoded());
        String decryStr = new String(decryptBytes);
        end = System.currentTimeMillis();
        Log.e("MainActivity", "私钥解密耗时 cost time---->" + (end - start));
        Log.e("MainActivity", "解密后json数据 --1-->" + decryStr);

        //私钥加密
        start = System.currentTimeMillis();
        encryptBytes = RSAUtil.encryptByPrivateKeyForSpilt(jsonData.getBytes(), privateKey.getEncoded());
        end = System.currentTimeMillis();
        Log.e("MainActivity", "私钥加密密耗时 cost time---->" + (end - start));
        encryStr = Base64Decoder.encode(encryptBytes);
        Log.e("MainActivity", "加密后json数据 --2-->" + encryStr);
        Log.e("MainActivity", "加密后json数据长度 --2-->" + encryStr.length());
        //公钥解密
        start = System.currentTimeMillis();
        decryptBytes = RSAUtil.decryptByPublicKeyForSpilt(Base64Decoder.decode(encryStr), publicKey.getEncoded());
        decryStr = new String(decryptBytes);
        end = System.currentTimeMillis();
        Log.e("MainActivity", "公钥解密耗时 cost time---->" + (end - start));
        Log.e("MainActivity", "解密后json数据 --2-->" + decryStr);
    }

    //生成密钥对
    public void copyDB(View view) throws Exception {
        QRutils.generateKey(new QRutils.GenerateKeyCallBack() {
            @Override
            public void onSaveResult(boolean success, String key) {
                Log.e("MainActivity", "生成新的密钥对保存成功 ::" + key);
            }
        });

    }

    //写入签名
    public void writeSignature(View view) {
        QRutils.writeUserCert("我是app请求来的签名", new QRutils.WriteUsertCertCallBack() {
            @Override
            public void onResult(boolean success) {
                Log.e("MainActivity", "写入签名成功");
            }
        });
    }

    //查找签名
    public void findSignature(View view) {
        QRutils.findSignatrueAsyn(new QRutils.FindUsertCertCallBack() {
            @Override
            public void onSuccess(int validTime) {
                Log.e("MainActivity", "查询签名的剩余有效时长为:" + validTime);
            }
            @Override
            public void onFail(int f) {
                Log.e("MainActivity", f + "");
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
        //niasdfnieniasdfiad
        //nienilinnihao nsldfnoiezehjishi ai shuoyeshuobuqingchu zhejiushiai shuoyeshuobuqingchu dangguangbuweiminzhaoxiang buruhuijiamaihongshu
    }
}
