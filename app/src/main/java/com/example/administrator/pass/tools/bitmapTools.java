package com.example.administrator.pass.tools;

import android.graphics.Bitmap;
import android.graphics.Matrix;
/**
 * Created by Administrator on 11/19 0019.
 */

public class bitmapTools {
	/**
	 * 图片比例变换
	 * @param bitmap
	 * @param var1 长
	 * @param var2 宽
	 * @return
	 */
	public static Bitmap postScale(Bitmap bitmap, float var1, float var2) {

		Matrix matrix = new Matrix();
		matrix.postScale(var1,var2); //长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
		return resizeBmp;
	}

}
