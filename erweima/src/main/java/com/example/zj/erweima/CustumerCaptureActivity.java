package com.example.zj.erweima;


import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

/**
 * Created by zj on 2016/9/25.
 */
public class CustumerCaptureActivity extends AppCompatActivity {
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("result_type", 1);
            bundle.putString("result_string", result);
            resultIntent.putExtras(bundle);
            CustumerCaptureActivity.this.setResult(-1, resultIntent);
            CustumerCaptureActivity.this.finish();
        }

        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("result_type", 2);
            bundle.putString("result_string", "");
            resultIntent.putExtras(bundle);
            CustumerCaptureActivity.this.setResult(-1, resultIntent);
            CustumerCaptureActivity.this.finish();
        }
    };

    public CustumerCaptureActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_custumer);


        CaptureFragment captureFragment = new CaptureFragment();
        captureFragment.setAnalyzeCallback(this.analyzeCallback);
        this.getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
//        try{
//            Camera m_Camera = Camera.open();
//            Camera.Parameters mParameters;
//            mParameters = m_Camera.getParameters();
//            mParameters.setFlashMode("torch");
//            m_Camera.setParameters(mParameters);
//        } catch(Exception ex){}
    }

    public void quit(View view) {
        this.finish();
    }
}
