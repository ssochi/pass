package com.example.administrator.pass.tools;

import android.graphics.Paint;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 11/10 0010.
 * 对经纬度处理的类
 */

public class Maptools {

	static Double  R = 6371004.0;
	static Integer zoom[] = {2000000,1000000,500000,200000,100000,50000,25000,20000,10000,5000,2000,1000,500,200,100,50,20,10,5,2};

	/**
	 *  将经纬度坐标转换为xyz坐标
	 * @param lng
	 * @return V3 V3是Maptools的子类
	 */
	 static V3 changeToXyz(LatLng lng){
		 V3 v = new V3();
		if (lng.latitude<0){
			v.var1 = 90 + Math.abs(lng.latitude);
		}else {
			v.var1 = 90 - lng.latitude ;
		 }
		 if(lng.longitude<0){
			 v.var2 = 180 + Math.abs(lng.longitude);
		 }else {
			 v.var2 = 180 - lng.longitude;
		 }
		 v.var3 = R;
		 V3 temp = new V3();
		 temp.var1 = v.var3*Math.sin(Math.PI*v.var1/180)*Math.cos(Math.PI*v.var2/180);
		 temp.var2 = v.var3*Math.sin(Math.PI*v.var1/180)*Math.sin(Math.PI*v.var2/180);
		 temp.var3 = v.var3*Math.cos(Math.PI*v.var1/180);
		 return temp;
	}

	/**
	 * 将xyz坐标转换为经纬度坐标
	 * @param in
	 * @return Latlng
	 */
	static LatLng changexyzToLatLng(V3 in){
		Double x = in.var1;
		Double y = in.var2;
		Double z = in.var3;
		Double r = Math.sqrt(x*x+y*y+z*z);
		Double ceta =    Math.atan(y/x);//x
		Double gama = Math.acos(z/r); //z
		if (x==0.0&&y==0.0){
			ceta = 0.0;
		}
		ceta = Math.abs(ceta);
		if(x>0&&y>=0){
			ceta  = ceta;
		}
		if(x<=0&&y>0){
			ceta = Math.PI/2-ceta;
			ceta += Math.PI/2;
		}
		if(x<0&&y<=0){
			ceta += Math.PI;
		}
		if(x>=0&&y<0){
			ceta = Math.PI/2-ceta;
			ceta += Math.PI/2*3;
		}
		Double latitude = (gama/Math.PI)*180;
		Double longitude = (ceta/Math.PI)*180;
		if (latitude >= 90){
			latitude = -1*(latitude-90);
		}else {
			latitude = 90 - latitude;
		}
		if (longitude >=180){
			longitude = -1*(longitude-180);
		}else {
			longitude = 180 - longitude;
		}
		return new LatLng(latitude,longitude);

	}

