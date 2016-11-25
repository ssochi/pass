package com.example.administrator.pass.tools;

/**三维向量类 处理空间向量问题 提供平面求垂直向量 向量旋转 向量求膜 向量转单位向量等函数
 * Created by ssochi on 11/25 0025.
 */

public class Vector3 {
	double x;
	double y;
	double z;
	Vector3(double x,double y,double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * 向量求和
	 * @param in
	 * @return
	 */
	public Vector3 add(Vector3 in){
		return new Vector3(x+in.x,y+in.y,z+in.z);
	}

	/**
	 * 向量求和
	 * @param in1
	 * @param in2
	 * @return
	 */
	public Vector3 add(Vector3 in1,Vector3 in2){
		double x = in1.x + in2.x;
		double y = in1.y + in2.y;
		double z = in1.z + in2.z;
		return new Vector3(x,y,z);
	}

	/**
	 * 向量求差
	 * @param in
	 * @return
	 */
	public Vector3 sub(Vector3 in){
		return new Vector3(x-in.x,y-in.y,z-in.z);
	}

	/**
	 * 向量求差
	 * @param in1
	 * @param in2
	 * @return
	 */
	public Vector3 sub(Vector3 in1,Vector3 in2){
		double x = in1.x - in2.x;
		double y = in1.y - in2.y;
		double z = in1.z - in2.z;
		return new Vector3(x,y,z);
	}
	/**
	 * 获得方向相同膜为r的向量
	 * @param v
	 * @param r
	 * @return
	 */
	public static Vector3 getmR(Vector3 v,double r){
		double x = v.x;
		double y = v.y;
		double z = v.z;
		double sum = x*x + y*y + z*z;
		double k = Math.sqrt(sum/(r*r));
		x = x/k;
		y = y/k;
		z = z/k;
		return new Vector3(x,y,z);
	}
	/**
	 * 获得两个点的距离
	 * @param in1
	 * @param in2
	 * @return distance
	 */
	public static double getDistance(Vector3 in1,Vector3 in2){
		double distance = Math.sqrt(Math.pow((in1.x-in2.x),2)+Math.pow((in1.y-in2.y),2)+Math.pow((in1.z-in2.z),2));
		return distance;
	}
	/**
	 * 求与v1,v2向量垂直的向量 r是返回向量的膜
	 * @param v1
	 * @param v2
	 * @param r
	 * @return
	 */
	public static Vector3 getvertical(Vector3 v1, Vector3 v2, Double r){
		Double ax = v1.x;
		Double ay = v1.y;
		Double az = v1.z;
		Double bx = v2.x;
		Double by = v2.y;
		Double bz = v2.z;
		double x = ay*bz - az*by;
		double y = az*bx - ax*bz;
		double z = ax*by - ay*bx;
		Vector3 target = new Vector3(x,y,z);
		target = Vector3.getmR(target, r);
		return target;
	}

	/**
	 * 向量旋转 center是旋转轴 vector旋转向量 ang旋转角度
	 * @param center
	 * @param vector
	 * @param ang
	 * @return
	 */
	public static Vector3 getRotate(Vector3 center, Vector3 vector, Double ang){
		center = getmR(center,1.0);
		Double x = center.x;
		Double y = center.y;
		Double z = center.z;
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
		T[0][0] = vector.x;T[1][0] =vector.y;T[2][0] =vector.z;
		t.init(T);
		m = m.Multiply(t);
		m = m.Trans();
//		m.Output();
		return new Vector3(m.mat[0][0],m.mat[0][1],m.mat[0][2]);
	}

	/**
	 * 向量求膜
	 * @param v
	 * @return
	 */
	public static double getmodulus(Vector3 v){
		return Math.sqrt(Math.pow(v.x,2)+Math.pow(v.y,2)+Math.pow(v.z,2));
	}



}
