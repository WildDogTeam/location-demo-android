package com.wilddog.testlocation;

import android.content.Intent;
import android.graphics.Color;
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
import com.amap.api.maps2d.model.PolylineOptions;
import com.wilddog.client.SyncError;
import com.wilddog.location.PathQuery;
import com.wilddog.location.PathQueryListener;
import com.wilddog.location.PathSnapshot;
import com.wilddog.location.Position;
import com.wilddog.location.WilddogLocation;
import com.wilddog.testlocation.util.WLocationUtil;
import com.wilddog.testlocation.view.HintDialog;

import java.text.SimpleDateFormat;
import java.util.Date;


public class PathQueryActivity extends AppCompatActivity implements View.OnClickListener, LocationSource, AMapLocationListener {
    private static final String TAG = LocationActivy.class.getSimpleName();
    private AMap aMap;
    private MapView mapView;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private TextView time;
    private Button start;
    private Button stop;
    private WilddogLocation location;
    private String key = "47D63607-CA1D-4E96-8E75-CCA0D434B402";
    private PathQuery pathQuery;
    private LinearLayout back;
    private TextView rTitle;
    private TextView cTitle;
    private Position prePosition;
    private Marker marker;
    private ImageView ivlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_query);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
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
        cTitle.setText(R.string.path_title);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        back.setOnClickListener(this);
        rTitle.setOnClickListener(this);
        ivlocation.setOnClickListener(this);

        if(WLocationUtil.getInstance().isStartState(WLocationUtil.PATH)){
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
        new HintDialog.Builder(this)
                .setTitle(getString(R.string.path_dialog_title))
                .setContents(getString(R.string.path_contents_dialog))
                .builder();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        location = new WilddogLocation(WLocationUtil.getInstance().getReferece());
        pathQuery = location.getPathQueryWithStartTime(key,new Date());
        pathQuery.addPathQueryListener(new PathQueryListener() {
            @Override
            public void onLocationResult(PathSnapshot pathSnapshot) {
                Log.e(TAG, "points: "+pathSnapshot.getPoints() );
                SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");
                String dateStr = dateformat.format(pathSnapshot.getLatestPoint().getTimestamp());
                time.setText(getString(R.string.near_up)+dateStr);

                drawPath(pathSnapshot.getLatestPoint());
            }

            @Override
            public void onCancelled(SyncError error) {

            }
        });
    }

    private void drawPath(Position latestPoint) {
        aMap.addPolyline((new PolylineOptions()).add(
                new LatLng(prePosition.getLatitude(), prePosition.getLongitude()), new LatLng(latestPoint.getLatitude(), latestPoint.getLongitude())).color(
                Color.RED));
        prePosition = latestPoint;
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
//        mlocationClient = new AMapLocationClient(this);
//        Location myLocation = mlocationClient.getLastKnownLocation();
//        prePosition=new Position(myLocation.getLatitude(), myLocation.getLongitude());
//        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
//        aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
//                .fromResource(R.mipmap.red_dot))
//                .position(latLng)
//                .draggable(true));
//        aMap.moveCamera(CameraUpdateFactory
//                .newLatLngZoom(latLng, 14.25f));// 设置指定的可视区域地图
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
        pathQuery.removeAllPathListener();
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
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
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
        switch (v.getId()){
            case R.id.btn_start:
                location.startRecordingPath(key);

                start.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);

                showWindow();
                WLocationUtil.getInstance().setState(location,WLocationUtil.PATH);

                break;
            case R.id.btn_stop:
                start.setVisibility(View.VISIBLE);
                stop.setVisibility(View.GONE);
                time.setVisibility(View.VISIBLE);
                WLocationUtil.getInstance().getLocation(WLocationUtil.PATH).stopRecordingPath(key);
                WLocationUtil.getInstance().removeState(WLocationUtil.PATH);
                break;
            case R.id.btn_title_back:
                finish();
                break;
            case R.id.tv_hint_title:
                startActivity(new Intent(this,InfoActivoty.class));
                break;
            case R.id.iv_location:
                AMapLocation lastKnownLocation = mlocationClient.getLastKnownLocation();
                LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(latLng, 14.25f));// 设置指定的可视区域地图
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation myLocation) {
        prePosition=new Position(myLocation.getLatitude(), myLocation.getLongitude());
        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        if(marker ==null) {
            marker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                    .fromResource(R.mipmap.red_dot))
                    .position(latLng)
                    .draggable(true));
            aMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(latLng, 14.25f));// 设置指定的可视区域地图
        }else {
            marker.setPosition(latLng);
        }

    }
}
