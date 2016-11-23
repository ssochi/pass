package com.example.administrator.pass.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

public class DisplayUtil {
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static point getScreenSizeOfDevice2(Resources res, Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Point point = new Point();
            wm.getDefaultDisplay().getRealSize(point);
            DisplayMetrics dm = res.getDisplayMetrics();
            double x = Math.pow(point.x / dm.xdpi, 2);
            double y = Math.pow(point.y / dm.ydpi, 2);
            double screenInches = Math.sqrt(x + y);
            Log.d(TAG, "Screen inches : " + screenInches);
            return new point(Math.sqrt(x), Math.sqrt(y));
        }
        else {
            System.err.println("sdk太低了");
            return new point(0,0);

        }

    }
    public static point getScreenSizeOfDevice1(Resources res, Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        float SCREEN_WIDTH = dm.widthPixels;
        float SCREEN_HEIGHT = dm.heightPixels;
        return  new point(SCREEN_WIDTH,SCREEN_HEIGHT);
    }


} 