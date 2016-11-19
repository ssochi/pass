package com.example.administrator.pass;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

import com.example.administrator.pass.tools.markerState;
import com.example.administrator.pass.tools.Maptools;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.Gradient;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.example.administrator.pass.Service.LocationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapActivity extends AppCompatActivity {
    LocationService mService ;
    MapView mMapView = null;
    BaiduMap mBaiduMap;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    boolean isFirstLoc = true;
    static LatLng mpostion = new LatLng(39.926854,119.56445);
    List<markerState> markerList = new ArrayList<>();
    Overlay Smoke;
    float zoom;
    boolean zoomchange = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        init();
    }

    private void init() {
        initMap();
        initService();
    }

    private void initMap() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap = mMapView.getMap();
        addMarker(mpostion,"0","infomation here",R.mipmap.location_marker);
        //去除百度logo
        mMapView.removeViewAt(1);
//        initHeatMap();
        zoom = mBaiduMap.getMapStatus().zoom;

        mBaiduMap.setMyLocationEnabled(true);
        // 开启室内图
        mBaiduMap.setIndoorEnable(true);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!marker.getTitle().equals("null")){
                    int id = Integer.parseInt(marker.getTitle());
                    markerState m = markerList.get(id);
                    m.click(marker,mBaiduMap,mBaiduMap.getMapStatus().zoom);
                }

                return false;
            }
        });
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                zoom = mapStatus.zoom;

                    for(int i = 0;i<markerList.size();i++){
                        markerState m = markerList.get(i);
                        if(m.hasClick){
                            m.getLine().remove();
                            m.getChatbox().remove();
                        }
                    }

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {

                float z = mapStatus.zoom;

                    for(int i = 0;i<markerList.size();i++){
                        markerState m = markerList.get(i);
                        if(m.hasClick){
                            m.getLine().remove();
                            m.getChatbox().remove();
                            m.addChatbox(m.latLng,mBaiduMap,z);
                            m.addLine(m.latLng,mBaiduMap,z);


                        }
                    }

            }
        });


    }

    private void initHeatMap() {
        //设置渐变颜色值
        int[] DEFAULT_GRADIENT_COLORS = {Color.rgb(255, 0, 0), Color.rgb(255, 0, 0) };
        //设置渐变颜色起始值
        float[] DEFAULT_GRADIENT_START_POINTS = { 0.2f, 1f };
        //构造颜色渐变对象
        Gradient gradient = new Gradient(DEFAULT_GRADIENT_COLORS, DEFAULT_GRADIENT_START_POINTS);
        //以下数据为随机生成地理位置点，开发者根据自己的实际业务，传入自有位置数据即可
        List<LatLng> randomList = new ArrayList<LatLng>();
        Random r = new Random();
        for (int i = 0; i < 5000; i++) {
            // 116.220000,39.780000 116.570000,40.150000
            int rlat = r.nextInt(370000);
            int rlng = r.nextInt(370000);
            int lat = 39930528-370000/2 + rlat;
            int lng = 119555501-370000/2 + rlng;
            LatLng ll = new LatLng(lat / 1E6, lng / 1E6);
            randomList.add(ll);
        }
        //在大量热力图数据情况下，build过程相对较慢，建议放在新建线程实现
        HeatMap heatmap = new HeatMap.Builder()
                .data(randomList)
                .gradient(gradient)
                .build();
        //在地图上添加热力图
        mBaiduMap.addHeatMap(heatmap);

    }

    private void initService() {
        MyLocationListener myListener = new MyLocationListener();
        mService = new LocationService(getApplicationContext());
        mService.registerListener(myListener);
        mService.start();
    }

    private void initSmoke(int acc,Double r,LatLng postion) {

        List<LatLng> pts = new ArrayList<LatLng>();
        Double latitude = postion.latitude;
        Double longitude = postion.longitude;
        Double lat = 0.0;
        Double lon = 0.0;

        for(int i=0;i<acc/2+1;i++){
            lat = latitude + r*Math.sin((2*Math.PI)/acc*i);
            lon = longitude + r*Math.cos((2*Math.PI)/acc*i);
            LatLng pt = new LatLng(lat,lon );
            pts.add(pt);
        }
        LatLng pt1 = new LatLng(lat,180);
        pts.add(pt1);
        LatLng pt2 = new LatLng(90,180);
        pts.add(pt2);
        LatLng pt3 = new LatLng(90,-180);
        pts.add(pt3);
        LatLng pt4 = new LatLng(latitude + r*Math.sin((2*Math.PI)/acc*0),-180);
        pts.add(pt4);
        for(int i=acc/2;i<acc+1;i++){
            lat = latitude + r*Math.sin((2*Math.PI)/acc*i);
            lon = longitude + r*Math.cos((2*Math.PI)/acc*i);
            LatLng pt = new LatLng(lat,lon );
            pts.add(pt);
        }
        pt1 = new LatLng(lat,180);
        pts.add(pt1);
        pt2 = new LatLng(-90,180);
        pts.add(pt2);
        pt3 = new LatLng(-90,-180);
        pts.add(pt3);
        pt4 = new LatLng(latitude + r*Math.sin((2*Math.PI)/acc*(acc/2)),-180);
        pts.add(pt4);
        //构建用户绘制多边形的Option对象
        OverlayOptions polygonOption = new PolygonOptions()
                .points(pts)
                .stroke(new Stroke(4, 0))
                .fillColor(0x99999900);
//在地图上添加多边形Option，用于显示
        mBaiduMap.addOverlay(polygonOption);


    }








    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
        mService.stop();
        mBaiduMap.setMyLocationEnabled(false);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }








    class MyLocationListener implements BDLocationListener {
         StringBuffer sb = new StringBuffer(256);

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());
            //向map设置位置信息（定位）
            if (isFirstLoc) {
                mpostion =  new LatLng(location.getLatitude(), location.getLongitude());
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                Smoke = Maptools.getSmokeOverlayOptions(new LatLng(location.getLatitude(), location.getLongitude()),200.0,40,mBaiduMap);
                MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);
            }else {
                if(mpostion.latitude !=location.getLatitude()&&mpostion.longitude != location.getLongitude()){
                mpostion =  new LatLng(location.getLatitude(), location.getLongitude());
                MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);
                Smoke.remove();
                Smoke = Maptools.getSmokeOverlayOptions(new LatLng(location.getLatitude(), location.getLongitude()),200.0,40,mBaiduMap);
            }
            }



        }





    }
    public void addMarker(LatLng latLng,String id,String info,int path){
        markerList.add(markerState.init(latLng,id,info,path,mBaiduMap,getResources()));
    }
}
