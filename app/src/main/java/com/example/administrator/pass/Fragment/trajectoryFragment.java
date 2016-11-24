package com.example.administrator.pass.Fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Gradient;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.administrator.pass.R;
import com.example.administrator.pass.Service.LocationService;
import com.example.administrator.pass.tools.MarkerGroup;
import com.example.administrator.pass.tools.point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 11/1 0001.
 * 轨迹 主页面
 */

public class trajectoryFragment extends Fragment {
	LocationService mService ;
	MapView mMapView = null;
	BaiduMap mBaiduMap;
	point center;
	int zoom ;
	private MyLocationConfiguration.LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	boolean isFirstLoc = true;
	View root;
	MarkerGroup markerGroup;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		SDKInitializer.initialize(getActivity().getApplicationContext());
		root = inflater.inflate(R.layout.fragment_trajectory,container,false);
		init();




		return root;
	}
	private void init() {
		initMap();

		initService();
	}

	private void initMap() {
		mMapView = (MapView) root.findViewById(R.id.trajectoryMapView);
		mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
		mBaiduMap = mMapView.getMap();
		Map<Integer,LatLng> data =  initData();
		center = initTrajectory(data);
		markerGroup = new MarkerGroup(getActivity(),mBaiduMap,getResources(), (FrameLayout) root.findViewById(R.id.zone_container));
		for(int i = 0;i<data.size();i++){
			markerGroup.addMarker(data.get(i),""+i,"title"+i,"info"+i,"Username"+i,R.drawable.mini_avatar_shadow,R.mipmap.location_marker);
		}
		markerGroup.setOnClickLisener();
		//去除百度logo
		mMapView.removeViewAt(1);
		//去除zoom工具
		mMapView.showZoomControls(false);
		// 开启室内图
		mBaiduMap.setIndoorEnable(true);
		// 定位初始化


	}

	private Map<Integer,LatLng> initData() {
		Map<Integer,LatLng> params = new HashMap<>();
		int num = 5;
		Random r = new Random();
		for(int i=0;i<num;i++){
			params.put(i,new LatLng(90*r.nextDouble()-0.5,180*r.nextDouble()-1));//in1[-90,90] in2[-180,180]
		}
		return params;
	}


	private point initTrajectory(Map<Integer,LatLng> params) {
		point MAX = new point(-90,-180);
		point MIN = new point(90,180);
		int num = params.size();
		for(int i=0;i<num;i++){
			if(params.get(i).latitude>MAX.getIn1()){
				MAX.setIn1(params.get(i).latitude);
			}
			if(params.get(i).longitude>MAX.getIn2()){
				MAX.setIn2(params.get(i).longitude);
			}
			if(params.get(i).latitude<MIN.getIn1()){
				MIN.setIn1(params.get(i).latitude);
			}
			if(params.get(i).longitude<MIN.getIn2()){
				MIN.setIn2(params.get(i).longitude);
			}
			LatLng p = params.get(i);
			//定义Maker坐标点
			LatLng point = new LatLng(p.latitude, p.longitude);
			System.out.println("N0"+i+"********"+p.latitude+"N0"+i+"********"+p.longitude);
//构建Marker图标
			BitmapDescriptor bitmap = BitmapDescriptorFactory
					.fromResource(R.mipmap.location_marker);
//构建MarkerOption，用于在地图上添加Marker
			OverlayOptions option = new MarkerOptions()
					.position(point)
					.icon(bitmap);
//在地图上添加Marker，并显示
			mBaiduMap.addOverlay(option);
		}
		zoom = getZoom(MAX,MIN);
		System.out.println("ZOOM:"+zoom);
		addTrajectory(params,num);
		return new point((MAX.getIn1()+MIN.getIn1())/2,(MAX.getIn2()+MIN.getIn2())/2);

	}

	private int getZoom(point max, point min) {
		double distance = DistanceUtil. getDistance(new LatLng(max.getIn1(),max.getIn2()),new LatLng(min.getIn1(),min.getIn2()));
		System.out.println("distance:"+distance);
		int zoom[] = {2000000,1000000,500000,200000,100000,50000,25000,20000,10000,5000,2000,1000,500,200,100,50,20,10,5,2};
		for(int i=0;i<zoom.length;i++){
			if(zoom[i]<distance/7){
				return i+3;
			}
		}
		return 3;
	}

	private void addTrajectory(Map<Integer, LatLng> params, int num) {
		//构造纹理资源
		BitmapDescriptor custom1 = BitmapDescriptorFactory
				.fromResource(R.mipmap.line);


		//构造纹理队列
		List<BitmapDescriptor> customList = new ArrayList<BitmapDescriptor>();
		customList.add(custom1);
		List<LatLng> points = new ArrayList<LatLng>();
		List<Integer> index = new ArrayList<Integer>();
		for(int i=0;i<num;i++){
			points.add(new LatLng(params.get(i).latitude,params.get(i).longitude));
			index.add(0);
		}
		OverlayOptions ooPolyline = new PolylineOptions().width(15).color(0xAAFF0000).points(points).customTextureList(customList).textureIndex(index);
//添加到地图
		mBaiduMap.addOverlay(ooPolyline);


	}


	private void initService() {
		trajectoryFragment.MyLocationListener myListener = new trajectoryFragment.MyLocationListener();
		mService = new LocationService(getActivity().getApplicationContext());
		mService.registerListener(myListener);
		mService.start();

	}











	@Override
	public void onDestroy() {
		super.onDestroy();
		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		mMapView = null;
		mService.stop();
		mBaiduMap.setMyLocationEnabled(false);
	}
	@Override
	public void onResume() {
		super.onResume();
		//在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
		//在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}








	class MyLocationListener implements BDLocationListener {
		StringBuffer sb = new StringBuffer(256);

		@Override
		public void onReceiveLocation(BDLocation location) {

//			//向map设置位置信息（定位）
//			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
//					// 此处设置开发者获取到的方向信息，顺时针0-360
//					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
//			mBaiduMap.setMyLocationData(locData);

			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(center.getIn1(), center.getIn2());
				MapStatus.Builder builder = new MapStatus.Builder();
				builder.target(ll).zoom(zoom);//zoom是放大倍数
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
			}
		}





	}



}
