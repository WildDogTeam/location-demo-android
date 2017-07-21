package com.wilddog.testlocation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.testlocation.util.WLocationUtil;
import com.wilddog.testlocation.view.HintDialog;


public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etApp;
    private TextView tvApp;
    private Button confirm;
    boolean timeshow = true;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
//                if(timeshow){
                showIdDialog();
//                }
            } catch (Exception e) {
            }
        }
    };
    private String mAppId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        WLocationUtil.getInstance().checkNetworkState(this);
        etApp = (EditText) findViewById(R.id.et_appid);
        tvApp = (TextView) findViewById(R.id.tv_appid);
        confirm = (Button) findViewById(R.id.btn_confirm);
        tvApp.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }

    private void initdata() {

        handler.postDelayed(runnable, 7000);

        WLocationUtil.getInstance().getReferece().child("DemoConnectInfo").setValue("oko", new SyncReference.CompletionListener() {
            @Override
            public void onComplete(SyncError error, SyncReference ref) {
                Log.e("onComplete: ", error + "");
                if (error == null) {
                    handler.removeCallbacks(runnable);
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.putExtra("mAppId", mAppId);
                    startActivity(intent);
                } else
                    showIdDialog();
            }
        });
    }

    private void showIdDialog() {
        confirm.setEnabled(true);
        new HintDialog.Builder(WelcomeActivity.this).setTitle("AppID").setContents(getString(R.string.content_app)).builder();
    }

    @Override
    protected void onResume() {
        super.onResume();
        confirm.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                mAppId = etApp.getText().toString().trim();
                if (TextUtils.isEmpty(mAppId)) {
                    return;
                }
                confirm.setEnabled(false);
                WLocationUtil.getInstance().initWilddogApp(mAppId);
                initdata();
                break;
            case R.id.tv_appid:
                new HintDialog.Builder(this)
                        .setTitle("AppID")
                        .setContents(getString(R.string.app_contents_dialgo))
                        .builder();
                break;
        }
    }
}
