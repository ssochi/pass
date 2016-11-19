package com.example.administrator.pass.tools;

import android.content.res.Resources;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.pass.tools.Maptools.getNextLatlng;
import static com.example.administrator.pass.tools.Maptools.getTextWidth;
import static com.example.administrator.pass.tools.Maptools.zoom;

/**
 * Created by Administrator on 11/17 0017.
 * 地图标志的状态类
 */

public class markerState  {
	public boolean hasClick ;
	public String info;
	public Overlay marker;
	public Overlay line;
	public Overlay chatbox;
	Resources res;
	public LatLng latLng;
	float info_len;
	public markerState(boolean hasClick, String information, Overlay marker,LatLng latLng,Resources res){
		this.hasClick = hasClick;
		this.info = information;
		this.marker = marker;
		this.latLng = latLng;
		this.res = res;
		info_len = getTextWidth(info);
	}

	public void setLine(Overlay line) {
		this.line = line;
	}

	public Overlay getLine() {
		return line;
	}

	public void removeItem(){
		this.line.remove();
		this.chatbox.remove();
	}
	public void setItem(BaiduMap baiduMap){

	}

	/**
	 * Marker的点击处理
	 * @param marker
	 * @param baiduMap
	 * @param z 放大倍数
	 */
	public void click(Marker marker,BaiduMap baiduMap,float z){
		if(!this.hasClick)
		{

			LatLng markerPosition = marker.getPosition();
			addChatbox(markerPosition,baiduMap,z);
			addLine(markerPosition,baiduMap,z);

			this.hasClick = true;
		}else {
			this.getLine().remove();
			this.getChatbox().remove();
			this.hasClick = false;
		}
	}

	/**
	 * 添加文字
	 * @param markerPosition 起始位置
	 * @param baiduMap
	 * @param z zoom
	 * @return
	 */
	public Overlay addLine(LatLng markerPosition, BaiduMap baiduMap, float z) {
		int MaxZoom = zoom[0];
		double distance = 0.0;
		distance = MaxZoom/Math.pow(2.0,z-3);

		LatLng p =  getNextLatlng(markerPosition,distance/2,Math.PI/2);
		p = getNextLatlng(p,distance + distance/5*(info_len/2),0);
		OverlayOptions textOption = new TextOptions()
				.bgColor(0)
				.fontSize(50)
				.fontColor(0xFFFF00FF)
				.text(this.info)
				.position(p);
		//在地图上添加该文字对象并显示
		Overlay line = baiduMap.addOverlay(textOption);
		setLine(line);
		return line;
	}

	/**
	 * 添加对话框
	 * @param markerPosition 起始位置
	 * @param baiduMap
	 * @param z zoom
	 * @return
	 */
	public Overlay addChatbox(LatLng markerPosition,BaiduMap baiduMap,float z){

		int MaxZoom = zoom[0];
		double distance = MaxZoom/Math.pow(2.0,z-3);
		double w = distance;
		double l = (distance*2.5)/11* info_len;
		List<LatLng> pts = new ArrayList<LatLng>();
		LatLng start =  getNextLatlng(markerPosition,distance/2,Math.PI/2);
		start = getNextLatlng(start,distance/2,0);
		LatLng pt1 = getNextLatlng(start,w/3,Math.PI/6);
		LatLng pt2 = getNextLatlng(pt1,w/3,Math.PI/2);
		LatLng pt3 = getNextLatlng(pt2,l,0);
		LatLng pt4 = getNextLatlng(pt3,w,Math.PI/2*3);
		LatLng pt5 = getNextLatlng(pt4,l,Math.PI);
		LatLng pt6 = getNextLatlng(pt5,w/3,Math.PI/2);
		pts.add(start);
		pts.add(pt1);
		pts.add(pt2);
		pts.add(pt3);
		pts.add(pt4);
		pts.add(pt5);
		pts.add(pt6);
		//构建用户绘制多边形的Option对象
		OverlayOptions polygonOption = new PolygonOptions()
				.points(pts)
				.stroke(new Stroke(5, 0xAA00FF00))
				.fillColor(0xAAFFFF00);
		//在地图上添加多边形Option，用于显示
		Overlay Chatbox = baiduMap.addOverlay(polygonOption);
		setChatbox(Chatbox);
		return  Chatbox;

	}

	/**
	 * 初始化
	 * @param latLng marker坐标
	 * @param id marker的id
	 * @param info marker处的留言
	 * @param path 图片的地址
	 * @param mBaiduMap
	 * @return
	 */
	public static markerState init(LatLng latLng, String id, String info, int path, BaiduMap mBaiduMap,Resources res){
		//定义Maker坐标点

		//构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(path);
		//构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions()
				.position(latLng)
				.icon(bitmap)
				.title(id);
		//在地图上添加Marker，并显示
		Overlay marker = mBaiduMap.addOverlay(option);
		return new markerState(false,info,marker,latLng,res);
	}

	public void setChatbox(Overlay chatbox) {
		this.chatbox = chatbox;
	}

	public Overlay getChatbox() {
		return chatbox;
	}
}
