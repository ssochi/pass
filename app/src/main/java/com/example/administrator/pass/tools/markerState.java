package com.example.administrator.pass.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.administrator.pass.R;

import static com.example.administrator.pass.tools.Maptools.getTextWidth;

/**
 * Created by Administrator on 11/17 0017.
 * 地图标志的状态类
 */

public class markerState  {

	public boolean hasClick ;
	String info;
	Overlay marker;
	View chatBox = null;
	Resources res;
	static LatLng latLng;
	float info_len;
	static float WindowWidth;
	static float WindowHeight;
	static float WindowWidthpx;
	static float WindowHeightpx;
	public  FrameLayout fm;


	public markerState(boolean hasClick, String information, Overlay marker,LatLng latLng,Resources res){
		this.hasClick = hasClick;
		this.info = information;
		this.marker = marker;
		this.latLng = latLng;
		this.res = res;

		info_len = getTextWidth(info);

	}

//	public void setLine(Overlay line) {
//		this.line = line;
//	}
//
//	public Overlay getLine() {
//		return line;
//	}

	//	public void removeItem(){
//		this.line.remove();
//		this.chatboxsmall.remove();
//	}
	public void setItem(BaiduMap baiduMap){

	}

	/**
	 * Marker的点击处理
	 * @param marker
	 * @param baiduMap
	 * @param z 放大倍数
	 */
	public void click(Marker marker,BaiduMap baiduMap,float z,FrameLayout fm, Context context){
		if(!this.hasClick)
		{
			this.fm = fm;
			latLng = marker.getPosition();
			addChatbox(fm,context,baiduMap);
			this.hasClick = true;
		}else {
			fm.removeView(chatBox);
			this.hasClick = false;
		}
	}

	public void addChatbox(final FrameLayout Fm, final Context context, final BaiduMap baiduMap) {
		removeChatBox();
		final Point p = baiduMap.getProjection().toScreenLocation(latLng);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				600, 100);
//		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
//				600, 300);
		lp.setMargins((int)(p.x)+75,(int)(p.y)-125,0,0);
		LayoutInflater inflater3 = LayoutInflater.from(context);
		View view = inflater3.inflate(R.layout.chatboxsmall,null);
		view.setLayoutParams(lp);
		CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.CircleView_chatboxsmall);
		circleImageView.setImageResource(R.mipmap.head);
		TextView tv = (TextView) view.findViewById(R.id.tv_chatboxsmall);
		tv.setText("neuqer");
		TextView tv1 = (TextView) view.findViewById(R.id.tv_chatboxsmall1);
		tv1.setText("目标 工学馆八楼");
		RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.chatboxsmall_layout);
		relativeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layoutonClick(fm,context,baiduMap,true);
			}
		});
		Fm.addView(view);
		chatBox = view;
		fm = Fm;



	}
	public void layoutonClick(final FrameLayout Fm, final Context context, final BaiduMap baiduMap, boolean isSmall){
		removeChatBox();
		if(isSmall){
			final Point p = baiduMap.getProjection().toScreenLocation(latLng);
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
					600, 500);
//		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
//				600, 300);
			lp.setMargins((int)(p.x)+75,(int)(p.y)-125,0,0);
			LayoutInflater inflater3 = LayoutInflater.from(context);
			View view = inflater3.inflate(R.layout.chatboxbig,null);
			view.setLayoutParams(lp);
			CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.CircleView_chatboxbig);
			circleImageView.setImageResource(R.mipmap.head);
			TextView tv = (TextView) view.findViewById(R.id.tv_chatboxbig);
			tv.setText("neuqer");
			TextView tv1 = (TextView) view.findViewById(R.id.tv_chatboxbig1);
			tv1.setText("目标 工学馆八楼");
			RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.chatboxbig_layout);
			TextView tv2 = (TextView) view.findViewById(R.id.tv_chatboxbig2);
			tv2.setText(info);
			relativeLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					layoutonClick(fm,context,baiduMap,false);
				}
			});
			Fm.addView(view);
			chatBox = view;
			fm = Fm;
		}else {
			addChatbox(fm,context,baiduMap);
		}
	}

	/**
	 * 添加文字
	 * @param markerPosition 起始位置
	 * @param baiduMap
	 * @param z zoom
	 * @return
	 */
