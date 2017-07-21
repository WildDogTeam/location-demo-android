package com.wilddog.testlocation;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.wilddog.client.SyncError;
import com.wilddog.location.CircleQuery;
import com.wilddog.location.CircleQueryListener;
import com.wilddog.location.Position;
import com.wilddog.location.WilddogLocation;
import com.wilddog.testlocation.util.WLocationUtil;
import com.wilddog.testlocation.view.CircleAdapter;
import com.wilddog.testlocation.view.DrawableCenterButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CircleQueryActivy extends AppCompatActivity implements View.OnClickListener, LocationSource, AMapLocationListener {
    private static final String TAG = "CircleQueryActivy";
    public static final LatLng BEIJING = new LatLng(39.90403, 116.407525);// 北京市经纬度
    private AMap aMap;
    private MapView mapView;
    private Circle circle;
    private WilddogLocation location;
    private CircleQuery circleQuery;
    private Button start;
    private Button stop;
    List<String> keys = new ArrayList<String>();
    DrawableCenterButton centerButton;
    private CircleAdapter adapter;
    private ListView lvDevice;
    private LinearLayout llClose;
    Map<String, Marker> optionsMap = new HashMap<>();
    private LinearLayout back;
    private TextView rTitle;
    private TextView cTitle;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private Marker marker;
    private LinearLayout llCircle;
    private View empty;
    private ImageView ivlocation;
    private LinearLayout llDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circlequery_activy);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        location = new WilddogLocation(WLocationUtil.getInstance().getReferece());
        start = (Button) findViewById(R.id.btn_start);
        stop = (Button) findViewById(R.id.btn_stop);
        centerButton = (DrawableCenterButton) findViewById(R.id.btn_device);
        lvDevice = (ListView) findViewById(R.id.lv_device);
        llClose = (LinearLayout) findViewById(R.id.ll_close);
        llDevice = (LinearLayout) findViewById(R.id.ll_device);
        back = (LinearLayout) findViewById(R.id.btn_title_back);
        rTitle = (TextView) findViewById(R.id.tv_hint_title);
        cTitle = (TextView) findViewById(R.id.iv_title_name);
        llCircle = (LinearLayout) findViewById(R.id.ll_circle);

        ivlocation = (ImageView) findViewById(R.id.iv_location);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        centerButton.setOnClickListener(this);
        back.setOnClickListener(this);
        rTitle.setOnClickListener(this);
        ivlocation.setOnClickListener(this);
        cTitle.setText(R.string.circle_title);

        adapter = new CircleAdapter(this, android.R.layout.simple_list_item_1, keys);
        lvDevice.setAdapter(adapter);
        lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.changeItemColor(position);
                resetAllMarkers();
                String key = (String) parent.getItemAtPosition(position);
                optionsMap.get(key).setIcon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.green_dot));
            }
        });

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }

        if (WLocationUtil.getInstance().isCircleStartState(WLocationUtil.CICLE)) {
            circleQuery = WLocationUtil.getInstance().getCircle(WLocationUtil.CICLE);
            startCircleQuery();
        } else {
            llClose.setVisibility(View.GONE);
            start.setVisibility(View.VISIBLE);
        }
    }

    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
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
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                startCircleQuery();
                break;
            case R.id.btn_device:
                if (centerButton.isSelected()) {
                    if (empty != null)
                        llCircle.removeView(empty);
                    centerButton.setSelected(false);
                    llDevice.setVisibility(View.GONE);
                    resetAllMarkers();
                } else {
                    centerButton.setSelected(true);
                    if (keys.isEmpty()) {
                        empty = View.inflate(this, R.layout.layout_empty, null);
                        llCircle.addView(empty);
                        return;
                    }
                    llDevice.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_stop:
//                circleQuery.removeAllCircleQueryListeners();
                keys.clear();
                llClose.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
                llDevice.setVisibility(View.GONE);
                centerButton.setSelected(false);
                cancleCircleListener();
                resetAMap();
                break;
            case R.id.btn_title_back:
                finish();
                break;
            case R.id.tv_hint_title:
                startActivity(new Intent(this, InfoActivoty.class));
                break;
            case R.id.iv_location:
                AMapLocation lastKnownLocation = mlocationClient.getLastKnownLocation();
                LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(latLng, 15.25f));// 设置指定的可视区域地图
                break;
        }
    }

    private void startCircleQuery() {
        circleQuery.addCircleQueryEventListener(new CircleQueryListener() {
            @Override
            public void onKeyEntered(String key, Position location) {
                Log.e(TAG, "onKeyEntered: " + location.getTimestamp());
//                if (System.currentTimeMillis() - location.getTimestamp() < 30 * 1000) {
                    MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                            .fromResource(R.mipmap.blu_dot))
                            .anchor(0.5f, 0.5f)
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .draggable(false);
                    Marker marker = aMap.addMarker(markerOption);
                    optionsMap.put(key, marker);
                    keys.add(key);
                    adapter.notifyDataSetChanged();
//                }
            }

            @Override
            public void onKeyExited(String key) {
                Log.e(TAG, "onKeyExited: " + key);
                if (keys.contains(key)) {
                    optionsMap.get(key).destroy();
                    keys.remove(key);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onKeyMoved(String key, Position location) {
                Log.e(TAG, "onKeyMoved: " + key);
                optionsMap.get(key).setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }

            @Override
            public void onCircleQueryReady() {

            }

            @Override
            public void onCircleQueryError(SyncError error) {

            }
        });
        llClose.setVisibility(View.VISIBLE);
        start.setVisibility(View.GONE);
        WLocationUtil.getInstance().setCircleState(circleQuery, WLocationUtil.CICLE);
    }

    private void resetAMap() {
        aMap.clear();
        AMapLocation myLocation = mlocationClient.getLastKnownLocation();
        drawNewCircle(myLocation);
    }

    private void cancleCircleListener() {
        WLocationUtil.getInstance().getCircle(WLocationUtil.CICLE).removeAllCircleQueryListeners();
        WLocationUtil.getInstance().removeCircleState(WLocationUtil.CICLE);
    }

    private void resetAllMarkers() {
        for (Marker m :
                optionsMap.values()) {
            m.setIcon(BitmapDescriptorFactory
                    .fromResource(R.mipmap.blu_dot));
        }
    }

    @Override
    public void onLocationChanged(AMapLocation myLocation) {

        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        if (marker == null) {
            drawNewCircle(myLocation);
            aMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(latLng, 15.25f));// 设置指定的可视区域地图
            circleQuery = location.getCircleQuery(new Position(myLocation.getLatitude(), myLocation.getLongitude()), 1);
        } else {
            marker.setPosition(latLng);
            circle.setCenter(latLng);
            circleQuery.setCenter(new Position(latLng.latitude, latLng.longitude));
        }
    }

    private void drawNewCircle(AMapLocation myLocation) {
        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        marker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.red_dot))
                .position(latLng)
                .anchor(0.5f, 0.5f)
                .draggable(false));
        // 绘制一个圆形
        circle = aMap.addCircle(new CircleOptions().center(latLng)
                .radius(1000).strokeColor(Color.parseColor("#00a0e9"))
                .fillColor(Color.parseColor("#2000a0e9")).strokeWidth(2));
    }
}
