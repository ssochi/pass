package com.example.administrator.pass.UI;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.example.administrator.pass.R;
import com.example.administrator.pass.tools.MarkerGroup;
import com.example.administrator.pass.tools.markerState;
import com.example.administrator.pass.tools.Maptools;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
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
	LocationService mService;
	MapView mMapView = null;
	BaiduMap mBaiduMap;
	MarkerGroup markerGroup;
	private MyLocationConfiguration.LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	boolean isFirstLoc = true;
	Overlay Smoke;
	float zoom;
	boolean zoomchange = false;
	int open_index = -1;
	LatLng mpostion = new LatLng(39.926854, 119.56445);


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SDKInitializer.initialize(getApplicationContext());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		getSupportActionBar().hide();
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
		//去除百度logo
		mMapView.removeViewAt(1);
//        initHeatMap();
		zoom = mBaiduMap.getMapStatus().zoom;

		mBaiduMap.setMyLocationEnabled(true);
		// 开启室内图
		mBaiduMap.setIndoorEnable(true);
		initMarker();



	}

	private void initMarker() {
		markerGroup = new MarkerGroup(MapActivity.this,mBaiduMap,getResources(), (FrameLayout) findViewById(R.id.MapFramelayout));
		LatLng postion = new LatLng(39.926854, 119.56445);
//		LatLng mpostion1 = new LatLng(39.926854+0.001, 119.56445+0.001);
//		LatLng mpostion2 = new LatLng(39.926854+0.002, 119.56445);
		String info = "你为什么要召唤我？\n"+ "为了十四斤的农药\n"+ "讲课开始了~~\n"+ "嘿嘿，该收钱了~\n"+
				"侍从！来我房间！\n"+ "~啊~感觉真棒！\n"+ "随风而去吧 小虫子们！\n "+ "我对你非常感谢\n"+
				"铭记于心年轻人\n"+ "这群怪不是我引的\n"+ "你~需要我的帮助~\n";
		markerGroup.addMarker(postion, "0", "个性签名总是很长很长很长很长很长很长很长很长很长很长很长很长",info ,"ssochi [VIP]",R.mipmap.head,R.mipmap.location_marker);
		markerGroup.setOnClickLisener();
	}

	private void initHeatMap() {
		//设置渐变颜色值
		int[] DEFAULT_GRADIENT_COLORS = {Color.rgb(255, 0, 0), Color.rgb(255, 0, 0)};
		//设置渐变颜色起始值
		float[] DEFAULT_GRADIENT_START_POINTS = {0.2f, 1f};
		//构造颜色渐变对象
		Gradient gradient = new Gradient(DEFAULT_GRADIENT_COLORS, DEFAULT_GRADIENT_START_POINTS);
		//以下数据为随机生成地理位置点，开发者根据自己的实际业务，传入自有位置数据即可
		List<LatLng> randomList = new ArrayList<LatLng>();
		Random r = new Random();
		for (int i = 0; i < 5000; i++) {
			// 116.220000,39.780000 116.570000,40.150000
			int rlat = r.nextInt(370000);
			int rlng = r.nextInt(370000);
			int lat = 39930528 - 370000 / 2 + rlat;
			int lng = 119555501 - 370000 / 2 + rlng;
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

		mService = new LocationService(getApplicationContext());
		mService.registerListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				//向map设置位置信息（定位）
				if (isFirstLoc) {
					mpostion = new LatLng(location.getLatitude(), location.getLongitude());
					isFirstLoc = false;
					LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
					MapStatus.Builder builder = new MapStatus.Builder();
					builder.target(ll).zoom(18.0f);
					mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
					Smoke = Maptools.getSmokeOverlayOptions(new LatLng(location.getLatitude(), location.getLongitude()), 200.0, 40, mBaiduMap);
					MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
							// 此处设置开发者获取到的方向信息，顺时针0-360
							.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
					mBaiduMap.setMyLocationData(locData);
				} else {
					if (mpostion.latitude != location.getLatitude() && mpostion.longitude != location.getLongitude()) {
						mpostion = new LatLng(location.getLatitude(), location.getLongitude());
						MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
								// 此处设置开发者获取到的方向信息，顺时针0-360
								.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
						mBaiduMap.setMyLocationData(locData);
						Smoke.remove();
						Smoke = Maptools.getSmokeOverlayOptions(new LatLng(location.getLatitude(), location.getLongitude()), 200.0, 40, mBaiduMap);
					}
				}
			}
		});
		mService.start();
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





}
