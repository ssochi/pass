package com.example.administrator.pass.tools;

import android.content.Context;
import android.content.res.Resources;
import android.widget.FrameLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.example.administrator.pass.R;
import com.example.administrator.pass.UI.MapActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 11/24 0024.
 */

public class MarkerGroup {
	List<markerState> markerGroup ;
	BaiduMap map;
	Context context;
	Resources res;
	FrameLayout container;
	int open_index = -1;

	/**
	 *
	 * @param context Activity的context
	 * @param map BaiduMap
	 * @param res getResource()
	 * @param container FrameLayout
	 */
	public MarkerGroup(Context context, BaiduMap map, Resources res,FrameLayout container){
		markerGroup = new ArrayList<>();
		this.context = context;
		this.map = map;
		this.res = res;
		this.container = container;
	}

	/**
	 *
	 * @param latLng
	 * @param id
	 * @param title
	 * @param info
	 * @param Username
	 * @param Headpic_path
	 * @param Markpic_path
	 */
	public void addMarker(LatLng latLng, String id, String title, String info, String Username, int Headpic_path, int Markpic_path){
		markerState m = markerState.init(latLng,id,title,info,Username,Headpic_path,Markpic_path,context,map,res);
		markerGroup.add(m);
	}

	/**
	 * 设置点击marker事件 addMarker之后执行这个函数
	 */
	public void setOnClickLisener(){
		map.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				if (!marker.getTitle().equals("null")) {
					int id = Integer.parseInt(marker.getTitle());
					markerState m = markerGroup.get(id);
					if(id != open_index){
						if(open_index != -1){
							markerState m1 = markerGroup.get(open_index);
							m1.removeChatBox();
							m1.hasClick = false;

						}
						m.click(marker, map, map.getMapStatus().zoom,container,context);
						open_index = id;
					}else {
						m.click(marker, map, map.getMapStatus().zoom,container,context);
					}

				}

				return false;
			}
		});
		map.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
			@Override
			public void onMapStatusChangeStart(MapStatus mapStatus) {
				if(open_index != -1&& markerGroup.get(open_index).hasClick){
					markerState m = markerGroup.get(open_index);
					m.removeChatBox();
				}

			}

			@Override
			public void onMapStatusChange(MapStatus mapStatus) {



			}

			@Override
			public void onMapStatusChangeFinish(MapStatus mapStatus) {
				if(open_index != -1&& markerGroup.get(open_index).hasClick){
					markerState m = markerGroup.get(open_index);
					m.addChatbox(container,context,map);
				}

			}
		});
	}

}
