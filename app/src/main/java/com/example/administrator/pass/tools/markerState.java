package com.example.administrator.pass.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
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
	int Headpic_path;
	int Markerpic_path;
	String title;
	String Username;
	boolean isSmall;
	Context context;
	BaiduMap baiduMap;


	public markerState(boolean hasClick, String information, Overlay marker,LatLng latLng,String title,String Username,int Markerpic_path,int Headpic_path,Resources res,Context context,BaiduMap map){
		this.hasClick = hasClick;
		this.info = information;
		this.marker = marker;
		this.latLng = latLng;
		this.res = res;
		this.title = title;
		this.Headpic_path = Headpic_path;
		this.Markerpic_path = Markerpic_path;
		this.Username = Username;
		this.context = context;
		this.baiduMap = map;
		info_len = getTextWidth(info);

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
		isSmall = true;
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
		circleImageView.setImageResource(Headpic_path);
		TextView tv = (TextView) view.findViewById(R.id.tv_chatboxsmall);
		tv.setText(Username);
		MarqueeTextView tv1 = (MarqueeTextView) view.findViewById(R.id.tv_chatboxsmall1);
		tv1.setText(title);
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
			this.isSmall = false;
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
			circleImageView.setImageResource(Headpic_path);
			TextView tv = (TextView) view.findViewById(R.id.tv_chatboxbig);
			tv.setText(Username);
			MarqueeTextView tv1 = (MarqueeTextView) view.findViewById(R.id.tv_chatboxbig1);
			tv1.setText(title);
			RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.chatboxbig_layout);
			TextView tv2 = (TextView) view.findViewById(R.id.tv_chatboxbig2);
			tv2.setText(info);
			tv2.setMovementMethod(new ScrollingMovementMethod());
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
	public void updateChatbox(){

		layoutonClick(fm,context,baiduMap,!isSmall);

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
	 * @param context
	 * @param map
	 * @param res
	 * @return
	 */
	public static markerState init(LatLng latLng, String id, String title, String info, String Username, int Headpic_path, int Markpic_path, Context context, BaiduMap map, Resources res){
		//定义Maker坐标点

		//构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(Markpic_path);
		//构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions()
				.position(latLng)
				.icon(bitmap)
				.title(id);
		//在地图上添加Marker，并显示
		Overlay marker = map.addOverlay(option);
		point point = DisplayUtil.getScreenSizeOfDevice2(res, context);
		WindowWidth = (float) ((float) point.getIn1()*2.54);
		WindowHeight = (float) ((float) point.getIn2()*2.54);
		point = DisplayUtil.getScreenSizeOfDevice1(res,context);
		WindowWidthpx = (float) point.getIn1();
		WindowHeightpx = (float) point.getIn2();


		return new markerState(false,info,marker,latLng,title,Username,Markpic_path,Headpic_path,res,context,map);
	}

	public void removeChatBox(){
		if(hasClick){
			fm.removeView(chatBox);
			chatBox = null;
		}
	}
}