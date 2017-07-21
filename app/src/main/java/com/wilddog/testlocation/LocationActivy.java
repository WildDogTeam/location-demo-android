package com.wilddog.testlocation;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.wilddog.client.SyncError;
import com.wilddog.location.Position;
import com.wilddog.location.WilddogLocation;
import com.wilddog.testlocation.util.WLocationUtil;
import com.wilddog.testlocation.view.HintDialog;

import java.text.SimpleDateFormat;

/**
 * AMapV1��ͼ�м򵥽�����ʾ��λС����
 */
public class LocationActivy extends AppCompatActivity implements
        View.OnClickListener, LocationSource, AMapLocationListener {
    private static final String TAG = LocationActivy.class.getSimpleName();
    private AMap aMap;
    private MapView mapView;
    private AMapLocationClient mlocationClient;
    private TextView time;
    private Button start;
    private Button stop;
    private WilddogLocation location;
    private String key = "47D63607-CA1D-4E96-8E75-CCA0D434B402";
    private LinearLayout back;
    private TextView rTitle;
    private TextView cTitle;
    private AMapLocationClientOption mLocationOption;
    private Marker marker=null;
    private ImageView ivlocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_activy);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// �˷���������д
        init();
        initView();
    }

    private void initView() {
        time = (TextView) findViewById(R.id.tv_time);
        start = (Button) findViewById(R.id.btn_start);
        stop = (Button) findViewById(R.id.btn_stop);
        back = (LinearLayout) findViewById(R.id.btn_title_back);
        rTitle = (TextView) findViewById(R.id.tv_hint_title);
        cTitle = (TextView) findViewById(R.id.iv_title_name);
        ivlocation = (ImageView) findViewById(R.id.iv_location);
        cTitle.setText(R.string.location_title);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        back.setOnClickListener(this);
        rTitle.setOnClickListener(this);
        ivlocation.setOnClickListener(this);

        if(WLocationUtil.getInstance().isStartState(WLocationUtil.LOCATION)){
            start.setVisibility(View.GONE);
            stop.setVisibility(View.VISIBLE);
            time.setVisibility(View.VISIBLE);
        }else {
            start.setVisibility(View.VISIBLE);
            stop.setVisibility(View.GONE);
            time.setVisibility(View.GONE);
        }
    }

    private void showWindow() {
        new HintDialog.Builder(this).setTitle(getString(R.string.hint_title2))
                .setContents(getString(R.string.dialog_content_location)).builder();
    }

    /**
     * ��ʼ��AMap����
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        location = new WilddogLocation(WLocationUtil.getInstance().getReferece());
        location.addPositionListener(key, new WilddogLocation.PositionListener() {
            @Override
            public void onDataChange(String key, Position position) {
                Log.e(TAG, "onDataChange: " + position);
                if(position!=null) {
                    SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");
                    String dateStr = dateformat.format(position.getTimestamp());
                    time.setText(getString(R.string.near_up) + dateStr);
                }
            }

            @Override
            public void onCancelled(SyncError syncError) {

            }
        });
    }

    /**
     * ����һЩamap������
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// ���ö�λ����
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setScaleControlsEnabled(true);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// ����Ĭ�϶�λ��ť�Ƿ���ʾ
        aMap.setMyLocationEnabled(true);
    }

    /**
     * ����������д
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * ����������д
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * ����������д
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * ����������д
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        deactivate();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    /**
     * ֹͣ��λ
     */
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
            mlocationClient = null;
        }
        location.removeAllPositionListeners();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                location.startTracingPosition(key);

                start.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);

                showWindow();
                WLocationUtil.getInstance().setState(location,WLocationUtil.LOCATION);
                break;
            case R.id.btn_stop:
                start.setVisibility(View.VISIBLE);
                stop.setVisibility(View.GONE);
                time.setVisibility(View.VISIBLE);
                WLocationUtil.getInstance().getLocation(WLocationUtil.LOCATION).stopTracingPosition(key);
                WLocationUtil.getInstance().removeState(WLocationUtil.LOCATION);
                break;
            case R.id.btn_title_back:
                finish();
                break;
            case R.id.tv_hint_title:
                startActivity(new Intent(this,InfoActivoty.class));
                break;
            case R.id.iv_location:
                AMapLocation lastKnownLocation;
                lastKnownLocation = mlocationClient.getLastKnownLocation();
                LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(latLng, 14.25f));// ����ָ���Ŀ��������ͼ
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation myLocation) {
        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        if(marker==null) {
            marker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                    .fromResource(R.mipmap.red_dot))
                    .position(latLng)
                    .draggable(true));
            aMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(latLng, 14.25f));// ����ָ���Ŀ��������ͼ
        }else {
            marker.setPosition(latLng);
        }

    }
}