//	public Overlay addLine(LatLng markerPosition, BaiduMap baiduMap, float z) {
//		int MaxZoom = zoom[0];
//		double distance = 0.0;
//		distance = MaxZoom/Math.pow(2.0,z-3);
//
//		LatLng p =  getNextLatlng(markerPosition,distance/2,Math.PI/2);
//		p = getNextLatlng(p,distance + distance/5*(info_len/2),0);
//		OverlayOptions textOption = new TextOptions()
//				.bgColor(0)
//				.fontSize(50)
//				.fontColor(0xFFFF00FF)
//				.text(this.info)
//				.position(p);
//		//在地图上添加该文字对象并显示
//		Overlay line = baiduMap.addOverlay(textOption);
//		setLine(line);
//		return line;
//	}

	/**
	 * 添加对话框
	 * @param markerPosition 起始位置
	 * @param baiduMap
	 * @param z zoom
	 * @return
	 */
//	public Overlay addChatbox(LatLng markerPosition,BaiduMap baiduMap,float z){
//
//		int MaxZoom = zoom[0];
//		double distance = MaxZoom/Math.pow(2.0,z-3);
//		double w = distance;
//		double l = (distance*2.5)/11* info_len;
//		List<LatLng> pts = new ArrayList<LatLng>();
//		LatLng start =  getNextLatlng(markerPosition,distance/2,Math.PI/2);
//		start = getNextLatlng(start,distance/2,0);
//		LatLng pt1 = getNextLatlng(start,w/3,Math.PI/6);
//		LatLng pt2 = getNextLatlng(pt1,w/3,Math.PI/2);
//		LatLng pt3 = getNextLatlng(pt2,l,0);
//		LatLng pt4 = getNextLatlng(pt3,w,Math.PI/2*3);
//		LatLng pt5 = getNextLatlng(pt4,l,Math.PI);
//		LatLng pt6 = getNextLatlng(pt5,w/3,Math.PI/2);
//		pts.addChatbox(start);
//		pts.addChatbox(pt1);
//		pts.addChatbox(pt2);
//		pts.addChatbox(pt3);
//		pts.addChatbox(pt4);
//		pts.addChatbox(pt5);
//		pts.addChatbox(pt6);
//		//构建用户绘制多边形的Option对象
//		OverlayOptions polygonOption = new PolygonOptions()
//				.points(pts)
//				.stroke(new Stroke(5, 0xAA00FF00))
//				.fillColor(0xAAFFFF00);
//		//在地图上添加多边形Option，用于显示
//		Overlay Chatbox = baiduMap.addOverlay(polygonOption);
//		setChatbox(Chatbox);
//		return  Chatbox;
//
//	}

	/**
	 * 初始化
	 * @param latLng marker坐标
	 * @param id marker的id
	 * @param info marker处的留言
	 * @param path 图片的地址
	 * @param mBaiduMap
	 * @return
	 */
	public static markerState init(LatLng latLng, String id, String info, int path, BaiduMap mBaiduMap,Resources res,Context context){
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
		point point = DisplayUtil.getScreenSizeOfDevice2(res, context);
		WindowWidth = (float) ((float) point.getIn1()*2.54);
		WindowHeight = (float) ((float) point.getIn2()*2.54);
		point = DisplayUtil.getScreenSizeOfDevice1(res,context);
		WindowWidthpx = (float) point.getIn1();
		WindowHeightpx = (float) point.getIn2();
		return new markerState(false,info,marker,latLng,res);
	}
	//
//	public void setChatbox(Overlay chatboxsmall) {
//		this.chatboxsmall = chatboxsmall;
//	}
//
//	public Overlay getChatbox() {
//		return chatboxsmall;
//	}
	public void removeChatBox(){
		if(hasClick){
			fm.removeView(chatBox);
			chatBox = null;
		}
	}
}