	/**
	 * 获得并设置smoke
	 * @param Center 圆心
	 * @param r 半径
	 * @param acc 精度
	 * @param map BaiduMap
	 * @return Overlay
	 */
	public static Overlay getSmokeOverlayOptions(LatLng Center, Double r, int acc, BaiduMap map){
		List<LatLng> pts = new ArrayList<LatLng>();
		changexyzToLatLng(changeToXyz(new LatLng(0.0,0.0)));
		r = R*Math.sin(r/R);
		V3 center = changeToXyz(Center);
		V3 vector = new V3(1.0,-1*center.var1/center.var2,0.0);
		Double rate =  Math.sqrt(R*R-r*r)/R;
		V3 half = new V3(rate*center.var1,rate*center.var2,rate*center.var3);
		V3 temp = new V3();
		V3 mR = new V3();
		LatLng lat = new LatLng(0.0,0.0);
		for(int i=0;i<acc/2+1;i++){

//				temp = new V3(R*Math.cos((2*Math.PI)/acc*i),R*Math.sin((2*Math.PI)/acc*i),0.0);
				temp = getRotate(center,vector,(2*Math.PI)/acc*i);
//				mR = getvertical(center,temp,r);
				mR = getmR(temp,r);
//			System.out.println(mR.var1+"%%%%%%%"+mR.var2+"%%%%%%%%"+mR.var3);
				mR = V3.add(mR,half);
				lat = changexyzToLatLng(mR);
				System.out.println(lat.latitude+"**********"+lat.longitude);
				pts.add(lat);

		}


		LatLng pt1 = new LatLng(lat.latitude,180);
		pts.add(pt1);
		LatLng pt2 = new LatLng(90,180);
		pts.add(pt2);
		LatLng pt3 = new LatLng(90,-180);
		pts.add(pt3);
		LatLng pt4 = new LatLng( getPoint(center,acc,0,r,half,vector).latitude,-180);
		pts.add(pt4);
		for(int i=acc/2;i<acc+1;i++){

				temp = getRotate(center,vector,(2*Math.PI)/acc*i);
//				mR = getvertical(center,temp,r);
				mR = getmR(temp,r);

				mR = V3.add(mR,half);
//			System.out.println(mR.var1+"%%%%%%%"+mR.var2+"%%%%%%%%"+mR.var3);
				lat = changexyzToLatLng(mR);
				System.out.println(lat.latitude+"**********"+lat.longitude);
				pts.add(lat);
		}

		pt1 = new LatLng(lat.latitude,180);
		pts.add(pt1);
		pt2 = new LatLng(-90,180);
		pts.add(pt2);
		pt3 = new LatLng(-90,-180);
		pts.add(pt3);
		pt4 = new LatLng(getPoint(center,acc,acc/2,r,half,vector).latitude,-180);
		pts.add(pt4);
		OverlayOptions polygonOption = new PolygonOptions()
				.points(pts)
				.stroke(new Stroke(4,0))
				.fillColor(0x99999900);
		Overlay smoke = map.addOverlay(polygonOption);
		return  smoke;
	}

	/**
	 * xyz坐标系容器
	 */
	static class V3 {
		double var1 = 0.0;
		double var2 = 0.0;
		double var3 = 0.0;
		V3(){

		}
		V3(Double var1,Double var2, Double var3){
			this.var1 = var1;
			this.var2 = var2;
			this.var3 = var3;
		}

		/**
		 * 两向量相加
		 * @param v1
		 * @param v2
		 * @return
		 */
		public static V3 add(V3 v1, V3 v2){
			return new V3(v1.var1+v2.var1,v1.var2+v2.var2,v1.var3+v2.var3);
		}

	}

	/**
	 * 向量旋转 center是旋转轴 vector旋转向量 ang旋转角度
	 * @param center
	 * @param vector
	 * @param ang
	 * @return
	 */
	public static V3 getRotate(V3 center, V3 vector, Double ang){
		center = getmR(center,1.0);
		Double x = center.var1;
		Double y = center.var2;
		Double z = center.var3;
		Matrix a = new Matrix(3,3);
		Matrix b = new Matrix(3,3);
		Matrix i = new Matrix(3,3);
		double[][] A = new double[3][3];
		A[0][0] = x * x;    A[0][1] = x * y;    A[0][2] = x * z;
		A[1][0] = y * x;    A[1][1] = y * y;    A[1][2] = y * z;
		A[2][0] = z * x;    A[2][1] = z * y;    A[2][2] = z * z;
		double[][] B = new double[3][3];
		B[0][0] =  0;       B[0][1] = -z;       B[0][2] = y;
		B[1][0] =  z;       B[1][1] =  0;       B[1][2] = -x;
		B[2][0] = -y;       B[2][1] =  x;       B[2][2] =  0;
		double[][] I = new double[3][3];
		I[0][0] =  1;       I[0][1] =  0;       I[0][2] = 0;
		I[1][0] =  0;       I[1][1] =  1;       I[1][2] = 0;
		I[2][0] =  0;       I[2][1] =  0;       I[2][2] =  1;
		a.init(A);
//		System.out.println("A:");
//		a.Output();
		b.init(B);
//		System.out.println("B:");
//		b.Output();
		i.init(I);
//		System.out.println("I:");
//		i.Output();
		Matrix m = new Matrix(3,3);
		m = i.Minus(a);
//		m.Output();
		m = m.mMultiply(Math.cos(ang));
//		m.Output();
		Matrix t = new Matrix(3,3);
		t = b;
		t = t.mMultiply(Math.sin(ang));
//		m.Output();
		m = m.Plus(t);
//		m.Output();
		m = m.Plus(a);
//		m.Output();
		m = m.Trans();
//		m.Output();
		t = new Matrix(3,1);
		double[][] T = new double[3][1];

//		T[0][0] = 1.0;T[1][0] =1.0;//T[0][2] = -1*(center.var1*center.var2)/center.var3;
//		T[2][0] = -1*(center.var1*center.var2)/center.var3;
		T[0][0] = vector.var1;T[1][0] =vector.var2;T[2][0] =vector.var3;
				t.init(T);
		m = m.Multiply(t);
		m = m.Trans();
//		m.Output();
		return new V3(m.mat[0][0],m.mat[0][1],m.mat[0][2]);

	}

