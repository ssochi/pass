package com.example.administrator.pass.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.example.administrator.pass.MapActivity;
import com.example.administrator.pass.R;
import com.example.administrator.pass.Service.LocationService;
import com.example.administrator.pass.tools.point;
import com.example.administrator.pass.tools.smoke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 11/1 0001.
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
		center = initTrajectory();
		//去除百度logo
		mMapView.removeViewAt(1);
		//去除zoom工具
		mMapView.showZoomControls(false);
		// 开启室内图
		mBaiduMap.setIndoorEnable(true);
		// 定位初始化


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

	private point initTrajectory() {
		Map<Integer,point> params = new HashMap<>();
		int num = 5;
		Random r = new Random();
		point MAX = new point(-90,-180);
		point MIN = new point(90,180);
		for(int i=0;i<num;i++){
			params.put(i,new point(1*r.nextDouble()-0.5,2*r.nextDouble()-1));//in1[-90,90] in2[-180,180]
		}

		for(int i=0;i<num;i++){
			if(params.get(i).getIn1()>MAX.getIn1()){
				MAX.setIn1(params.get(i).getIn1());
			}
			if(params.get(i).getIn2()>MAX.getIn2()){
				MAX.setIn2(params.get(i).getIn2());
			}
			if(params.get(i).getIn1()<MIN.getIn1()){
				MIN.setIn1(params.get(i).getIn1());
			}
			if(params.get(i).getIn2()<MIN.getIn2()){
				MIN.setIn2(params.get(i).getIn2());
			}
			point p = params.get(i);
			//定义Maker坐标点
			LatLng point = new LatLng(p.getIn1(), p.getIn2());
			System.out.println("N0"+i+"********"+p.getIn1()+"N0"+i+"********"+p.getIn2());
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
		Integer zoom[] = {2000000,1000000,500000,200000,100000,50000,25000,20000,10000,5000,2000,1000,500,200,100,50,20,10};
		for(int i=0;i<zoom.length;i++){
			if(zoom[i]<distance/7){
				return i+3;
			}
		}
		return 3;
	}

	private void addTrajectory(Map<Integer, point> params, int num) {
		//构造纹理资源
		BitmapDescriptor custom1 = BitmapDescriptorFactory
				.fromResource(R.mipmap.line);


		//构造纹理队列
		List<BitmapDescriptor> customList = new ArrayList<BitmapDescriptor>();
		customList.add(custom1);
		List<LatLng> points = new ArrayList<LatLng>();
		List<Integer> index = new ArrayList<Integer>();
		for(int i=0;i<num;i++){
			points.add(new LatLng(params.get(i).getIn1(),params.get(i).getIn2()));
			index.add(0);
		}
		OverlayOptions ooPolyline = new PolylineOptions().width(15).color(0xAAFF0000).points(points).customTextureList(customList).textureIndex(index);
//添加到地图
		mBaiduMap.addOverlay(ooPolyline);


	}

	private void initHeatMap() {
		//设置渐变颜色值
		int[] DEFAULT_GRADIENT_COLORS = {Color.rgb(102, 225,  0), Color.rgb(255, 0, 0)  };
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
