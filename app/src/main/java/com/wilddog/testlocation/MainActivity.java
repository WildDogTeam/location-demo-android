package com.wilddog.testlocation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wilddog.client.DataSnapshot;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;
import com.wilddog.client.ValueEventListener;
import com.wilddog.client.WilddogSync;
import com.wilddog.testlocation.util.WLocationUtil;
import com.wilddog.testlocation.view.HintDialog;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout location;
    private LinearLayout geoquery;
    private LinearLayout pathquery;
    private TextView appid;
    private String mAppId;
    private TextView tvSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppId = getIntent().getStringExtra("mAppId");

        initView();
    }



    private void initView() {
        location = (LinearLayout) findViewById(R.id.location);
        geoquery = (LinearLayout) findViewById(R.id.geoquery);
        pathquery = (LinearLayout) findViewById(R.id.pathquery);
        appid = (TextView) findViewById(R.id.tv_appid);
        tvSwitch = (TextView) findViewById(R.id.tv_switch);

        location.setOnClickListener(this);
        geoquery.setOnClickListener(this);
        pathquery.setOnClickListener(this);
        tvSwitch.setOnClickListener(this);
        findViewById(R.id.ll_fence).setOnClickListener(this);

        appid.setText(mAppId);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.location:
                startActivity(new Intent(this,LocationActivy.class));
                break;
            case R.id.geoquery:
                startActivity(new Intent(this,CircleQueryActivy.class));
                break;
            case R.id.pathquery:
                startActivity(new Intent(this,PathQueryActivity.class));
                break;
            case R.id.ll_fence:
                new HintDialog.Builder(this).setTitle(getString(R.string.coming_soon)).setContents(getString(R.string.content_dev)).builder();
                break;
            case R.id.tv_switch:
                WLocationUtil.getInstance().reset();
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        WLocationUtil.getInstance().reset();
        super.onBackPressed();
    }
}