	/**
	 * 求与v1,v2向量垂直的向量 r是返回向量的膜
	 * @param v1
	 * @param v2
	 * @param r
	 * @return
	 */
	public static V3 getvertical(V3 v1, V3 v2, Double r){
		Double ax = v1.var1;
		Double ay = v1.var2;
		Double az = v1.var3;
		Double bx = v2.var1;
		Double by = v2.var2;
		Double bz = v2.var3;
		double x = ay*bz - az*by;
		double y = az*bx - ax*bz;
		double z = ax*by - ay*bx;
		V3 target = new V3(x,y,z);
		target = getmR(target, r);
		return target;
	}

	/**
	 * 获得两个点的距离
	 * @param in1
	 * @param in2
	 * @return
	 */
	public static double getDistance(V3 in1,V3 in2){
		double distance = Math.sqrt(Math.pow((in1.var1-in2.var1),2)+Math.pow((in1.var2-in2.var2),2)+Math.pow((in1.var3-in2.var3),2));
		return distance;
	}
	/**
	 * 获得方向相同膜为r的向量
	 * @param v
	 * @param r
	 * @return
	 */
	public static V3 getmR(V3 v,double r){
		double x = v.var1;
		double y = v.var2;
		double z = v.var3;
		double sum = x*x + y*y + z*z;
		double k = Math.sqrt(sum/(r*r));
		x = x/k;
		y = y/k;
		z = z/k;
		return new V3(x,y,z);
	}

	public static LatLng getPoint(V3 center,double acc,int i,double r,V3 half,V3 vector){
		V3 temp = getRotate(center,vector,(2*Math.PI)/acc*i);
//				mR = getvertical(center,temp,r);
		V3 mR = getmR(temp,r);
//			System.out.println(mR.var1+"%%%%%%%"+mR.var2+"%%%%%%%%"+mR.var3);
		mR = V3.add(mR,half);
		LatLng lat = changexyzToLatLng(mR);
		return lat;
	}

	/**
	 * 获取与输入坐标距离为r并且与x轴的夹角为arg的坐标
	 * @param in 输入坐标
	 * @param r	距离
	 * @param arg	角度
	 * @return
	 */
	public static LatLng getNextLatlng(LatLng in,double r,double arg){
		r = R*Math.sin(r/ R);
		V3 center = Maptools.changeToXyz(in);
		Double rate =  Math.sqrt(R*R-r*r)/R;
		V3 half = new Maptools.V3(rate*center.var1,rate*center.var2,rate*center.var3);
		V3 vector = new V3(1.0,-1*center.var1/center.var2,0.0);
		V3 temp = getRotate(center,vector,arg);
		temp = getmR(temp,r);
		temp = V3.add(center,temp);
		LatLng out = changexyzToLatLng(temp);
		return out;
	}

	/**
	 * 获取string字长 中文字长为2 英文字长为1
	 * @param str
	 * @return
	 */
	public static int getTextWidth( String str) {
		int iRet = 0;
		if (str != null && str.length() > 0) {
			int len = str.length();
			float[] widths = new float[len];
			Paint p = new Paint();
			p.setTextSize(2);
			p.getTextWidths(str, widths);
			for (int j = 0; j < len; j++) {
				iRet += (int) Math.ceil(widths[j]);
			}
		}
		return iRet;
	}
}

