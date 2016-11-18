package com.example.administrator.pass.tools;

import com.baidu.mapapi.map.Overlay;

/**
 * Created by Administrator on 11/17 0017.
 */

public class markerState  {
	public boolean hasClick ;
	public String information;
	public Overlay marker;
	public Overlay line;
	public markerState(boolean hasClick, String information, Overlay marker){
		this.hasClick = hasClick;
		this.information = information;
		this.marker = marker;
	}

	public void setLine(Overlay line) {
		this.line = line;
	}

	public Overlay getLine() {
		return line;
	}
}
