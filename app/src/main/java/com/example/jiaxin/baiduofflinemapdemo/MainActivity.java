package com.example.jiaxin.baiduofflinemapdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity implements BDLocationListener {
    private BaiduMap baiduMap;
    private TextureMapView mapView;
    private LocationClient locationClient;

    boolean isFirstLoc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView = (TextureMapView) findViewById(R.id.mapview);
        initMap();
    }

    private void initMap() {
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL
                , true, null));
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //设置地图坐标到上海
        LatLng ll = new LatLng(31.245105, 121.506377);
        MapStatus mapStatus = new MapStatus.Builder().target(ll).zoom(12).build();
        MapStatusUpdate msu = MapStatusUpdateFactory
                .newMapStatus(mapStatus);
        baiduMap.animateMapStatus(msu);
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(this);
        initLocation();
        locationClient.start();
        locationClient.requestLocation();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
//        option.setLocationNotify(true);
        option.setScanSpan(3000);
        locationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        if (locationClient != null) {
            locationClient.stop();
        }
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (mapView == null || bdLocation == null)
            return;

        MyLocationData locationData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                .direction(100)
                .latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude())
                .build();
        Toast.makeText(this, bdLocation.getLatitude() + " , " + bdLocation.getLongitude() + "," + bdLocation.getLocType(), Toast.LENGTH_SHORT).show();
        baiduMap.setMyLocationData(locationData);

        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatus mapStatus = new MapStatus.Builder().target(ll).zoom(16).build();
            MapStatusUpdate msu = MapStatusUpdateFactory
                    .newMapStatus(mapStatus);
            baiduMap.animateMapStatus(msu);
        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }
}
